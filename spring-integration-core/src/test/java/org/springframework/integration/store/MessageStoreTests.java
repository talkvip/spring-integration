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

package org.springframework.integration.store;

import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Dave Syer
 */
public class MessageStoreTests {


	@Test
	public void shouldRegisterCallbacks() throws Exception {
		TestMessageStore store = new TestMessageStore();
		store.setExpiryCallbacks(Arrays.<MessageGroupCallback>asList(new MessageGroupCallback() {
			public void execute(MessageGroupStore messageGroupStore, MessageGroup group) {
			}
		}));
		assertEquals(1, ((Collection<?>)ReflectionTestUtils.getField(store, "expiryCallbacks")).size());
	}

	@Test
	public void shouldExpireMessageGroup() throws Exception {

		TestMessageStore store = new TestMessageStore();
		final List<String> list = new ArrayList<String>();
		store.registerMessageGroupExpiryCallback(new MessageGroupCallback() {
			public void execute(MessageGroupStore messageGroupStore, MessageGroup group) {
				list.add(group.getOne().getPayload().toString());
				messageGroupStore.removeMessageGroup(group.getGroupId());
			}
		});

		store.expireMessageGroups(-10000);
		assertEquals("[foo]", list.toString());
		assertEquals(0, store.getMessageGroup("bar").size());

	}
	
	private static class TestMessageStore extends AbstractMessageGroupStore {

		MessageGroup testMessages = new SimpleMessageGroup(Arrays.asList(new GenericMessage<String>("foo")), "bar");
		private boolean removed = false;

		@Override
		public Iterator<MessageGroup> iterator() {
			return Arrays.asList(testMessages).iterator();
		}

		public MessageGroup addMessageToGroup(Object correlationKey, Message<?> message) {
			throw new UnsupportedOperationException();
		}

		public MessageGroup getMessageGroup(Object correlationKey) {
			return removed ? new SimpleMessageGroup(correlationKey) : testMessages;
		}

		public MessageGroup markMessageGroup(MessageGroup group) {
			throw new UnsupportedOperationException();
		}

		public MessageGroup removeMessageFromGroup(Object key, Message<?> messageToRemove) {
			throw new UnsupportedOperationException();
		}

		public MessageGroup markMessageFromGroup(Object key, Message<?> messageToMark) {
			throw new UnsupportedOperationException();
		}

		public void removeMessageGroup(Object correlationKey) {
			if (correlationKey.equals(testMessages.getGroupId())) {
				removed = true;
			}
		}
		
	}

}
