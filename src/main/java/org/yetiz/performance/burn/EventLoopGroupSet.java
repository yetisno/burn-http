package org.yetiz.performance.burn;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yeti on 15/8/21.
 */
public class EventLoopGroupSet {
	private final static String OS = System.getProperty("os.name").toLowerCase();
	private Class serverSocketClass;
	private Class socketClass;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public EventLoopGroupSet(int bossCount, Integer workerCount) {
		if (OS.indexOf("win") > -1) {
			windowsSet(bossCount, workerCount);
		} else if (OS.indexOf("mac") > -1) {
			macSet(bossCount, workerCount);
		} else {
			linuxSet(bossCount, workerCount);
		}
	}

	private void linuxSet(int bossCount, Integer workerCount) {
		serverSocketClass = EpollServerSocketChannel.class;
		socketClass = EpollSocketChannel.class;
		if (bossCount > 0)
			bossGroup = new EpollEventLoopGroup(bossCount);
		workerGroup = null == workerCount ? new EpollEventLoopGroup() : new EpollEventLoopGroup(workerCount);
	}

	private void macSet(int bossCount, Integer workerCount) {
		serverSocketClass = NioServerSocketChannel.class;
		socketClass = NioSocketChannel.class;
		if (bossCount > 0)
			bossGroup = new NioEventLoopGroup(bossCount);
		workerGroup = null == workerCount ? new NioEventLoopGroup() : new NioEventLoopGroup(workerCount);
	}

	private void windowsSet(int bossCount, Integer workerCount) {
		serverSocketClass = NioServerSocketChannel.class;
		socketClass = NioSocketChannel.class;
		if (bossCount > 0)
			bossGroup = new NioEventLoopGroup(bossCount);
		workerGroup = null == workerCount ? new NioEventLoopGroup() : new NioEventLoopGroup(workerCount);
	}

	public Class getServerSocketClass() {
		return serverSocketClass;
	}

	public Class getSocketClass() {
		return socketClass;
	}

	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}

	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}

	public void gracefullyShutdown() {
		if (bossGroup != null)
			bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
