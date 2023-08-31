package com.aman.BlogPost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.aman.BlogPost")
@ComponentScan(basePackages = "com.aman.BlogPost")
@EnableJpaRepositories(basePackages = "com.aman.BlogPost")
public class BlogPostApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlogPostApplication.class, args);
		System.out.println("Aman Sachan");
	}
}
