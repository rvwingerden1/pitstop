package com.example.app;

import io.fluxcapacitor.javaclient.configuration.spring.FluxCapacitorSpringConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FluxCapacitorSpringConfig.class)
@Slf4j
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		log.info("Application running");
	}

}
