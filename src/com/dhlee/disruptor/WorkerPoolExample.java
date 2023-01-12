package com.dhlee.disruptor;

import java.util.UUID;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class WorkerPoolExample {
	
	@SuppressWarnings("unchecked")
    public static void main(String[] args) {
		int queueMax = (int)Math.pow(2, 10);
		CustomThreadFactory tFactory = new CustomThreadFactory();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, queueMax, tFactory,
        		ProducerType.SINGLE, 
                new SleepingWaitStrategy());
//        BlockingWaitStrategy | SleepingWaitStrategy | YieldingWaitStrategy | BusySpinWaitStrategy
        WorkHandler<ValueEvent>[] handlers = new WorkHandler[10];
        for(int i=0; i< handlers.length; i++) {
        	CustomWorkHandler handler = new CustomWorkHandler("Handler"+i, 100, 1); 
	        handlers[i] = handler;
        }
        
        try {
	        // Build dependency graph
	        disruptor.handleEventsWithWorkerPool(handlers);
	        
	        System.out.println(">> disruptor.start");
	        disruptor.start();
	        RingBuffer<ValueEvent> ringBuffer = disruptor.getRingBuffer();
	
	        for (long i = 0; i < 100; i++) {
	            String uuid = UUID.randomUUID().toString();
	            // Two phase commit. Grab one of the 1024 slots
	            System.out.println("-> next :" + i);
	            long seq = ringBuffer.next();
	            // try/finally : Failing to do so can result in corruption of the state of the Disruptor
	            try {
//		            System.out.println( String.format("ringBuffer seq = %d uuid=%s", seq, uuid) );
	            	System.out.println("-> ringBuffer.get :" + i);
		            ValueEvent valueEvent = ringBuffer.get(seq);
		            System.out.println("-> valueEvent.setValue :" + i);
		            valueEvent.setValue(uuid);
	            }
	            finally {
	            	System.out.println("-> publish :" + i); 
	            	ringBuffer.publish(seq);
	            }
	        }
	        System.out.println("<< disruptor remainingCapacity : " + disruptor.getRingBuffer().remainingCapacity());
	        int sleepSecs = 2;
	        System.out.println(String.format("Sleep %,d secs.", sleepSecs));
	        Thread.sleep(sleepSecs *1000);	        
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        finally {
        	while(true) {
        		if(queueMax == disruptor.getRingBuffer().remainingCapacity()) break;
        		try {
        			System.out.println(String.format("Sleep 100 ms."));
					Thread.sleep(100);
				} catch (InterruptedException e) {
					;
				}
        	}
        	System.out.println("<< disruptor.shutdown remainingCapacity : " + disruptor.getRingBuffer().remainingCapacity());
        	
        	if(disruptor !=null) disruptor.shutdown();
        }
    }	
}
