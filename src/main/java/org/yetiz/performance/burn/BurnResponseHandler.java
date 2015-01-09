package org.yetiz.performance.burn;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yeti on 15/1/8.
 */
public class BurnResponseHandler extends AsyncCompletionHandler<Response> {

	private AtomicLong completedCount;
	private AtomicLong deltaTimeSum;
	private AtomicLong dropCount;
	private long startTime = 0;

	public BurnResponseHandler(AtomicLong completedCount, AtomicLong deltaTimeSum, AtomicLong dropCount) {
		this.completedCount = completedCount;
		this.deltaTimeSum = deltaTimeSum;
		this.dropCount = dropCount;
	}

	public BurnResponseHandler setStartTime(long startTime) {
		this.startTime = startTime;
		return this;
	}

	@Override
	public Response onCompleted(Response response) throws Exception {
		completedCount.incrementAndGet();
		deltaTimeSum.addAndGet(System.currentTimeMillis() - startTime);
		return response;
	}

	@Override
	public void onThrowable(Throwable t) {
		dropCount.incrementAndGet();
	}
}
