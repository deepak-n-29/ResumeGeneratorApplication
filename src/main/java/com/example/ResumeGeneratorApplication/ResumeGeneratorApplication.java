package com.example.ResumeGeneratorApplication;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ResumeGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeGeneratorApplication.class, args);
	}
}
