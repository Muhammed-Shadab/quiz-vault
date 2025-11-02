package com.minProject.root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RootApplication {

	public static void main(String[] args) {
        System.out.println("working");
        SpringApplication.run(RootApplication.class, args);
	}

}
