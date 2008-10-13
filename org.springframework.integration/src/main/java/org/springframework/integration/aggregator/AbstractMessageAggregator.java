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

package org.springframework.integration.aggregator;

import java.util.List;

import org.springframework.integration.message.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.util.Assert;

/**
 * A base class for aggregating a group of Messages into a single Message. 
 * Extends {@link AbstractMessageBarrierConsumer} and waits for a
 * <em>complete</em> group of {@link Message Messages} to arrive. Subclasses
 * must provide the implementation of the {@link #aggregateMessages(List)}
 * method to combine the group of Messages into a single {@link Message}.
 * 
 * <p>The default strategy for determining whether a group is complete is based
 * on the '<code>sequenceSize</code>' property of the header. Alternatively, a
 * custom implementation of the {@link CompletionStrategy} may be provided.
 * 
 * <p>All considerations regarding <code>timeout</code> and grouping by
 * <code>correlationId</code> from {@link AbstractMessageBarrierConsumer}
 * apply here as well.
 * 
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
public abstract class AbstractMessageAggregator extends AbstractMessageBarrierConsumer {

	private volatile CompletionStrategy completionStrategy = new SequenceSizeCompletionStrategy();


	/**
	 * Strategy to determine whether the group of messages is complete.
	 */
	public void setCompletionStrategy(CompletionStrategy completionStrategy) {
		Assert.notNull(completionStrategy, "'completionStrategy' must not be null");
		this.completionStrategy = completionStrategy;
	}

	protected MessageBarrier createMessageBarrier() {
		return new AggregationBarrier(this.completionStrategy);
	}

	protected boolean isBarrierRemovable(Object correlationId, List<Message<?>> releasedMessages) {
		return releasedMessages != null && releasedMessages.size() > 0;
	}

	protected Message<?>[] processReleasedMessages(Object correlationId, List<Message<?>> messages) {
		Message<?> result = this.aggregateMessages(messages);
		if (result == null) {
			return new Message<?>[0];
		}
		if (result.getHeaders().getCorrelationId() == null) {
			result = MessageBuilder.fromMessage(result).setCorrelationId(correlationId).build();
		}
		return new Message<?>[] { result };
	}

	protected abstract Message<?> aggregateMessages(List<Message<?>> messages);

}