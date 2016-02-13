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

import io.spring.batch.components.CustomRetryableException;
import io.spring.batch.components.SkipItemProcessor;
import io.spring.batch.components.SkipItemWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
	@StepScope
	public ListItemReader<String> reader() {

		List<String> items = new ArrayList<>();

		for(int i = 0; i < 100; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public SkipItemProcessor processor(@Value("#{jobParameters['skip']}")String skip) {
		SkipItemProcessor processor = new SkipItemProcessor();

		processor.setSkip(StringUtils.hasText(skip) && skip.equalsIgnoreCase("processor"));

		return processor;
	}

	@Bean
	@StepScope
	public SkipItemWriter writer(@Value("#{jobParameters['skip']}")String skip) {
		SkipItemWriter writer = new SkipItemWriter();

		writer.setSkip(StringUtils.hasText(skip) && skip.equalsIgnoreCase("writer"));

		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step")
				.<String, String>chunk(10)
				.reader(reader())
				.processor(processor(null))
				.writer(writer(null))
				.faultTolerant()
				.skip(CustomRetryableException.class)
				.skipLimit(15)
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}
}
