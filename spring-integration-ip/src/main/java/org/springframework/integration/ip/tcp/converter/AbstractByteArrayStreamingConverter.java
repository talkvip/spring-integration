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

package org.springframework.integration.ip.tcp.converter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.commons.serializer.InputStreamingConverter;
import org.springframework.commons.serializer.OutputStreamingConverter;

/**
 * Base class for streaming converters that convert to/from a byte array.
 * 
 * @author Gary Russell
 * @since 2.0
 *
 */
public abstract class AbstractByteArrayStreamingConverter implements
		InputStreamingConverter<byte[]>, 
		OutputStreamingConverter<byte[]> {

	protected int maxMessageSize = 2048;
	
	protected Log logger = LogFactory.getLog(this.getClass());

	/**
	 * The maximum supported message size for this converter.
	 * Default 2048.
	 * @return The max message size.
	 */
	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	/**
	 * The maximum supported message size for this converter.
	 * Default 2048.
	 * @param maxMessageSize The max message size.
	 */
	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	protected void checkClosure(int bite) throws IOException {
		if (bite < 0) {
			logger.debug("Socket closed");				
			throw new IOException("Socket closed");
		}
	}

}
