package org.yetiz.performance.burn;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yeti on 15/1/5.
 */
public class BurnThread extends Thread implements BurnThreadCountable {
	protected AtomicLong roundCount;
	protected AtomicLong roundDeltaTimeSum;
	protected AtomicLong roundDropCount;

	public Long getRoundCountAndReset() {
		Long rtn = roundCount.getAndSet(0);
		return rtn;
	}

	public Long getRoundDeltaTimeSumAndReset() {
		Long rtn = roundDeltaTimeSum.getAndSet(0);
		return rtn;
	}

	public Long getRoundDropCountAndReset() {
		Long rtn = roundDropCount.getAndSet(0);
		return rtn;
	}
}
