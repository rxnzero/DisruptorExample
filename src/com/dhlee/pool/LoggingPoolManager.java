package com.dhlee.pool;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class LoggingPoolManager {
	private static boolean stopped = true;
	private ObjectPool<LoggingPoolObject> pool;
	
	public LoggingPoolManager() {
		@SuppressWarnings("rawtypes")
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		int maxSize = 10;
		LoggingPoolObjectFactory factory = new LoggingPoolObjectFactory();
		
		config.setMaxTotal(maxSize);
		config.setMinIdle(maxSize);
		config.setMaxIdle(maxSize);
        config.setMaxWait(Duration.ofMillis(1000));
        config.setLifo(true);
        pool = new GenericObjectPool<LoggingPoolObject>(factory, config);
        
        try {
        	LoggingPoolObject initArray[] = new LoggingPoolObject[maxSize];
            for (int i=0; i<maxSize; i++) {
            	initArray[i] = pool.borrowObject();
            }
            for (int i=maxSize-1; i >= 0; i--) {
            	pool.returnObject(initArray[i]);
            }
        } catch(Exception e) {
        	e.printStackTrace();
        }
        stopped = false;
	}
	
	public LoggingPoolObject borrowObject() throws Exception {
		if(stopped) throw new PoolShutdownException();
		return pool.borrowObject();
	}
	
	public void returnObject(LoggingPoolObject object) throws Exception {
		if(stopped) throw new PoolShutdownException();
		pool.returnObject(object);
	}
	
	public void shutdown() {
		System.out.println("<< shutdown start >>");
		int retry = 0;
		try {
			 while(true) {
				pool.close();
				System.out.println("<< close pool.getNumActive() = "+pool.getNumActive());
				if(pool.getNumActive() == 0 || retry > 10) break;
				System.out.println("<< close sleep..");
				Thread.sleep(100);
				retry++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			stopped = true;
		}
		System.out.println("<< shutdown end >>");
	}
	
	private static void testSingle() {
		System.out.println("MAIN : START. -->");
		LoggingPoolManager manager = new LoggingPoolManager();
		try {
			LoggingPoolObject queue = manager.borrowObject();
			String uuid = UUID.randomUUID().toString();
			queue.putMessage(uuid);
			manager.returnObject(queue);
			System.out.println("MAIN : Wait 10secs.");
			Thread.sleep(10 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("MAIN : <-- manager.shutdown");
			manager.shutdown();
		}
	}
	private static void testMulti() {
		LoggingPoolManager manager = new LoggingPoolManager();
		Runnable task = new Runnable() {			
			public void run() {
				for(int i=0; i< 10; i++) {
					LoggingPoolObject queue = null;
					try {
						queue = manager.borrowObject();
						String uuid = UUID.randomUUID().toString() + "-" + i;
						queue.putMessage(uuid);
					} catch (Exception e) {
						System.out.println("ERROR -" + e.getMessage());
					}
					finally {					
						try {
							manager.returnObject(queue);
						} catch (Exception e) {
							System.out.println("ERROR -" + e.getMessage());
						}
					}
				}
			}
		};
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			service.submit(task);
		}
//		System.out.println("MAIN : Wait 1 secs.");
//		try {
//			Thread.sleep(1 * 1000);
//		} catch (InterruptedException e) {
//			;
//		}
		System.out.println("MAIN : <-- manager.shutdown");
		manager.shutdown();
		
		service.shutdown();
	}
	public static void main(String[] args) {
		testMulti();
	}
}
