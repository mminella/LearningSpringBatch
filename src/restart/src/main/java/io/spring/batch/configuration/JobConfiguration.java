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

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
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
	@StepScope
	public Tasklet restartTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

				if(stepExecutionContext.containsKey("ran")) {
					System.out.println("This time we'll let it go.");
					return RepeatStatus.FINISHED;
				}
				else {
					System.out.println("I don't think so...");
					chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);
					throw new RuntimeException("Not this time...");
				}
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet(restartTasklet())
				.build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.tasklet(restartTasklet())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.next(step2())
				.build();
	}
}
