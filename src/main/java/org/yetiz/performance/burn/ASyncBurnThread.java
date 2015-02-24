package org.yetiz.performance.burn;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yeti on 15/1/5.
 */
public class ASyncBurnThread extends BurnThread {

	private static AsyncHttpClient asyncHttpClient;
	protected int requestPerSecond;
	protected CountDownLatch preparedSignal;
	protected ArrayList<Req> reqList;
	protected ArrayList<Request> requestArrayList;

	static {
		AsyncHttpClientConfig asyncHttpClientConfig = new AsyncHttpClientConfig.Builder()
			.setRequestTimeout(4000)
			.setConnectTimeout(1000)
			.setMaxRequestRetry(1)
			.setIOThreadMultiplier(4)
			.build();
		asyncHttpClient = new AsyncHttpClient(asyncHttpClientConfig);
	}

	/**
	 * Allocates a new {@code Thread} object. This constructor has the same
	 * effect as {@linkplain #Thread(ThreadGroup, Runnable, String) Thread}
	 * {@code (null, null, gname)}, where {@code gname} is a newly generated
	 * name. Automatically generated names are of the form
	 * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
	 */
	public ASyncBurnThread(String reqFilePath, Integer requestPerSecond, CountDownLatch preparedSignal) {
		this.reqList = ReqList.getReqList(reqFilePath);
		this.requestPerSecond = requestPerSecond;
		this.preparedSignal = preparedSignal;
		requestArrayList = new ArrayList<Request>();
		roundCount = new AtomicLong(0);
		roundDeltaTimeSum = new AtomicLong(0);
		roundDropCount = new AtomicLong(0);
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
		if (reqList == null) {
			System.out.println("no file!");
			System.exit(0);
		}
		for (Iterator<Req> iterator = reqList.iterator(); iterator.hasNext(); ) {
			Req req = iterator.next();
			RequestBuilder requestBuilder = new RequestBuilder(req.getMethod())
				.setUrl(req.getUrl())
				.setBody(req.getBody());
			for (Map.Entry<String, String> entry : req.getHeaders().entrySet()) {
				requestBuilder.setHeader(entry.getKey(), entry.getValue());
			}
			requestArrayList.add(asyncHttpClient.prepareRequest(requestBuilder.build()).build());
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
							sleep(1000 - deltaTime);
						} catch (Exception e) {
						}
					}
					currentRoundRPSCount = 0;
					lastTime = System.currentTimeMillis();
				}
				asyncHttpClient.executeRequest(iterator.next(), new BurnResponseHandler(roundCount,
					roundDeltaTimeSum, roundDropCount).setStartTime(System.currentTimeMillis()));
				currentRoundRPSCount++;
			}
		}
	}
}
