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
        System.out.println(">> newThread-"+count);
        return new Thread(command);
    }

    public CustomThreadFactory() {
		// TODO Auto-generated constructor stub
	}

}
