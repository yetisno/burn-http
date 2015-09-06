package org.yetiz.performance.burn;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yeti on 15/9/6.
 */
public class AsyncSender implements Sender {
	protected volatile Counter counter = Counter.instance();
	protected Bootstrap bootstrap;
	protected volatile Channel channel;
	private int timeout = 3000;

	public AsyncSender(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	private void send(Req req) {
		try {
			URI uri = new URI(req.getUrl());
			channel = bootstrap.connect(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()).syncUninterruptibly().channel();
			FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(req.getMethod().toUpperCase()), uri.getPath());
			HttpHeaders headers = request.headers();
			headers.set("Host", String.format("%s:%d", uri.getHost(), uri.getPort()));
			for (Map.Entry<String, String> entry : req.getHeaders().entrySet()) {
				headers.set(entry.getKey(), entry.getValue());
			}
			if (request.getMethod().equals(HttpMethod.POST)) {
				request.content().writeBytes(req.getBody().getBytes());
			}
			channel.writeAndFlush(request);
		} catch (Exception e) {
			counter.fail().getAndIncrement();
			return;
		}
	}

	public void start(ArrayList<Req> reqList, int loopTimes) {
		for (int currentTimes = 0; currentTimes < loopTimes; currentTimes++) {
			for (Req req : reqList) {
				try {
					while (!counter.semaphore().tryAcquire(1, TimeUnit.SECONDS)) {
					}
				} catch (InterruptedException e) {
				}
				counter.send().getAndIncrement();
				send(req);
			}
		}
	}
}
