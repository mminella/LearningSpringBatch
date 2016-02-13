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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public JobLauncher jobLauncher;

	@Bean
	@ServiceActivator(inputChannel = "requests", outputChannel = "replies")
	public JobLaunchingMessageHandler jobLaunchingMessageHandler() {
		return new JobLaunchingMessageHandler(this.jobLauncher);
	}

	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel replies() {
		return new DirectChannel();
	}

	@Bean
	@StepScope
	public Tasklet tasklet(@Value("#{jobParameters['name']}") String name) {
		return (contribution, chunkContext) -> {
			System.out.println(String.format("The job ran for %s", name));
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(stepBuilderFactory.get("step1")
					.tasklet(tasklet(null))
					.build())
				.build();
	}
}
