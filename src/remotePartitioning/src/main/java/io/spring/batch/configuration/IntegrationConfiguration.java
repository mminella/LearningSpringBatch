/*
 * Copyright 2016 the original author or authors.
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
package io.spring.batch.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.PollableChannel;

/**
 * @author Michael Minella
 */
@Configuration
public class IntegrationConfiguration {

	@Bean
	public MessagingTemplate messageTemplate() {
		MessagingTemplate messagingTemplate = new MessagingTemplate(outboundRequests());

		messagingTemplate.setReceiveTimeout(60000000l);

		return messagingTemplate;
	}

	@Bean
	public DirectChannel outboundRequests() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "outboundRequests")
	public AmqpOutboundEndpoint amqpOutboundEndpoint(AmqpTemplate template) {
		AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);

		endpoint.setExpectReply(true);
		endpoint.setOutputChannel(inboundRequests());

		endpoint.setRoutingKey("partition.requests");

		return endpoint;
	}

	@Bean
	public Queue requestQueue() {
		return new Queue("partition.requests", false);
	}

	@Bean
	@Profile("slave")
	public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);

		adapter.setOutputChannel(inboundRequests());

		adapter.afterPropertiesSet();

		return adapter;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container =
				new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames("partition.requests");
		container.setAutoStartup(false);

		return container;
	}

	@Bean
	public PollableChannel outboundStaging() {
		return new NullChannel();
	}

	@Bean
	public QueueChannel inboundRequests() {
		return new QueueChannel();
	}
}
