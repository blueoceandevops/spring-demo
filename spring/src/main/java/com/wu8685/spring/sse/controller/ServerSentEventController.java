package com.wu8685.spring.sse.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.wu8685.spring.event.pojo.MemoryEvent;

@RestController
public class ServerSentEventController {

	private static Logger logger = Logger.getLogger(ServerSentEventController.class);

	private ConcurrentHashMap<SseEmitter, Integer> sseEmitters = new ConcurrentHashMap<>();
	private Set<SseEmitter> deadEmitters = new HashSet<SseEmitter>();

	@RequestMapping("sse/closeall")
	public String closeAllSseEmitters() {
		for (Entry<SseEmitter, Integer> entry : sseEmitters.entrySet()) {
			SseEmitter se = entry.getKey();
			se.complete();
			sseEmitters.remove(se);
		}

		return "success";
	}

	@RequestMapping("/sse/memory")
	public SseEmitter serverSentEvent(@RequestHeader(name = "Last-Event-ID", required = false) String lastId)
			throws IOException {
		// keep connection timeout to 20s, defaultly 30s
		SseEmitter sseEmitter = new CrossDomainAllowSseEmitter(20000L);
		sseEmitter.onCompletion(() -> {
			sseEmitter.complete();
			logger.warn("complete one sse emitter");
		});
		sseEmitter.onTimeout(() -> {
			sseEmitter.complete();
			logger.warn("complete one sse emitter with exception");
		});

		sseEmitters.put(sseEmitter, 1);
		return sseEmitter;
	}

	@EventListener
	public void onApplicationEvent(MemoryEvent event) {
		deadEmitters.clear();
		logger.debug("active sseEmitter is " + sseEmitters.size());
		for (Entry<SseEmitter, Integer> entry : sseEmitters.entrySet()) {
			SseEmitter se = entry.getKey();
			try {
				se.send(SseEmitter.event()
						.id(String.valueOf(event.getTimestamp()))
						.name("memory_info")
						.data(new Memory(event.getTotal(), event.getUsage()), MediaType.APPLICATION_JSON)
						.comment("wu8685 sse momery monitor"));
			} catch (Exception e) {
				deadEmitters.add(se); // filter out dead emitters caused by network issue or client-closed by exception
			}
		}

		for (SseEmitter se : deadEmitters) {
			sseEmitters.remove(se);
		}
	}

	static class Memory {

		private long total;

		private long usage;

		public Memory(long total, long usage) {
			super();
			this.total = total;
			this.usage = usage;
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getUsage() {
			return usage;
		}

		public void setUsage(long usage) {
			this.usage = usage;
		}
	}

	static class CrossDomainAllowSseEmitter extends SseEmitter {

		public CrossDomainAllowSseEmitter() {
			super();
		}

		public CrossDomainAllowSseEmitter(Long timeout) {
			super(timeout);
		}

		@Override
		protected void extendResponse(ServerHttpResponse outputMessage) {
			super.extendResponse(outputMessage);

			// enable crossing domain accessing from web page
			outputMessage.getHeaders().setAccessControlAllowOrigin("*");
		}

	}
}
