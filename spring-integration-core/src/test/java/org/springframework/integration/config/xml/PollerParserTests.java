/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.integration.config.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.config.TestTrigger;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.scheduling.PollerMetadata;

/**
 * @author Mark Fisher
 */
public class PollerParserTests {

	@Test
	public void defaultPollerWithId() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"defaultPollerWithId.xml", PollerParserTests.class);
		Object poller = context.getBean("defaultPollerWithId");
		assertNotNull(poller);
		Object defaultPoller = context.getBean(IntegrationContextUtils.DEFAULT_POLLER_METADATA_BEAN_NAME);
		assertNotNull(defaultPoller);
		assertEquals(defaultPoller, context.getBean("defaultPollerWithId"));
	}

	@Test
	public void defaultPollerWithoutId() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"defaultPollerWithoutId.xml", PollerParserTests.class);
		Object defaultPoller = context.getBean(IntegrationContextUtils.DEFAULT_POLLER_METADATA_BEAN_NAME);
		assertNotNull(defaultPoller);
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void multipleDefaultPollers() {
		new ClassPathXmlApplicationContext(
				"multipleDefaultPollers.xml", PollerParserTests.class);
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void topLevelPollerWithoutId() {
		new ClassPathXmlApplicationContext(
				"topLevelPollerWithoutId.xml", PollerParserTests.class);
	}

	@Test
	public void pollerWithAdviceChain() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"pollerWithAdviceChain.xml", PollerParserTests.class);
		Object poller = context.getBean("poller");
		assertNotNull(poller);
		PollerMetadata metadata = (PollerMetadata) poller;
		assertNotNull(metadata.getAdviceChain());
		assertEquals(3, metadata.getAdviceChain().size());
		assertEquals(context.getBean("adviceBean1"), metadata.getAdviceChain().get(0));
		assertEquals(TestAdviceBean.class, metadata.getAdviceChain().get(1).getClass());
		assertEquals(2, ((TestAdviceBean) metadata.getAdviceChain().get(1)).getId());
		assertEquals(context.getBean("adviceBean3"), metadata.getAdviceChain().get(2));
	}

	@Test
	public void pollerWithReceiveTimeout() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"pollerWithReceiveTimeout.xml", PollerParserTests.class);
		Object poller = context.getBean("poller");
		assertNotNull(poller);
		PollerMetadata metadata = (PollerMetadata) poller;
		assertEquals(1234, metadata.getReceiveTimeout());
	}

    @Test
	public void pollerWithTriggerReference() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"pollerWithTriggerReference.xml", PollerParserTests.class);
		Object poller = context.getBean("poller");
		assertNotNull(poller);
		PollerMetadata metadata = (PollerMetadata) poller;
		assertTrue(metadata.getTrigger() instanceof TestTrigger);
	}

}
