import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;
import org.yetiz.Log;
import org.yetiz.performance.burn.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yeti on 2015/8/13.
 */
public class Launcher {

	public static void main(String[] args) throws IOException, InterruptedException, IllegalAccessException, InstantiationException {
		System.setProperty(YamlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log.yaml");
		if (args.length != 4) {
			System.out.println("java -jar target/BurnHTTP.jar <Url File Path> <sync/async> <Thread Count> <# of " +
				"Request Per Second Per Thread");
			System.exit(0);
		}
		final ArrayList<Req> list = ReqList.getReqList(args[0]);
		final Class sender = args[1].toUpperCase().equals("SYNC") ? SyncSender.class : AsyncSender.class;
		final Class initializer = args[1].toUpperCase().equals("SYNC") ? SyncInitializer.class : AsyncInitializer.class;
		int threadCount = Integer.valueOf(args[2]);
		final int tps = Integer.valueOf(args[3]);
		Log.i(String.format("URL File: %s\n" +
				"Mode: %s\n" +
				"Threads: %d\n" +
				"Request Per Second: %d\n",
			args[0], args[1].toUpperCase(), threadCount, tps));
		EventLoopGroupSet loopGroupSet = new EventLoopGroupSet(0, threadCount);
		final Bootstrap bootstrap = new Bootstrap()
			.group(loopGroupSet.getWorkerGroup())
			.channel(loopGroupSet.getSocketClass())
			.handler(((ChannelInitializer) initializer.newInstance()))
			.option(ChannelOption.SO_RCVBUF, 130172)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.option(ChannelOption.SO_KEEPALIVE, true);
		Counter counter = Counter.instance();

		for (int i = 0; i < threadCount; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Sender syncSender = null;
					try {
						syncSender = (Sender) sender.getConstructor(Bootstrap.class).newInstance(bootstrap);
					} catch (Exception e) {
					}
					syncSender.start(list, tps);
				}
			}).start();
		}

		long currentSuccess = 0;
		while (true) {
			long send = counter.send().get();
			long success = counter.success().get();
			long fail = counter.fail().get();
			long timeSum = counter.receiveTimeSum().getAndSet(0);
			long sizeSum = counter.receiveSizeSum().getAndSet(0);
			long tmpSuccess = success;
			long sps = tmpSuccess - currentSuccess;
			currentSuccess = tmpSuccess;
			Log.i(String.format("SEND:%-10d, SUCCESS:%-10d, FAIL:%-10d, SPS:%-6d, TIME AVG.(MS):%-6d, SIZE AVG.(B):%-8d, SIZE SUM(B):%-10d",
				send,
				success,
				fail,
				sps,
				sps == 0 || timeSum == 0 ? 0 : timeSum / sps,
				sps == 0 || sizeSum == 0 ? 0 : (sizeSum / sps),
				sizeSum
			));
			Thread.sleep(1000);
		}
	}
}
