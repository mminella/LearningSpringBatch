package io.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@EnableBatchProcessing
@IntegrationComponentScan
//@EnableIntegration
public class RemoteChunkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemoteChunkingApplication.class, args);
	}
}
