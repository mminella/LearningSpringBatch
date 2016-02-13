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
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkHandler;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

/**
 * @author Michael Minella
 */
@Configuration
public class IntegrationConfiguration {

	@Bean
	public ChunkMessageChannelItemWriter chunkWriter() {
		ChunkMessageChannelItemWriter chunkWriter = new ChunkMessageChannelItemWriter();

		chunkWriter.setMessagingOperations(messageTemplate());
		chunkWriter.setReplyChannel(inboundReplies());
		chunkWriter.setMaxWaitTimeouts(10);

		return chunkWriter;
	}

	@Bean
	@ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundReplies")
	public ChunkHandler chunkHandler(TaskletStep step1) throws Exception {
		RemoteChunkHandlerFactoryBean factoryBean = new RemoteChunkHandlerFactoryBean();

		factoryBean.setChunkWriter(chunkWriter());
		factoryBean.setStep(step1);

		return factoryBean.getObject();
	}

	@Bean
	public MessagingTemplate messageTemplate() {
		MessagingTemplate messagingTemplate = new MessagingTemplate(outboundRequests());

		messagingTemplate.setReceiveTimeout(60000000l);

		return messagingTemplate;
	}

	@Bean
	public QueueChannel inboundReplies() {
		return new QueueChannel();
	}

	@Bean
	public QueueChannel outboundReplies() {
		return new QueueChannel();
	}

	@Bean
	public Queue requestQueue() {
		return new Queue("chunking.requests", false);
	}

	@Bean
	public MessageChannel outboundRequests() {
		return new DirectChannel();
	}

	@Bean
	public PollableChannel inboundRequests() {
		return new QueueChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "outboundRequests")
	public AmqpOutboundEndpoint amqpOutboundEndpoint(AmqpTemplate template) {
		AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);

		endpoint.setExpectReply(true);
		endpoint.setOutputChannel(inboundReplies());

		endpoint.setRoutingKey("chunking.requests");

		return endpoint;
	}

	@Bean
	public AmqpInboundGateway inbound(SimpleMessageListenerContainer listenerContainer) {
		AmqpInboundGateway gateway = new AmqpInboundGateway(listenerContainer);

		gateway.setRequestChannel(inboundRequests());
		gateway.setRequestTimeout(60000000l);
		gateway.setReplyChannel(outboundReplies());

		gateway.afterPropertiesSet();

		return gateway;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container =
				new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames("chunking.requests");
		container.setConcurrentConsumers(4);

		return container;
	}
}
