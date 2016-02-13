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

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public StatefulItemReader itemReader() {
		List<String> items = new ArrayList<>(100);

		for(int i = 1; i <= 100; i++) {
			items.add(String.valueOf(i));
		}

		return new StatefulItemReader(items);
	}

	@Bean
	public ItemWriter itemWriter() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println(">> " + item);
				}
			}
		};
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(10)
				.reader(itemReader())
				.writer(itemWriter())
				.stream(itemReader())
				.build();
	}

	@Bean
	public Job statefulJob() {
		return jobBuilderFactory.get("statefulJob")
				.start(step())
				.build();
	}
}
