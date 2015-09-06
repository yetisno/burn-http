package org.yetiz.performance.burn;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by yeti on 15/9/6.
 */
public class AsyncResultHandler extends ChannelDuplexHandler {
	Counter counter = Counter.instance();
	volatile boolean done = false;
	long startTime = 0;

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (!done) {
//			counter.fail().getAndIncrement();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpMessage response = ((FullHttpMessage) msg);
		int status = ((HttpResponse) msg).getStatus().code();
		if (status != 200) {
			counter.fail.getAndIncrement();
			release(response);
			ctx.channel().close();
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
		ctx.channel().close();
	}

	private void release(FullHttpMessage response) {
		response.release();
		done = true;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		counter.send().decrementAndGet();
//		counter.fail.getAndIncrement();
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		startTime = System.currentTimeMillis();
		super.flush(ctx);
	}
}
