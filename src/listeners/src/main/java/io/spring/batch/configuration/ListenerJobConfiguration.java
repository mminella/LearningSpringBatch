/*
 * Copyright 2015 the original author or authors.
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

import java.util.Arrays;
import java.util.List;

import io.spring.batch.listener.ChunkListener;
import io.spring.batch.listener.JobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Michael Minella
 */
@Configuration
public class ListenerJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public ItemReader<String> reader() {
		return new ListItemReader<>(Arrays.asList("one", "two", "three"));
	}

	@Bean
	public ItemWriter<String> writer() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println("Writing item " + item);
				}
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(2)
				.faultTolerant()
				.listener(new ChunkListener())
				.reader(reader())
				.writer(writer())
				.build();
	}

	@Bean
	public Job listenerJob(JavaMailSender javaMailSender) {
		return jobBuilderFactory.get("listenerJob")
				.start(step1())
				.listener(new JobListener(javaMailSender))
				.build();
	}
}
