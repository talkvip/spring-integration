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
package org.springframework.integration.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;

/**
 * 
 * @author Jonas Partner
 * 
 */
public class ErrorHandlingExecutorBeanPostProcessorTests {

	StubErrorHandler errorHandler;

	ApplicationContext applicationContext;

	@Before
	public void setUp() {
		applicationContext = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());
		errorHandler = (StubErrorHandler) applicationContext.getBean("stubErrorHandler");
	}

	@Test
	public void testProxiedTaskExecutor() throws Exception {
		errorHandler.latch = new CountDownLatch(1);
		TaskExecutor taskExecutor = (TaskExecutor) applicationContext.getBean("proxiedTaskExecutor");
		assertTrue("Task executor was not proxied ", AopUtils.isAopProxy(taskExecutor));
		ErrorThrowingRunnable runnable = new ErrorThrowingRunnable();
		taskExecutor.execute(runnable);
		assertTrue("Runnable faield to run", errorHandler.latch.await(5, TimeUnit.SECONDS));
		assertEquals("Incorrect count of exceptions", 1, errorHandler.throwables.size());
	}

	@Test
	public void testExcludedFromProxyingTaskExecutor() throws Exception {
		TaskExecutor taskExecutor = (TaskExecutor) applicationContext.getBean("excludeFromProxyingTaskExecutor");
		assertFalse("Excluded task executor was proxied ", AopUtils.isAopProxy(taskExecutor));
	}

	private static class ErrorThrowingRunnable implements Runnable {

		public void run() {
			throw new RuntimeException();
		}

	}
}