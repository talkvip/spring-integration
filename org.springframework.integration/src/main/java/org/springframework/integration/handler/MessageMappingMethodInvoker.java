/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MethodParameterMessageMapper;
import org.springframework.integration.message.OutboundMessageMapper;
import org.springframework.integration.util.DefaultMethodInvoker;
import org.springframework.integration.util.DefaultMethodResolver;
import org.springframework.integration.util.MethodInvoker;
import org.springframework.integration.util.MethodResolver;
import org.springframework.integration.util.NameResolvingMethodInvoker;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A base or helper class for any Messaging component that acts as an adapter
 * by invoking a "plain" (not Message-aware) method on a given target object.
 * The target Object is mandatory, and either a {@link Method} reference, a
 * 'methodName', or an Annotation type must be provided.
 * 
 * @author Mark Fisher
 */
public class MessageMappingMethodInvoker implements MethodInvoker, InitializingBean {

	protected static final Log logger = LogFactory.getLog(MessageMappingMethodInvoker.class);

	private volatile boolean methodExpectsMessage;

	private volatile Object object;

	private volatile Method method;

	private volatile String methodName;

	private volatile OutboundMessageMapper<Object[]> messageMapper;

	private volatile MethodInvoker invoker;

	private volatile boolean initialized;

	private final Object initializationMonitor = new Object();


	public MessageMappingMethodInvoker(Object object, Method method) {
		Assert.notNull(object, "object must not be null");
		Assert.notNull(method, "method must not be null");
		this.object = object;
		this.method = method;
		this.methodName = method.getName();		
	}

	public MessageMappingMethodInvoker(Object object, Class<? extends Annotation> annotationType) {
		Assert.notNull(object, "object must not be null");
		Assert.notNull(annotationType, "annotation type must not be null");
		this.object = object;
		MethodResolver methodResolver = new DefaultMethodResolver(annotationType);
		this.method = methodResolver.findMethod(object);
		Assert.notNull(method, "unable to resolve method for annotation [" + annotationType.getSimpleName()
				+ "] on target class [" + object.getClass() + "]");
	}

	public MessageMappingMethodInvoker(Object object, String methodName) {
		Assert.notNull(object, "object must not be null");
		Assert.notNull(methodName, "methodName must not be null");
		this.object = object;
		this.methodName = methodName;
	}


	public void afterPropertiesSet() {
		synchronized (this.initializationMonitor) {
			if (this.initialized) {
				return;
			}
			if (this.method == null) {
				final List<Method> candidates = new ArrayList<Method>();
				ReflectionUtils.doWithMethods(this.object.getClass(), new ReflectionUtils.MethodCallback() {
					public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
						if (method.getName().equals(MessageMappingMethodInvoker.this.methodName)) {
							candidates.add(method);
						}
					}
				});
				if (candidates.size() == 0) {
					throw new IllegalArgumentException("unable to find a candidate method named '"
							+ this.methodName + "'" + " on target class [" + this.object.getClass() + "]"); 
				}
				else if (candidates.size() == 1) {
					this.method = candidates.get(0);
				}
			}
			if (this.method != null) {
				Class<?>[] parameterTypes = this.method.getParameterTypes();
				Assert.isTrue(parameterTypes.length > 0, "method must accept at least one parameter");
				if (parameterTypes.length == 1 && Message.class.isAssignableFrom(parameterTypes[0])) {
					this.methodExpectsMessage = true;
				}
				this.invoker = new DefaultMethodInvoker(this.object, this.method);
				this.messageMapper = new MethodParameterMessageMapper(this.method);
			}
			else {
				// TODO: resolve the candidate method and/or create a dynamic resolver
				this.invoker = new NameResolvingMethodInvoker(this.object, this.methodName);
			}
			this.initialized = true;
		}
	}

	public Object invokeMethod(Object... args) {
		if (!this.initialized) {
			this.afterPropertiesSet();
		}
		Assert.notEmpty(args, "argument array must not be empty");
		Message<?> message = null;
		if (args.length == 1 && args[0] != null && (args[0] instanceof Message)) {
			message = (Message<?>) args[0];
			if (message.getPayload() == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("received null payload");
				}
				return null;
			}
			args = this.createArgumentArrayFromMessage(message);
		}
		try {
			return this.doInvokeMethod(args, message);
		}
		catch (InvocationTargetException e) {
			if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw new MessageHandlingException(message,
					"method '" + this.methodName + "' threw an Exception.", e.getCause());
		}
		catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new MessageHandlingException(message, "Failed to invoke method '"
					+ this.methodName + "' with arguments: " + ObjectUtils.nullSafeToString(args), e);
		}
	}

	private Object doInvokeMethod(Object[] args, Message<?> message) throws Exception {
		Object result = null;
		try {
			result = this.invoker.invokeMethod(args);
		}
		catch (NoSuchMethodException e) {
			try {
				if (message != null) {
					result = this.invoker.invokeMethod(message);
					this.methodExpectsMessage = true;
				}
			}
			catch (NoSuchMethodException e2) {
				throw new MessageHandlingException(message, "unable to resolve method for args: "
						+ StringUtils.arrayToCommaDelimitedString(args));
			}
		}
		return result;
	}

	private Object[] createArgumentArrayFromMessage(Message<?> message) {
		Object args[] = null;
		Object mappingResult = this.methodExpectsMessage
				? message : this.resolveParameters(message);
		if (mappingResult != null && mappingResult.getClass().isArray()
				&& (Object.class.isAssignableFrom(mappingResult.getClass().getComponentType()))) {
			args = (Object[]) mappingResult;
		}
		else {
			args = new Object[] { mappingResult }; 
		}
		return args;
	}

	private Object[] resolveParameters(Message<?> message) {
		if (this.messageMapper != null) {
			return this.messageMapper.fromMessage(message);
		}
		return new Object[] { message.getPayload() };
	}

	public String toString() {
		return "invoker:" + this.object + "." + this.methodName;
	}

}