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
package io.spring.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Michael Minella
 */
@RestController
public class JobLaunchingController {

	@Autowired
	private MessageChannel requests;

	@Autowired
	private DirectChannel replies;

	@Autowired
	private Job job;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void launch(@RequestParam("name") String name) throws Exception {
		JobParameters jobParameters =
				new JobParametersBuilder()
						.addString("name", name)
						.toJobParameters();
		JobLaunchRequest launchRequest = new JobLaunchRequest(this.job, jobParameters);

		replies.subscribe(new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				JobExecution payload = (JobExecution) message.getPayload();

				System.out.println(">> " + payload.getJobInstance().getJobName() + " resulted in " + payload.getStatus());
			}
		});

		requests.send(MessageBuilder.withPayload(launchRequest).setReplyChannel(replies).build());
	}
}
