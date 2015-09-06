package org.yetiz.performance.burn;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpResponse;

import java.util.concurrent.Semaphore;

/**
 * Created by yeti on 15/9/6.
 */
public class SyncResultHandler extends ChannelDuplexHandler {
	Counter counter = Counter.instance();
	Semaphore semaphore = new Semaphore(1);
	volatile boolean done = false;
	long startTime = 0;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		semaphore.acquire();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (!done) {
			counter.fail().getAndIncrement();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpMessage response = ((FullHttpMessage) msg);
		int status = ((HttpResponse) msg).getStatus().code();
		if (status != 200) {
			counter.fail.getAndIncrement();
			release(response);
			return;
		}
		int dataLength = response.content().capacity();
		if (dataLength == 0 || dataLength == 45) {
			counter.fail.getAndIncrement();
		} else {
			counter.success.getAndIncrement();
			counter.receiveTimeSum().addAndGet(System.currentTimeMillis() - startTime);
			counter.receiveSizeSum().addAndGet(dataLength);
		}
		release(response);
	}

	private void release(FullHttpMessage response) {
		semaphore.release();
		response.release();
		done = true;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		counter.fail.getAndIncrement();
		semaphore.release();
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		startTime = System.currentTimeMillis();
		super.flush(ctx);
	}
}
