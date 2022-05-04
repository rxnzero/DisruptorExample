package com.dhlee.disruptor;

import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class Simple {
	
	@SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Preallocate RingBuffer with 1024 ValueEvents
//		ExecutorService exec = Executors.newCachedThreadPool();
//        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 1024, exec);
		CustomThreadFactory tFactory = new CustomThreadFactory();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 1024, tFactory);
        
        EventHandler<ValueEvent>[] handlers = new EventHandler[10];
        for(int i=0; i< handlers.length; i++) {
//	        EventHandler<ValueEvent> handler = new EventHandler<ValueEvent>() {
	            // event will eventually be recycled by the Disruptor after it wraps
//	            public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
//	            	System.out.println("EventHandler: " + pos);
//	            	System.out.println("Sequence: " + sequence);
//	                System.out.println("ValueEvent: " + event.getValue());
//	            }
//	        };
        	CustomEventHandler handler = new CustomEventHandler("Handler"+i); 
	        handlers[i] = handler;
        }
        
        try {
	        // Build dependency graph
	        disruptor.handleEventsWith(handlers);
	        disruptor.start();
	        RingBuffer<ValueEvent> ringBuffer = disruptor.getRingBuffer();
	
	        for (long i = 10; i < 11; i++) {
	            String uuid = UUID.randomUUID().toString();
	            // Two phase commit. Grab one of the 1024 slots
	            long seq = ringBuffer.next();
	            ValueEvent valueEvent = ringBuffer.get(seq);
	            valueEvent.setValue(uuid);
	            ringBuffer.publish(seq);
	        }
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        finally {
        	if(disruptor !=null) disruptor.shutdown();
//        	if(exec !=null) exec.shutdown();
        }
    }	
}
