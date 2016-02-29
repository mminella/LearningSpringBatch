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

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration implements ApplicationContextAware{

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private ApplicationContext applicationContext;

	@Bean
	public ListItemReader<String> itemReader() {
		List<String> items = new ArrayList<>(1000);

		for(int i = 0; i < 1000; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	public ItemWriter<String> itemWriter() {
		return items -> {
			for (String item : items) {
				System.out.println(">> " + item);
			}
		};
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(100)
				.reader(itemReader())
				.writer(itemWriter())
				.listener((ChunkListener) chunkListener())
				.build();
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("job")
				.start(step1())
				.listener((JobExecutionListener) jobExecutionlistener())
				.build();
	}

	@Bean
	public Object jobExecutionlistener() throws Exception {
		GatewayProxyFactoryBean proxyFactoryBean = new GatewayProxyFactoryBean(JobExecutionListener.class);

		proxyFactoryBean.setDefaultRequestChannel(events());
		proxyFactoryBean.setBeanFactory(this.applicationContext);

		return proxyFactoryBean.getObject();
	}

	@Bean
	public Object chunkListener() throws Exception {
		GatewayProxyFactoryBean proxyFactoryBean = new GatewayProxyFactoryBean(ChunkListener.class);

		proxyFactoryBean.setDefaultRequestChannel(events());
		proxyFactoryBean.setBeanFactory(this.applicationContext);

		return proxyFactoryBean.getObject();
	}

	@Bean
	public DirectChannel events() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "events")
	public CharacterStreamWritingMessageHandler logger() {
		return CharacterStreamWritingMessageHandler.stdout();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
