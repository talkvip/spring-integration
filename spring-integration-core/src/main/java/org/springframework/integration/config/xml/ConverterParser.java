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

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Zhurakousky
 * @since 2.0
 */
public class ConverterParser extends AbstractBeanDefinitionParser {
	private final ManagedSet<Object> converters = new ManagedSet<Object>();
	private boolean notInitialized = true;
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		if (notInitialized){
			this.initializeConversionServiceInfrustructure(parserContext);
		}
		BeanComponentDefinition converterDefinition = IntegrationNamespaceUtils.parseInnerHandlerDefinition(element, parserContext);
		if (converterDefinition == null){
			String beanName = element.getAttribute("ref");
			Assert.isTrue(StringUtils.hasText(beanName), "'ref' attribute pointing to a Converter definition or sub-element <bean> converter definition must be provided");
			converters.add(new RuntimeBeanReference(beanName));
		} else{
			converters.add(converterDefinition);
		}
		return null;
	}
	/*
	 * 
	 */
	private void initializeConversionServiceInfrustructure(ParserContext parserContext){
		String contextPackage = "org.springframework.integration.context.";
		BeanDefinitionBuilder creatorBuilder = BeanDefinitionBuilder.rootBeanDefinition(contextPackage + "ConversionServiceCreator");
		BeanDefinitionReaderUtils.registerWithGeneratedName(creatorBuilder.getBeanDefinition(), parserContext.getRegistry());
		
		BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder.rootBeanDefinition(contextPackage + "ConverterRegistrar");
		conversionServiceBuilder.addConstructorArgValue(converters);
		BeanDefinitionReaderUtils.registerWithGeneratedName(conversionServiceBuilder.getBeanDefinition(), parserContext.getRegistry());
		notInitialized = false;
	}
}
