package com.eventix.ticket;

import org.springframework.boot.SpringApplication;

public class TestEventixApplication {

	public static void main(String[] args) {
		SpringApplication.from(EventixApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
