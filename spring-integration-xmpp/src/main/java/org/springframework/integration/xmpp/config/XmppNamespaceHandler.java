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

package org.springframework.integration.xmpp.config;

import org.jivesoftware.smack.packet.Presence;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.HeaderEnricherParserSupport;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.integration.xmpp.XmppHeaders;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * This class parses the schema for XMPP support.
 *
 * @author Josh Long
 * @author Mark Fisher
 * @since 2.0
 */
public class XmppNamespaceHandler extends NamespaceHandlerSupport {

	private static final String PACKAGE_NAME = "org.springframework.integration.xmpp";

	private static String[] attributes = new String[]{
			"user", "password", "host", "service-name", "resource",
			"sasl-mechanism-supported", "sasl-mechanism-supported-index", "port", "subscription-mode"
	};


	public void init() {
		// connection
		registerBeanDefinitionParser("xmpp-connection", new XmppConnectionParser());

		// send/receive messages
		registerBeanDefinitionParser("message-inbound-channel-adapter", new XmppMessageInboundEndpointParser());
		registerBeanDefinitionParser("message-outbound-channel-adapter", new XmppMessageOutboundEndpointParser());

		// presence
		registerBeanDefinitionParser("roster-event-inbound-channel-adapter", new XmppRosterEventInboundEndpointParser());
		registerBeanDefinitionParser("roster-event-outbound-channel-adapter", new XmppRosterEventOutboundEndpointParser());

		registerBeanDefinitionParser("header-enricher", new XmppHeaderEnricherParser());
	}


	private static void configureXMPPConnection(Element element, BeanDefinitionBuilder builder, ParserContext parserContext) {
		String ref = element.getAttribute("xmpp-connection");
		if (StringUtils.hasText(ref)) {
			builder.addPropertyReference("xmppConnection", ref);
		} else {
			for (String attribute : attributes) {
				IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, attribute);
			}
		}
	}


	// connection management

	private static class XmppConnectionParser extends AbstractSingleBeanDefinitionParser {

		@Override
		protected String getBeanClassName(Element element) {
			return PACKAGE_NAME + ".XmppConnectionFactory";
		}

		@Override
		protected boolean shouldGenerateIdAsFallback() {
			return true;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			configureXMPPConnection(element, builder, parserContext);
		}
	}

	// messages

	private static class XmppMessageOutboundEndpointParser extends AbstractOutboundChannelAdapterParser {

		@Override
		protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
					PACKAGE_NAME + ".messages.XmppMessageSendingMessageHandler");
			configureXMPPConnection(element, builder, parserContext);
			return builder.getBeanDefinition();
		}
	}

	private static class XmppMessageInboundEndpointParser extends AbstractSingleBeanDefinitionParser {

		@Override
		protected String getBeanClassName(Element element) {
			return PACKAGE_NAME + ".messages.XmppMessageDrivenEndpoint";
		}

		@Override
		protected boolean shouldGenerateIdAsFallback() {
			return true;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			configureXMPPConnection(element, builder, parserContext);
			IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "requestChannel");
		}
	}

	private static class XmppRosterEventOutboundEndpointParser extends AbstractOutboundChannelAdapterParser {

		@Override
		protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
					PACKAGE_NAME + ".presence.XmppRosterEventMessageSendingHandler");
			configureXMPPConnection(element, builder, parserContext);
			return builder.getBeanDefinition();
		}
	}

	private static class XmppRosterEventInboundEndpointParser extends AbstractSingleBeanDefinitionParser {

		@Override
		protected String getBeanClassName(Element element) {
			return PACKAGE_NAME + ".presence.XmppRosterEventMessageDrivenEndpoint";
		}

		@Override
		protected boolean shouldGenerateIdAsFallback() {
			return true;
		}

		@Override
		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			configureXMPPConnection(element, builder, parserContext);
			IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "requestChannel");
		}
	}

	private static class XmppHeaderEnricherParser extends HeaderEnricherParserSupport {

		public XmppHeaderEnricherParser() {

			// chat headers
			this.addElementToHeaderMapping("message-to", XmppHeaders.CHAT_TO_USER);
			this.addElementToHeaderMapping("message-thread-id", XmppHeaders.CHAT_THREAD_ID);

			// presence headers
			this.addElementToHeaderMapping("presence-mode", XmppHeaders.PRESENCE_MODE, Presence.Mode.class);
			this.addElementToHeaderMapping("presence-type", XmppHeaders.PRESENCE_TYPE, Presence.Type.class);
			this.addElementToHeaderMapping("presence-from", XmppHeaders.PRESENCE_FROM);
			this.addElementToHeaderMapping("presence-status", XmppHeaders.PRESENCE_STATUS);
			this.addElementToHeaderMapping("presence-priority", XmppHeaders.PRESENCE_PRIORITY, Integer.class);
		}
	}

}
