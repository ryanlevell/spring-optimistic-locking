package com.example.optimistic.locking.optimisticlockingdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@Slf4j
public class OptimisticLockingDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(OptimisticLockingDemoApplication.class, args);
	}

	@Bean
	public RetryListener retryListener() {
		return new RetryListener() {
			@Override
			public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
				log.warn("Attempting retry due to [{}] for [{}]", throwable.getClass().getSimpleName(), context.getAttribute("context.name"));
			}
		};
	}
}
