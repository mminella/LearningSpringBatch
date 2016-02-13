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

import io.spring.batch.reader.StatelessItemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public StatelessItemReader statelessItemReader() {
		List<String> data = new ArrayList<>(3);

		data.add("Foo");
		data.add("Bar");
		data.add("Baz");

		return new StatelessItemReader(data);
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(2)
				.reader(statelessItemReader())
				.writer(list -> {
					for (String curItem : list) {
						System.out.println("curItem = " + curItem);
					}
				}).build();
	}

	@Bean
	public Job interfacesJob() {
		return jobBuilderFactory.get("interfacesJob")
				.start(step1())
				.build();
	}
}
