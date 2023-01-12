package com.dhlee.disruptor;

import java.util.ArrayList;
import java.util.List;

import com.lmax.disruptor.WorkHandler;

public class CustomWorkHandler implements WorkHandler<ValueEvent> {
	String name;
	int sleepMs;
	
	private int count = 0;
    private int batchSize = 1;
    private final List<ValueEvent> eventList = new ArrayList();
    
	public CustomWorkHandler() {
	}

	public CustomWorkHandler(String name, int sleepMs, int batchSize) {
		this.name = name;
		this.sleepMs = sleepMs;
		this.batchSize = batchSize;
	}

	@Override
	public void onEvent(ValueEvent event) throws Exception {
		Thread.sleep(sleepMs);
		if(batchSize > 1) {
			eventList.add(event);
	        if (++count >= batchSize) {
	            processBatch();
	            eventList.clear();
	            count = 0;
	        }
		}
		else {
			System.out.printf("WorkHandler: %s ValueEvent: %s\n"
					,name, event.getValue()
					);
		}
        
	}

	private void processBatch() {
		StringBuilder sb = new StringBuilder();
		sb.append("WorkHandler Batch: ").append(name);
		sb.append(" size=").append(eventList.size()).append(" [");
		for(ValueEvent event:eventList) {			
			sb.append(event.getValue()).append(",");	
		}
		sb.append("]");
		System.out.println(sb.toString());
	}
}
