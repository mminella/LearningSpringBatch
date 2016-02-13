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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import io.spring.batch.domain.Customer;
import io.spring.batch.domain.CustomerRowMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
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

	@Autowired
	public DataSource dataSource;
//
//	@Bean
//	public JdbcCursorItemReader<Customer> cursorItemReader() {
//		JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
//
//		reader.setSql("select id, firstName, lastName, birthdate from customer order by lastName, firstName");
//		reader.setDataSource(this.dataSource);
//		reader.setRowMapper(new CustomerRowMapper());
//
//		return reader;
//	}

	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
		queryProvider.setFromClause("from customer");

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);

		return reader;
	}

	@Bean
	public ItemWriter<Customer> customerItemWriter() {
		return items -> {
			for (Customer item : items) {
				System.out.println(item.toString());
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(10)
				.reader(pagingItemReader())
				.writer(customerItemWriter())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}
}
