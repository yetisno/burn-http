package org.yetiz.performance.burn;

/**
 * Created by yeti on 15/1/5.
 */
public interface BurnThreadCountable extends Runnable {

	public Long getRoundCountAndReset();

	public Long getRoundDeltaTimeSumAndReset();

	public Long getRoundDropCountAndReset();

}
