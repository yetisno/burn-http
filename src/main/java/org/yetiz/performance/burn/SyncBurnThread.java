package org.yetiz.performance.burn;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yeti on 15/1/5.
 */
public class SyncBurnThread extends BurnThread {

	private AsyncHttpClient asyncHttpClient;
	protected int requestPerSecond;
	protected CountDownLatch preparedSignal;
	protected ArrayList<String> urlList;
	protected ArrayList<Request> requestArrayList;


	/**
	 * Allocates a new {@code Thread} object. This constructor has the same
	 * effect as {@linkplain #Thread(ThreadGroup, Runnable, String) Thread}
	 * {@code (null, null, gname)}, where {@code gname} is a newly generated
	 * name. Automatically generated names are of the form
	 * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
	 */
	public SyncBurnThread(String urlFilePath, Integer requestPerSecond, CountDownLatch preparedSignal) {
		this.urlList = UrlList.getUrlList(urlFilePath);
		this.requestPerSecond = requestPerSecond;
		this.preparedSignal = preparedSignal;
		requestArrayList = new ArrayList<Request>();
		roundCount = new AtomicLong(0);
		roundDeltaTimeSum = new AtomicLong(0);
		roundDropCount = new AtomicLong(0);
		AsyncHttpClientConfig asyncHttpClientConfig = new AsyncHttpClientConfig.Builder()
			.setRequestTimeout(4000)
			.setConnectTimeout(1000)
			.setMaxRequestRetry(1)
			.setIOThreadMultiplier(1)
			.build();
		asyncHttpClient = new AsyncHttpClient(asyncHttpClientConfig);
	}

	/**
	 * If this thread was constructed using a separate
	 * <code>Runnable</code> run object, then that
	 * <code>Runnable</code> object's <code>run</code> method is called;
	 * otherwise, this method does nothing and returns.
	 * <p/>
	 * Subclasses of <code>Thread</code> should override this method.
	 *
	 * @see #start()
	 * @see #stop()
	 * @see #Thread(ThreadGroup, Runnable, String)
	 */
	@Override
	public void run() {
		if (urlList == null) {
			System.out.println("no file!");
			System.exit(0);
		}
		for (Iterator<String> iterator = urlList.iterator(); iterator.hasNext(); ) {
			requestArrayList.add(asyncHttpClient.prepareGet(iterator.next()).build());
		}
		try {
			preparedSignal.countDown();
			preparedSignal.await();
		} catch (InterruptedException e) {
		}
		long lastTime = System.currentTimeMillis(), deltaTime = 0;
		int currentRoundRPSCount = 0;
		while (true) {
			for (Iterator<Request> iterator = requestArrayList.iterator(); iterator.hasNext(); ) {
				if (currentRoundRPSCount == requestPerSecond) {
					deltaTime = System.currentTimeMillis() - lastTime;
					if (deltaTime < 1000) {
						try {
							Thread.sleep(1000 - deltaTime);
						} catch (Exception e) {
						}
					}
					currentRoundRPSCount = 0;
					lastTime = System.currentTimeMillis();
				}
				try {
					asyncHttpClient.executeRequest(iterator.next(), new BurnResponseHandler(roundCount,
						roundDeltaTimeSum, roundDropCount)
						.setStartTime(System.currentTimeMillis())).get();
				} catch (InterruptedException e) {
					roundDeltaTimeSum.incrementAndGet();
				} catch (ExecutionException e) {
					roundDeltaTimeSum.incrementAndGet();
				}
				currentRoundRPSCount++;
			}
		}
	}
}
