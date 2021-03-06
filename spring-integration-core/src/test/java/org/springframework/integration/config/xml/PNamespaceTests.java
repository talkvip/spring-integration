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

package org.springframework.integration.config.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Validates the "p:namespace" is working for inner "bean" definition within SI components.
 * 
 * @author Oleg Zhurakousky
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PNamespaceTests {

	@Autowired
	@Qualifier("sa")
	EventDrivenConsumer serviceActivator;

	@Autowired
	@Qualifier("sp")
	EventDrivenConsumer splitter;	

	@Autowired
	@Qualifier("rt")
	EventDrivenConsumer router;

	@Autowired
	@Qualifier("tr")
	EventDrivenConsumer transformer;


	@Test
	public void testPNamespaceServiceActivator() {	
		TestBean bean =  prepare(serviceActivator);
		assertEquals("paris", bean.getFname());
		assertEquals("hilton", bean.getLname());
	}

	@Test
	public void testPNamespaceSplitter() {		
		TestBean bean =  prepare(splitter);
		assertEquals("paris", bean.getFname());
		assertEquals("hilton", bean.getLname());
	}

	@Test
	public void testPNamespaceRouter() {
		TestBean bean =  prepare(router);
		assertEquals("paris", bean.getFname());
		assertEquals("hilton", bean.getLname());
	}

	@Test
	public void testPNamespaceTransformer() {		
		TestBean bean =  prepare(transformer);
		assertEquals("paris", bean.getFname());
		assertEquals("hilton", bean.getLname());
	}


	private TestBean prepare(EventDrivenConsumer edc) {
		return TestUtils.getPropertyValue(serviceActivator,
				"handler.processor.delegate.targetObject", TestBean.class);
	}


	public interface InboundGateway {
		public String echo();
	}


	public static class TestBean {

		private String fname;

		private String lname;

		public String getFname() {
			return fname;
		}

		public void setFname(String fname) {
			this.fname = fname;
		}

		public String getLname() {
			return lname;
		}

		public void setLname(String lname) {
			this.lname = lname;
		}
		
		public String printWithPrefix(String prefix) {
			return prefix + fname + " " + lname;
		}

		public String toString() {
			return fname + lname;
		}
	}

}
