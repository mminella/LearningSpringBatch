package io.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class XmlFileOutputApplication {

	public static void main(String[] args) {
		SpringApplication.run(XmlFileOutputApplication.class, args);
	}
}
