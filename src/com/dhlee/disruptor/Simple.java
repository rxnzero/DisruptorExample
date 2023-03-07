package com.dhlee.disruptor;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class Simple {
	
	@SuppressWarnings("unchecked")
    public static void main(String[] args) {
		
		double p = 0;
		for(int i=1; i<=10; i++) {
			p = Math.pow(2,  i);
			System.out.printf("2 pow %d = %f\n", i, p);
			System.out.printf("%f exp = %d\n", p, Math.getExponent(p));
			System.out.printf("check = %b\n", Math.pow(2, Math.getExponent(p)));
		}
		System.out.println(100 == Math.pow(2, Math.getExponent(100)));
	}
	public static void test() {
        // Preallocate RingBuffer with 1024 ValueEvents
		CustomThreadFactory tFactory = new CustomThreadFactory();
        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, (int)Math.pow(2, 10), tFactory,
        		ProducerType.SINGLE, 
                new BlockingWaitStrategy());
        
        EventHandler<ValueEvent>[] handlers = new EventHandler[1];
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
	
	        for (long i = 0; i < 11; i++) {
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
        }
    }	
}
