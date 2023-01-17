package com.dhlee.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class LoggingPoolObjectFactory extends BasePooledObjectFactory<LoggingPoolObject> {
	private static int i = 0;
	public LoggingPoolObjectFactory() {

	}

	@Override
	public LoggingPoolObject create() throws Exception {
		return new LoggingPoolObject(i++, 1024, 10);
	}

	@Override
	public PooledObject<LoggingPoolObject> wrap(LoggingPoolObject object) {
		return new DefaultPooledObject<LoggingPoolObject>(object);
	}
	
	@Override
    public void passivateObject(PooledObject<LoggingPoolObject> pooledObject) {
        ;
    }
	@Override
    public void destroyObject(PooledObject<LoggingPoolObject> pooledObject) {
		pooledObject.getObject().shutdown();
    }
}
