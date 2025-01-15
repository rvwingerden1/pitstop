package com.example.app;

import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.FluxCapacitor;
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
		FluxCapacitor.sendCommandAndWait(JsonUtils.fromFile("/refdata/register-operators.json"));
		log.info("Application running");
	}

}
