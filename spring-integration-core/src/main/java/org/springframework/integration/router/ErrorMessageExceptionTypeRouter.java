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

package org.springframework.integration.router;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.util.Assert;

/**
 * A Message Router that resolves the target {@link MessageChannel} for
 * messages whose payload is an Exception. The channel resolution is based upon
 * the most specific cause of the error for which a channel-mapping exists.
 * 
 * @author Mark Fisher
 */
public class ErrorMessageExceptionTypeRouter extends AbstractSingleChannelRouter {

	private volatile Map<Class<? extends Throwable>, MessageChannel> exceptionTypeChannelMap =
			new ConcurrentHashMap<Class<? extends Throwable>, MessageChannel>();


	public void setExceptionTypeChannelMap(Map<Class<? extends Throwable>, MessageChannel> exceptionTypeChannelMap) {
		Assert.notNull(exceptionTypeChannelMap, "exceptionTypeChannelMap must not be null");
		this.exceptionTypeChannelMap = exceptionTypeChannelMap;
	}


	@Override
	protected MessageChannel determineTargetChannel(Message<?> message) {
		MessageChannel channel = null;
		Object payload = message.getPayload();
		if (payload != null && (payload instanceof Throwable)) {
			Throwable mostSpecificCause = (Throwable) payload;
			while (mostSpecificCause != null) {
				MessageChannel mappedChannel = this.exceptionTypeChannelMap.get(mostSpecificCause.getClass());
				if (mappedChannel != null) {
					channel = mappedChannel;
				}
				mostSpecificCause = mostSpecificCause.getCause();
			}
		}
		return channel;
	}

}
