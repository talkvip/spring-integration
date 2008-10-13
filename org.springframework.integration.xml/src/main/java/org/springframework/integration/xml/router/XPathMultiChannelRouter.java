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

import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import org.springframework.integration.message.Message;
import org.springframework.integration.xml.XmlPayloadConverter;
import org.springframework.util.Assert;
import org.springframework.xml.xpath.NodeMapper;
import org.springframework.xml.xpath.XPathExpression;

/**
 * A router that evaluates the XPath expression using
 * {@link XPathExpression#evaluateAsNodeList(Node)} which returns zero or more
 * nodes in conjunction with an instance of {@link NodeMapper} to produce zero
 * or more channel names. An instance of {@link XmlPayloadConverter} is used to
 * extract the payload as a {@link Node}.
 * 
 * @author Jonas Partner
 */
public class XPathMultiChannelRouter extends AbstractXPathRouter {

	private volatile NodeMapper nodeMapper = new TextContentNodeMapper();


	/**
	 * @see AbstractXPathRouter#AbstractXPathChannelNameResolver(String, Map)
	 */
	public XPathMultiChannelRouter(String expression, Map<String, String> namespaces) {
		super(expression, namespaces);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathChannelNameResolver(String, String, String)
	 */
	public XPathMultiChannelRouter(String expression, String prefix, String namespace) {
		super(expression, prefix, namespace);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathChannelNameResolver(String)
	 */
	public XPathMultiChannelRouter(String expression) {
		super(expression);
	}

	/**
	 * @see AbstractXPathRouter#AbstractXPathChannelNameResolver(XPathExpression)
	 */
	public XPathMultiChannelRouter(XPathExpression expression) {
		super(expression);
	}


	public void setNodeMapper(NodeMapper nodeMapper) {
		Assert.notNull(nodeMapper, "NodeMapper must not be null");
		this.nodeMapper = nodeMapper;
	}

	@SuppressWarnings("unchecked")
	public String[] determineTargetChannelNames(Message<?> message) {
		Node node = getConverter().convertToNode(message.getPayload());
		List channelNamesList = getXPathExpression().evaluate(node, this.nodeMapper);
		return (String[]) channelNamesList.toArray(new String[channelNamesList.size()]);
	}


	private static class TextContentNodeMapper implements NodeMapper {

		public Object mapNode(Node node, int nodeNum) throws DOMException {
			return node.getTextContent();
		}

	}

}