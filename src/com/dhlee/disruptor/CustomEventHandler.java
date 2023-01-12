package com.dhlee.disruptor;

import com.lmax.disruptor.EventHandler;

public class CustomEventHandler  implements EventHandler<ValueEvent> {
	String name;
	public CustomEventHandler() {
	}
	
	public CustomEventHandler(String name) {
		this.name = name;
	}
	
	@Override
	public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) throws Exception {
		System.out.printf("EventHandler: %s Sequence: %s ValueEvent: %s\n"
				,name, sequence, event.getValue()
				);
//		Thread.sleep(2000);
    }
}
