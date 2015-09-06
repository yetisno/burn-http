package org.yetiz.performance.burn;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by yeti on 15/9/6.
 */
public class SyncInitializer extends ChannelInitializer {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		SyncResultHandler resultHandler = new SyncResultHandler();
		HttpClientCodec hCodec = new HttpClientCodec();
		ChannelPipeline p = ch.pipeline();
		p.addLast(new LoggingHandler(LogLevel.DEBUG));
		p.addLast(hCodec);
		p.addLast(new HttpObjectAggregator(10408576));
		p.addLast("SH", resultHandler);
	}
}
