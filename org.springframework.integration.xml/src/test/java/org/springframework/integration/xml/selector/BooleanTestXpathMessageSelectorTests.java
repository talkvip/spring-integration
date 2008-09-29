/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.integration.xml.selector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.message.StringMessage;
import org.springframework.integration.xml.util.XmlTestUtil;
import org.springframework.xml.xpath.XPathExpression;
import org.springframework.xml.xpath.XPathExpressionFactory;
import org.w3c.dom.Document;

/**
 * 
 * @author Jonas Partner
 *
 */
public class BooleanTestXpathMessageSelectorTests {
	
	@Test
	public void testWithSimpleString(){
		BooleanTestXPathMessageSelector selector = new BooleanTestXPathMessageSelector("boolean(/one/two)");
		assertTrue(selector.accept(new StringMessage("<one><two/></one>")) ) ;
		assertFalse(selector.accept(new StringMessage("<one><three/></one>")) ) ;
	}
	
	@Test
	public void testWithDocument() throws Exception{
		BooleanTestXPathMessageSelector selector = new BooleanTestXPathMessageSelector("boolean(/one/two)");
		assertTrue(selector.accept(new GenericMessage<Document>(XmlTestUtil.getDocumentForString("<one><two/></one>"))) ) ;
		assertFalse(selector.accept(new GenericMessage<Document>(XmlTestUtil.getDocumentForString("<one><three/></one>"))) ) ;
	}
	
	@Test
	public void testWithNamespace(){
		BooleanTestXPathMessageSelector selector = new BooleanTestXPathMessageSelector("boolean(/ns1:one/ns1:two)","ns1", "www.example.org");
		assertTrue(selector.accept(new StringMessage("<ns1:one xmlns:ns1='www.example.org'><ns1:two/></ns1:one>")) ) ;
		assertFalse(selector.accept(new StringMessage("<ns2:one xmlns:ns2='www.example2.org'><ns1:two xmlns:ns1='www.example.org' /></ns2:one>")) ) ;
	}
	
	@Test
	public void testWithXPathExpressionProvided(){
		XPathExpression xpathExpression = XPathExpressionFactory.createXPathExpression("boolean(/one/two)");
		BooleanTestXPathMessageSelector selector = new BooleanTestXPathMessageSelector(xpathExpression);
		assertTrue(selector.accept(new StringMessage("<one><two/></one>")) ) ;
		assertFalse(selector.accept(new StringMessage("<one><three/></one>")) ) ;
	}
	

}