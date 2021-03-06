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

import java.util.List;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;

/**
 * Base parser for routers that create instances that are subclasses of AbstractChannelNameResolvingMessageRouter.
 * 
 * @author Mark Fisher
 */
public abstract class AbstractChannelNameResolvingRouterParser extends AbstractRouterParser {

	@Override
	protected final BeanDefinition parseRouter(Element element, ParserContext parserContext) {
		BeanDefinition beanDefinition = this.doParseRouter(element, parserContext);
		if (beanDefinition != null) {
			// check if mapping is provided otherwise returned values will be treated as channel names
			List<Element> childElements = DomUtils.getChildElementsByTagName(element, "mapping");
			if (childElements != null && childElements.size() > 0) {
				BeanDefinitionBuilder channelResolverBuilder = BeanDefinitionBuilder.genericBeanDefinition(
						IntegrationNamespaceUtils.BASE_PACKAGE + ".channel.MapBasedChannelResolver");
				ManagedMap<String, RuntimeBeanReference> channelMap = new ManagedMap<String, RuntimeBeanReference>();
				for (Element childElement : childElements) {
					channelMap.put(childElement.getAttribute("value"),
							new RuntimeBeanReference(childElement.getAttribute("channel")));
				}
				channelResolverBuilder.addPropertyValue("channelMap", channelMap);
				beanDefinition.getPropertyValues().add("channelResolver", channelResolverBuilder.getBeanDefinition());
			}
		}
		return beanDefinition;
	}

	protected abstract BeanDefinition doParseRouter(Element element, ParserContext parserContext);

}
