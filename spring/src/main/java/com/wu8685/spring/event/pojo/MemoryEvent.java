package com.wu8685.spring.event.pojo;

import org.springframework.context.ApplicationEvent;

public class MemoryEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	private long total;
	private long usage;

	public MemoryEvent(Object source, long total, long usage) {
		super(source);
		
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
