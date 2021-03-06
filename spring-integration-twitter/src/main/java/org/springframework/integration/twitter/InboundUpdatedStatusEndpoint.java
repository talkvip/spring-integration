/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package org.springframework.integration.twitter;

import twitter4j.Paging;
import twitter4j.Twitter;


/**
 * This {@link org.springframework.integration.core.MessageSource} lets Spring Integration consume a given account's timeline
 * as messages. It has support for dynamic throttling of API requests.
 *
 * @author Josh Long
 * @since 2.0
 */
public class InboundUpdatedStatusEndpoint extends AbstractInboundTwitterStatusEndpointSupport {
	@Override
	protected void refresh() throws Exception {
		this.runAsAPIRateLimitsPermit(new ApiCallback<InboundUpdatedStatusEndpoint>() {
			public void run(InboundUpdatedStatusEndpoint t, Twitter twitter)
					throws Exception {
				forwardAll((!t.hasMarkedStatus()) ? twitter.getFriendsTimeline() : twitter.getFriendsTimeline(new Paging(t.getMarkerId())));
			}
		});
	}
}
