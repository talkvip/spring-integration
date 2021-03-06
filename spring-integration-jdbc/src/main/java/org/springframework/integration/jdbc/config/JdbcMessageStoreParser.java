/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.integration.jdbc.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for {@link org.springframework.integration.jdbc.JdbcMessageStore}.
 * 
 * @author Dave Syer
 * @since 2.0
 */
public class JdbcMessageStoreParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition("org.springframework.integration.jdbc.JdbcMessageStore");

		String dataSourceRef = element.getAttribute("data-source");
		String simpleJdbcOperationsRef = element.getAttribute("jdbc-operations");
		boolean refToDataSourceSet = StringUtils.hasText(dataSourceRef);
		boolean refToSimpleJdbcOperaitonsSet = StringUtils.hasText(simpleJdbcOperationsRef);
		if ((refToDataSourceSet && refToSimpleJdbcOperaitonsSet)
				|| (!refToDataSourceSet && !refToSimpleJdbcOperaitonsSet)) {
			parserContext.getReaderContext().error(
					"Exactly one of the attributes data-source or "
							+ "simple-jdbc-operations should be set for the JDBC message-store", source);
		}

		if (refToDataSourceSet) {
			builder.addPropertyReference("dataSource", dataSourceRef);
		} else {
			builder.addPropertyReference("jdbcTemplate", simpleJdbcOperationsRef);
		}

		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "lob-handler");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "serializer");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "deserializer");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "table-prefix", "tablePrefix");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "region", "region");

		return builder.getBeanDefinition();

	}

}
