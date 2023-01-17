package com.dhlee.pool;

public class PoolShutdownException extends Exception {

	public PoolShutdownException() {
		super("pool is shutdown already.");
	}

	public PoolShutdownException(String message) {
		super(message);
	}

	public PoolShutdownException(Throwable cause) {
		super(cause);
	}

	public PoolShutdownException(String message, Throwable cause) {
		super(message, cause);
	}

	public PoolShutdownException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
