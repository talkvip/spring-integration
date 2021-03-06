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


import org.springframework.commons.serializer.InputStreamingConverter;
import org.springframework.commons.serializer.OutputStreamingConverter;
import org.springframework.integration.Message;

/**
 * Base class for TcpConnectionIntercepters; passes all method calls through
 * to the underlying {@link TcpConnection}.
 * 
 * @author Gary Russell
 * @since 2.0
 *
 */
public abstract class AbstractTcpConnectionInterceptor implements TcpConnectionInterceptor {

	private TcpConnection theConnection;
	
	private TcpListener tcpListener;

	private TcpSender tcpSender;

	public void close() {
		this.theConnection.close();
	}

	public boolean isOpen() {
		return this.theConnection.isOpen();
	}

	public Object getPayload() throws Exception {
		return this.theConnection.getPayload();
	}

	public String getHostName() {
		return this.theConnection.getHostName();
	}

	public String getHostAddress() {
		return this.theConnection.getHostAddress();
	}

	public int getPort() {
		return this.theConnection.getPort();
	}

	public void registerListener(TcpListener listener) {
		this.theConnection.registerListener(this);
		this.tcpListener = listener;
	}

	public void registerSender(TcpSender sender) {
		this.tcpSender = sender;
		this.theConnection.registerSender(this);
	}

	public String getConnectionId() {
		return this.theConnection.getConnectionId();
	}

	public boolean isSingleUse() {
		return this.theConnection.isSingleUse();
	}

	public void run() {
		this.theConnection.run();
	}

	public void setSingleUse(boolean singleUse) {
		this.theConnection.setSingleUse(singleUse);
	}

	public void setMapper(TcpMessageMapper mapper) {
		this.theConnection.setMapper(mapper);
	}

	public InputStreamingConverter<?> getInputConverter() {
		return this.theConnection.getInputConverter();
	}

	public void setInputConverter(InputStreamingConverter<?> inputConverter) {
		this.theConnection.setInputConverter(inputConverter);
	}

	public OutputStreamingConverter<?> getOutputConverter() {
		return this.theConnection.getOutputConverter();
	}

	public void setOutputConverter(OutputStreamingConverter<?> outputConverter) {
		this.theConnection.setOutputConverter(outputConverter);
	}

	public boolean isServer() {
		return this.theConnection.isServer();
	}

	public boolean onMessage(Message<?> message) {
		if (this.tcpListener == null) {
			throw new NoListenerException("No listener registered for message reception");
		}
		return this.tcpListener.onMessage(message);
	}

	public void send(Message<?> message) throws Exception {
		this.theConnection.send(message);
	}

	/**
	 * Returns the underlying connection (or next interceptor)
	 * @return the connection
	 */
	public TcpConnection getTheConnection() {
		return this.theConnection;
	}

	/**
	 * Sets the underlying connection (or next interceptor)
	 * @param theConnection the connection
	 */
	public void setTheConnection(TcpConnection theConnection) {
		this.theConnection = theConnection;
	}

	/**
	 * @return the listener
	 */
	public TcpListener getListener() {
		return tcpListener;
	}

	public void addNewConnection(TcpConnection connection) {
		if (this.tcpSender != null) {
			this.tcpSender.addNewConnection(this);
		}
	}

	public void removeDeadConnection(TcpConnection connection) {
		if (this.tcpSender != null) {
			this.tcpSender.removeDeadConnection(this);
		}
	}

}
