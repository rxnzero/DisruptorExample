package com.dhlee.disruptor;

import java.util.UUID;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class PublishExample {
	
	@SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Preallocate RingBuffer with 1024 ValueEvents
//		ExecutorService exec = Executors.newCachedThreadPool();
//        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 1024, exec);
		CustomThreadFactory tFactory = new CustomThreadFactory();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 8, tFactory,
        		ProducerType.SINGLE, 
                new BlockingWaitStrategy());
        
        EventHandler<ValueEvent>[] handlers = new EventHandler[5];
        for(int i=0; i< handlers.length; i++) {
        	CustomEventHandler handler = new CustomEventHandler("Handler"+i); 
	        handlers[i] = handler;
        }
        
        try {
	        // Build dependency graph
	        disruptor.handleEventsWith(handlers);
	        disruptor.start();
	        RingBuffer<ValueEvent> ringBuffer = disruptor.getRingBuffer();
	
	        for (long i = 0; i < 20; i++) {
	            String uuid = UUID.randomUUID().toString();
	            // Two phase commit. Grab one of the 1024 slots
	            long seq = ringBuffer.next();
	            
	            // try/finally : Failing to do so can result in corruption of the state of the Disruptor
	            try {
		            System.out.println( String.format("ringBuffer seq = %d uuid=%s", seq, uuid) ); 
		            ValueEvent valueEvent = ringBuffer.get(seq);
		            valueEvent.setValue(uuid);
	            }
	            finally {
	            	ringBuffer.publish(seq);
	            }
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
