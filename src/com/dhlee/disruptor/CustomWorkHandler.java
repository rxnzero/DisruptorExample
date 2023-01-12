package com.dhlee.disruptor;

import com.lmax.disruptor.WorkHandler;

public class CustomWorkHandler implements WorkHandler<ValueEvent> {
	String name;
	int sleepMs;
	public CustomWorkHandler() {
	}

	public CustomWorkHandler(String name, int sleepMs) {
		this.name = name;
		this.sleepMs = sleepMs;
	}

	@Override
	public void onEvent(ValueEvent event) throws Exception {
		Thread.sleep(sleepMs);
		System.out.printf("WorkHandler: %s ValueEvent: %s\n"
				,name, event.getValue()
				);
		
	}

}
