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
package io.spring.batch.components;

import org.springframework.batch.item.ItemProcessor;

/**
 * @author Michael Minella
 */
public class RetryItemProcessor implements ItemProcessor<String, String> {

	private boolean retry = false;
	private int attemptCount = 0;

	@Override
	public String process(String item) throws Exception {
		System.out.println("processing item " + item);
		if(retry && item.equalsIgnoreCase("42")) {
			attemptCount++;

			if(attemptCount >= 5) {
				System.out.println("Success!");
				retry = false;
				return String.valueOf(Integer.valueOf(item) * -1);
			}
			else {
				System.out.println("Processing of item " + item + " failed");
				throw new CustomRetryableException("Process failed.  Attempt:" + attemptCount);
			}
		}
		else {
			return String.valueOf(Integer.valueOf(item) * -1);
		}
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}
}
