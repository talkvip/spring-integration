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
package org.springframework.integration.ip.tcp;

import java.net.ServerSocket;

import org.springframework.integration.Message;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.ConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpListener;

/**
 * Tcp inbound channel adapter using a TcpConnection to 
 * receive data - if the connection factory is a server
 * factory, this Listener owns the connections. If it is
 * a client factory, the sender owns the connection.
 * 
 * @author Gary Russell
 * @since 2.0
 *
 */
public class TcpReceivingChannelAdapter 
	extends MessageProducerSupport implements TcpListener {

	protected ServerSocket serverSocket;
	
	protected ConnectionFactory clientConnectionFactory;
	
	protected ConnectionFactory serverConnectionFactory;
	
	public boolean onMessage(Message<?> message) {
		sendMessage(message);
		return false;
	}
	
	@Override
	protected void doStart() {
		// Nothing to do; we're passive
	}

	@Override
	protected void doStop() {
		// Nothing to do; we're passive
	}

	/**
	 * Sets the client or server connection factory; for this (an inbound adapter), if
	 * the factory is a client connection factory, the sockets are owned by a sending
	 * channel adapter and this adapter is used to receive replies.
	 * 
	 * @param connectionFactory the connectionFactory to set
	 */
	public void setConnectionFactory(AbstractConnectionFactory connectionFactory) {
		if (connectionFactory instanceof AbstractClientConnectionFactory) {
			this.clientConnectionFactory = connectionFactory;
		} else {
			this.serverConnectionFactory = connectionFactory;
		}
		connectionFactory.registerListener(this);		
	}

	public boolean isListening() {
		if (this.serverConnectionFactory == null) {
			return false;
		}
		if (this.serverConnectionFactory instanceof AbstractServerConnectionFactory) {
			return ((AbstractServerConnectionFactory) this.serverConnectionFactory).isListening();
		}
		return false;
	}

}
