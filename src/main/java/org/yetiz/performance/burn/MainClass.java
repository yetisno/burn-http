package org.yetiz.performance.burn;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yeti on 15/1/5.
 */
public class MainClass {

	private static Logger logger = Logger.getLogger(MainClass.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 4) {
			System.out.println("java -jar target/BurnHTTP.jar <Url File Path> <sync/async> <Thread Count> <# of " +
				"Request Per Second of Each Thread>");
			System.exit(0);
		}
		String urlFilePath = args[0];
		boolean isSync = args[1].toUpperCase().equals("SYNC");
		int threadCount = Integer.parseInt(args[2]);
		int requestPerSecond = Integer.parseInt(args[3]);
		ArrayList<BurnThreadCountable> burnThreads = new ArrayList<BurnThreadCountable>();
		System.out.println("Sync Mode:      " + (isSync ? "True" : "False"));
		System.out.println("File Path:      " + urlFilePath);
		System.out.println("Thread Count:   " + threadCount);
		System.out.println("#RPSET:         " + requestPerSecond);
		System.out.println("Conn. timeout:  " + 5000);
		System.out.println("Retry:          " + 0);
		CountDownLatch preparedSignal = new CountDownLatch(threadCount);
		System.out.println("Prepare Threads...");
		logger.info("urlFilePath: " + urlFilePath);
		logger.info("Sync: " + isSync);
		logger.info("threadCount: " + threadCount);
		logger.info("requestPerSecond: " + requestPerSecond);
		for (int i = 0; i < threadCount; i++) {
			logger.info("Start Thread " + i);
			Thread burnThread =
				isSync ?
					new SyncBurnThread(urlFilePath, requestPerSecond, preparedSignal) :
					new ASyncBurnThread(urlFilePath, requestPerSecond, preparedSignal);
			burnThreads.add(((BurnThreadCountable) burnThread));
			burnThread.start();
		}
		preparedSignal.await();
		System.out.println("All Prepared!\nStart!");

		long currentCount = 0, currentDeltaTimeSum = 0, dropCount = 0;
		long lastTime = System.currentTimeMillis();
		StringBuilder stringBuilder = new StringBuilder();
		while (true) {
			stringBuilder.setLength(0);
			long deltaTime = System.currentTimeMillis() - lastTime;
			if (deltaTime < 1000) {
				try {
					Thread.sleep(1000 - deltaTime);
				} catch (Exception e) {
				}
			}
			for (Iterator<BurnThreadCountable> iterator = burnThreads.iterator(); iterator.hasNext(); ) {
				BurnThreadCountable burnThread = iterator.next();
				currentCount += burnThread.getRoundCountAndReset();
				currentDeltaTimeSum += burnThread.getRoundDeltaTimeSumAndReset();
				dropCount += burnThread.getRoundDropCountAndReset();
			}
			stringBuilder.append(Calendar.getInstance().getTime().toString());
			stringBuilder.append(" [tps: " + currentCount + "] ");
			if (currentCount != 0)
				stringBuilder.append("[AVG: " + new DecimalFormat("#0.00").format((new Double(currentDeltaTimeSum)
					/ new Double(currentCount))) + " ms.] ");
			stringBuilder.append("[Drp: " + dropCount + "]");
			System.out.println(stringBuilder.toString());
			logger.info(stringBuilder.toString()
			);
			lastTime = System.currentTimeMillis();
			currentCount = 0;
			currentDeltaTimeSum = 0;
			dropCount = 0;
		}
	}
}
