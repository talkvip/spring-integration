/*
 * Copyright 2002-2010 the original author or authors.
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
package org.springframework.integration.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.integration.MessageChannel;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.support.MetricType;

/**
 * @author Dave Syer
 * 
 * @since 2.0
 * 
 */
public class PollableChannelMonitor extends DirectChannelMonitor {

	private final AtomicInteger receiveCount = new AtomicInteger();

	private final AtomicInteger receiveErrorCount = new AtomicInteger();

	/**
	 * @param name
	 */
	public PollableChannelMonitor(String name) {
		super(name);
	}

	@Override
	protected Object doInvoke(MethodInvocation invocation, String method, MessageChannel channel) throws Throwable {
		if ("receive".equals(method)) {
			return monitorReceive(invocation, channel);
		}
		return super.doInvoke(invocation, method, channel);
	}

	private Object monitorReceive(MethodInvocation invocation, MessageChannel channel) throws Throwable {
		if (logger.isTraceEnabled()) {
			logger.trace("Recording receive on channel(" + channel + ") ");
		}
		try {
			Object object = invocation.proceed();
			if (object!=null) {
				receiveCount.incrementAndGet();
			}
			return object;

		}
		catch (Throwable e) {
			receiveErrorCount.incrementAndGet();
			throw e;
		}
	}

	@ManagedMetric(metricType = MetricType.COUNTER, displayName = "MessageChannel Receives")
	public int getReceiveCount() {
		return receiveCount.get();
	}

	@ManagedMetric(metricType = MetricType.COUNTER, displayName = "MessageChannel Receive Errors")
	public int getReceiveErrorCount() {
		return receiveErrorCount.get();
	}

	@Override
	public String toString() {
		return String.format("MessageChannelMonitor: [name=%s, sends=%d, receives=%d]", getName(), getSendCount(),
				receiveCount.get());
	}

}
