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

import io.spring.batch.domain.Customer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

/**
 * @author Michael Minella
 */
public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

	private ItemWriter<Customer> evenItemWriter;
	private ItemWriter<Customer> oddItemWriter;

	public CustomerClassifier(ItemWriter<Customer> evenItemWriter, ItemWriter<Customer> oddItemWriter) {
		this.evenItemWriter = evenItemWriter;
		this.oddItemWriter = oddItemWriter;
	}

	@Override
	public ItemWriter<? super Customer> classify(Customer customer) {
		return customer.getId() % 2 == 0 ? evenItemWriter : oddItemWriter;
	}
}
