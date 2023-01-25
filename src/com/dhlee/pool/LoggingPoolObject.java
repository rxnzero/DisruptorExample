package com.dhlee.pool;

import com.dhlee.disruptor.CustomThreadFactory;
import com.dhlee.disruptor.CustomWorkHandler;
import com.dhlee.disruptor.ValueEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class LoggingPoolObject {

	Disruptor<ValueEvent> disruptor = null;
	RingBuffer<ValueEvent> ringBuffer = null;
	int id = 0;
	int queueMax =  (int)Math.pow(2, 10);
	int workerSize = 0;

	public LoggingPoolObject() {
		
	}
	
	public LoggingPoolObject(int id, int queueMax, int workerSize) {
		this.id = id;
		CustomThreadFactory tFactory = new CustomThreadFactory();
        disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, queueMax, tFactory,
        		ProducerType.SINGLE, 
                new SleepingWaitStrategy());
//        BlockingWaitStrategy | SleepingWaitStrategy | YieldingWaitStrategy | BusySpinWaitStrategy
        WorkHandler<ValueEvent>[] handlers = new WorkHandler[workerSize];
        for(int i=0; i< handlers.length; i++) {
        	CustomWorkHandler handler = new CustomWorkHandler(String.format("Handler%d-%d",id, i), 100, 1); 
	        handlers[i] = handler;
        }
        try {
	        disruptor.handleEventsWithWorkerPool(handlers);
	        
	        System.out.println(String.format(">> disruptor-%d.start",id));
	        disruptor.start();
	        ringBuffer = disruptor.getRingBuffer();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        finally {
        	;
        }
	}
	
	public void putMessage(String message) {
		long seq = ringBuffer.next();
        try {
          System.out.println( String.format("disruptor-%d publish : ringBuffer seq = %d uuid=%s",id, seq, message) );
        	ValueEvent valueEvent = ringBuffer.get(seq);            
            valueEvent.setValue(message);
        }
        finally {
        	ringBuffer.publish(seq);
        }
	}

	public void shutdown() {
		if(disruptor == null) return;
		while(true) {
    		if(queueMax == disruptor.getRingBuffer().remainingCapacity()) break;
    		try {
    			System.out.println(String.format("disruptor-%d Sleep 100 ms.", id));
				Thread.sleep(100);
			} catch (InterruptedException e) {
				;
			}
    	}
    	System.out.println(String.format("<< disruptor-%d shutdown remainingCapacity : %d",id, disruptor.getRingBuffer().remainingCapacity()) );
    	if(disruptor !=null) disruptor.shutdown();
	}
}
