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

package org.springframework.integration.xml.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.xml.DefaultXmlPayloadConverter;
import org.springframework.integration.xml.XmlPayloadConverter;
import org.springframework.xml.xpath.XPathExpression;
import org.w3c.dom.Node;

/**
 * Router that evaluates the payload using {@link XPathExpression#evaluateAsString(Node)}
 * to extract a channel name. The payload is extracted as a node using the
 * provided {@link XmlPayloadConverter} with {@link DefaultXmlPayloadConverter}
 * being the default.
 * 
 * <p>The provided {@link XPathExpression} must evaluate to a non-empty String.
 * 
 * @author Jonas Partner
 */
public class XPathSingleChannelRouter extends AbstractXPathRouter  {

	/**
	 * @see AbstractXPathRouter#AbstractXPathRouter(String, Map)
	 */
	public XPathSingleChannelRouter(String expression, Map<String, String> namespaces) {
		super(expression, namespaces);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathRouter(String, String, String)
	 */
	public XPathSingleChannelRouter(String expression, String prefix, String namespace) {
		super(expression, prefix, namespace);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathRouter(String)
	 */
	public XPathSingleChannelRouter(String expression) {
		super(expression);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathRouter(XPathExpression)
	 */
	public XPathSingleChannelRouter(XPathExpression expression) {
		super(expression);
	}


	/**
	 * Evaluates the payload using {@link XPathExpression#evaluateAsString(Node)}
	 * 
	 * @throws MessagingException if the {@link XPathExpression} evaluates to
	 * an empty string
	 */
	
	@Override
	protected List<Object> getChannelIndicatorList(Message<?> message) {
		List<Object> channels = new ArrayList<Object>();
		Node node = getConverter().convertToNode(message.getPayload());
		String result = getXPathExpression().evaluateAsString(node);
		if (result == null || "".equals(result)) {
			return null;
		} else {
			channels.add(result);
		}
		
		return channels;
	
	}

}
