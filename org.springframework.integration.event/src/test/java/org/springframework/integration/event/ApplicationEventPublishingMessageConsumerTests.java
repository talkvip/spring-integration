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

package org.springframework.integration.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.message.Message;
import org.springframework.integration.message.StringMessage;

/**
 * @author Mark Fisher
 */
public class ApplicationEventPublishingMessageConsumerTests {

	@Test
	@SuppressWarnings("unchecked")
	public void testSendingEvent() throws InterruptedException {
		TestApplicationEventPublisher publisher = new TestApplicationEventPublisher();
		ApplicationEventPublishingMessageConsumer consumer = new ApplicationEventPublishingMessageConsumer();
		consumer.setApplicationEventPublisher(publisher);
		assertNull(publisher.getLastEvent());
		Message<?> message = new StringMessage("testing");
		consumer.onMessage(message);
		ApplicationEvent event = publisher.getLastEvent();
		assertEquals(MessagingEvent.class, event.getClass());
		assertEquals(message, ((MessagingEvent) event).getMessage());
	}


	private static class TestApplicationEventPublisher implements ApplicationEventPublisher {

		private volatile ApplicationEvent lastEvent;

		public ApplicationEvent getLastEvent() {
			return this.lastEvent;
		}

		public void publishEvent(ApplicationEvent event) {
			this.lastEvent = event;
		}
	}

}