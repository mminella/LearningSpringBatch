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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Minella
 */
@Configuration
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step startStep() {
		return stepBuilderFactory.get("startStep")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("This is the start tasklet");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step evenStep() {
		return stepBuilderFactory.get("evenStep")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("This is the even tasklet");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step oddStep() {
		return stepBuilderFactory.get("oddStep")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("This is the odd tasklet");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public JobExecutionDecider decider() {
		return new OddDecider();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(startStep())
				.next(decider())
				.from(decider()).on("ODD").to(oddStep())
				.from(decider()).on("EVEN").to(evenStep())
				.from(oddStep()).on("*").to(decider())
//				.from(decider()).on("ODD").to(oddStep())
//				.from(decider()).on("EVEN").to(evenStep())
				.end()
				.build();
	}

	public static class OddDecider implements JobExecutionDecider {

		private int count = 0;

		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			count++;

			if(count % 2 == 0) {
				return new FlowExecutionStatus("EVEN");
			}
			else {
				return new FlowExecutionStatus("ODD");
			}
		}
	}
}
