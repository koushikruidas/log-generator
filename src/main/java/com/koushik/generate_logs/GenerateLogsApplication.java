package com.koushik.generate_logs;

import com.koushik.generate_logs.simple.HelloWorld;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.poinciana.loganalyzerClient"})
public class GenerateLogsApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(GenerateLogsApplication.class, args);
		HelloWorld helloWorld = new HelloWorld();
		HelloWorld.createLogs();
	}

}
