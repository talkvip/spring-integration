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

package org.springframework.integration.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.MessageChannel;
import org.springframework.integration.channel.PollableChannel;
import org.springframework.integration.message.Message;
import org.springframework.integration.message.StringMessage;
import org.springframework.integration.message.selector.MessageSelector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

/**
 * @author Mark Fisher
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FilterParserTests {

	@Autowired @Qualifier("adapterInput")
	MessageChannel adapterInput;

	@Autowired @Qualifier("adapterOutput")
	PollableChannel adapterOutput;

	@Autowired @Qualifier("implementationInput")
	MessageChannel implementationInput;

	@Autowired @Qualifier("implementationOutput")
	PollableChannel implementationOutput;


	@Test
	public void filterWithSelectorAdapterAccepts() {
		adapterInput.send(new StringMessage("test"));
		Message<?> reply = adapterOutput.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void filterWithSelectorAdapterRejects() {
		adapterInput.send(new StringMessage(""));
		Message<?> reply = adapterOutput.receive(0);
		assertNull(reply);
	}

	@Test
	public void filterWithSelectorImplementationAccepts() {
		implementationInput.send(new StringMessage("test"));
		Message<?> reply = implementationOutput.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void filterWithSelectorImplementationRejects() {
		implementationInput.send(new StringMessage(""));
		Message<?> reply = implementationOutput.receive(0);
		assertNull(reply);
	}


	public static class TestSelectorBean {

		public boolean hasText(String s) {
			return StringUtils.hasText(s);
		}
	}


	public static class TestSelectorImpl implements MessageSelector {

		public boolean accept(Message<?> message) {
			if (message != null && message.getPayload() instanceof String) {
				return StringUtils.hasText((String) message.getPayload());
			}
			return false;
		}
	}

}