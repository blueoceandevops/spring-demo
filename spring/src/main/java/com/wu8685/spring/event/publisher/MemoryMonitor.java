package com.wu8685.spring.event.publisher;

import java.util.concurrent.Executors;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.wu8685.spring.event.pojo.MemoryEvent;

@Component
public class MemoryMonitor implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = Logger.getLogger(MemoryMonitor.class);

	@Autowired
	private ApplicationContext context;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("start monitor memory usage");

		Executors.newFixedThreadPool(1).execute(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Runtime runtime = Runtime.getRuntime();
					long total = runtime.totalMemory();
					long usage = total - runtime.freeMemory();

					context.publishEvent(new MemoryEvent(this, total, usage));
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logger.info("memory tracker interrupted", e);
						return;
					}
				}
			}

		});
	}

}
