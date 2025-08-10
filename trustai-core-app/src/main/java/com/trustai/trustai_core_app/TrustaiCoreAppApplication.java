package com.trustai.trustai_core_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.trustai.*"})
public class TrustaiCoreAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrustaiCoreAppApplication.class, args);
	}

}
