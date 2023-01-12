package com.dhlee.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;

public class EarlyReleaseHandler implements EventHandler<ValueEvent>
{
	String name;
    private Sequence sequenceCallback;
    private int batchRemaining = 20;

	public EarlyReleaseHandler() {
	}
	
	public EarlyReleaseHandler(String name) {
		this.name = name;
	}
	
    public void setSequenceCallback(final Sequence sequenceCallback)
    {
        this.sequenceCallback = sequenceCallback;
    }

    @Override
    public void onEvent(ValueEvent event, final long sequence, final boolean endOfBatch)
    {
        processEvent(event, sequence, endOfBatch);

        boolean logicalChunkOfWorkComplete = isLogicalChunkOfWorkComplete();
        if (logicalChunkOfWorkComplete)
        {
            sequenceCallback.set(sequence);
        }

        batchRemaining = logicalChunkOfWorkComplete || endOfBatch ? 20 : batchRemaining;
    }

    private boolean isLogicalChunkOfWorkComplete()
    {
        // Ret true or false based on whatever criteria is required for the smaller
        // chunk.  If this is doing I/O, it may be after flushing/syncing to disk
        // or at the end of DB batch+commit.
        // Or it could simply be working off a smaller batch size.

        return --batchRemaining == -1;
    }

    private void processEvent(ValueEvent event, long sequence, boolean endOfBatch)
    {
        // Do processing
		System.out.printf("EventHandler: %s sequenceCallback: %s Sequence: %s ValueEvent: %s\n"
				,name, sequenceCallback, sequence, event.getValue()
				);
    }

}
