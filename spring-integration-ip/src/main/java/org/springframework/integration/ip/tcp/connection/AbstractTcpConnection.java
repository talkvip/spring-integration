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

package org.springframework.integration.ip.tcp.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.commons.serializer.InputStreamingConverter;
import org.springframework.commons.serializer.OutputStreamingConverter;
import org.springframework.integration.ip.tcp.converter.AbstractByteArrayStreamingConverter;
import org.springframework.util.Assert;

/**
 * Base class for TcpConnections. TcpConnections are established by
 * client connection factories (outgoing) or server connection factories
 * (incoming).
 * 
 * @author Gary Russell
 * @since 2.0
 *
 */
public abstract class AbstractTcpConnection implements TcpConnection {

	protected Log logger = LogFactory.getLog(this.getClass());
	
	@SuppressWarnings("rawtypes")
	protected InputStreamingConverter inputConverter;
	
	@SuppressWarnings("rawtypes")
	protected OutputStreamingConverter outputConverter;
	
	protected TcpMessageMapper mapper;
	
	protected TcpListener listener;

	protected TcpSender sender;

	protected boolean singleUse;

	protected final boolean server;

	protected String connectionId;
	
	public AbstractTcpConnection(boolean server) {
		this.server = server;
	}

	/**
	 * Closes this connection.
	 */
	public void close() {
		if (this.sender != null) {
			this.sender.removeDeadConnection(this);
		}
	}
	
	/**
	 * @return the mapper
	 */
	public TcpMessageMapper getMapper() {
		return mapper;
	}

	/**
	 * @param mapper the mapper to set
	 */
	public void setMapper(TcpMessageMapper mapper) {
		Assert.notNull(mapper, this.getClass().getName() + " Mapper may not be null");
		this.mapper = mapper;
		if (this.outputConverter != null && 
			 !(this.outputConverter instanceof AbstractByteArrayStreamingConverter)) {
			mapper.setStringToBytes(false);
		}
	}

	/**
	 * 
	 * @return the input converter
	 */
	public InputStreamingConverter<?> getInputConverter() {
		return inputConverter;
	}

	/**
	 * @param inputConverter the input converter to set
	 */
	public void setInputConverter(InputStreamingConverter<?> inputConverter) {
		this.inputConverter = inputConverter;
	}

	/**
	 * 
	 * @return the output converter
	 */
	public OutputStreamingConverter<?> getOutputConverter() {
		return outputConverter;
	}

	/**
	 * @param outputConverter the output converter to set 
	 */
	public void setOutputConverter(OutputStreamingConverter<?> outputConverter) {
		this.outputConverter = outputConverter;
		if (!(outputConverter instanceof AbstractByteArrayStreamingConverter)) {
			mapper.setStringToBytes(false);
		}
	}

	/**
	 * @param listener the listener to set
	 */
	public void registerListener(TcpListener listener) {
		this.listener = listener;
	}
	
	/**
	 * @param sender the sender to set
	 */
	public void registerSender(TcpSender sender) {
		this.sender = sender;
		if (sender != null) {
			sender.addNewConnection(this);
		}
	}

	/**
	 * @return the listener
	 */
	public TcpListener getListener() {
		return this.listener;
	}
	
	/**
	 * @param singleUse true if this socket is to used once and 
	 * discarded.
	 */
	public void setSingleUse(boolean singleUse) {
		this.singleUse = singleUse;
	}

	/**
	 * 
	 * @return True if connection is used once.
	 */
	public boolean isSingleUse() {
		return this.singleUse;
	}

	public boolean isServer() {
		return server;
	}

}
