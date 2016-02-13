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
package io.spring.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Michael Minella
 */
public class JobListener implements JobExecutionListener {

	private JavaMailSender mailSender;

	public JobListener(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();

		SimpleMailMessage mail =
				getSimpleMailMessage(String.format("%s is starting", jobName),
						String.format("Per your request, we are informing you that %s is starting",
				jobName));

		mailSender.send(mail);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();

		SimpleMailMessage mail =
				getSimpleMailMessage(String.format("%s has completed", jobName),
						String.format("Per your request, we are informing you that %s has completed",
								jobName));

		mailSender.send(mail);
	}

	private SimpleMailMessage getSimpleMailMessage(String subject, String text) {
		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setTo("springbatch@michaelminella.com");
		mail.setSubject(subject);
		mail.setText(text);
		return mail;
	}
}
