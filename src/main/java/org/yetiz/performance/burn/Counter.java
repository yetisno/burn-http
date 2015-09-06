package org.yetiz.performance.burn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yeti on 15/9/6.
 */
public class Counter {
	protected static Counter counter = null;
	protected static ReentrantLock lock = new ReentrantLock(false);
	protected volatile AtomicLong send = new AtomicLong(0);
	protected volatile AtomicLong success = new AtomicLong(0);
	protected volatile AtomicLong fail = new AtomicLong(0);
	protected volatile AtomicLong receiveSizeSum = new AtomicLong(0);
	protected volatile AtomicLong receiveTimeSum = new AtomicLong(0);
	protected volatile Map<Integer, AtomicLong> status = new HashMap<>();
	protected volatile Semaphore semaphore;

	private Counter() {
	}

	public static Counter instance() {
		if (counter == null) {
			lock.lock();
			if (counter == null) {
				counter = new Counter();
			}
			lock.unlock();
		}
		return counter;
	}

	public Map<Integer, AtomicLong> status() {
		return status;
	}

	public AtomicLong receiveTimeSum() {
		return receiveTimeSum;
	}

	public AtomicLong receiveSizeSum() {
		return receiveSizeSum;
	}


	public AtomicLong send() {
		return send;
	}

	public AtomicLong success() {
		return success;
	}

	public AtomicLong fail() {
		return fail;
	}
}
