package com.khaoula.transactionsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.khaoula.transactionsservice", "com.aitsaid.commonsecurity"})
public class TransactionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsServiceApplication.class, args);
	}

}
