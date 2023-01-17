package com.dhlee.disruptor;

import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {
 
    // stores the thread count
    private int count = 0;
 
    // returns the thread count
    public int getCount() { return count; }
 
    // Factory method
    @Override
      public Thread newThread(Runnable command) {
        count++;
        System.out.println( String.format(">> newThread-%d created", count) );
        return new Thread(command);
    }

    public CustomThreadFactory() {

	}

}
