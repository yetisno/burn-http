package org.yetiz;

/**
 * Created by yeti on 2015/7/20.
 * Ref: https://github.com/yetisno/ACD-JAPI
 */

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yeti on 4/15/15.
 */
public class Log {
	private static Level level = LogManager.getRootLogger().getLevel();

	static {
		System.setProperty(YamlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log.yaml");
	}

	private static String getStackTraceString() {
		StringBuilder stringBuilder = new StringBuilder();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 3; i < stackTrace.length; i++) {
			StackTraceElement stackTraceElement = stackTrace[i];
			stringBuilder.append(stackTraceElement.toString());
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	private static Logger getLogger() {
		StackTraceElement ste = Thread.currentThread().getStackTrace().length >= 3 ?
			Thread.currentThread().getStackTrace()[3] :
			Thread.currentThread().getStackTrace()[2];
		return LoggerFactory.getLogger(ste.getClassName());
	}

	private static Logger getLogger(String className) {
		StackTraceElement ste = Thread.currentThread().getStackTrace().length >= 3 ?
			Thread.currentThread().getStackTrace()[3] :
			Thread.currentThread().getStackTrace()[2];
		return LoggerFactory.getLogger(className);
	}

	/**
	 * verbose
	 *
	 * @param description
	 */
	public static void t(String description) {
		getLogger().trace("{}", description);
	}

	/**
	 * debug
	 *
	 * @param LazyStringAggregator
	 */
	public static void t(LazyStringAggregator LazyStringAggregator) {
		if (level.isLessSpecificThan(Level.TRACE))
			getLogger().debug("{}", LazyStringAggregator.aggregate());
	}

	/**
	 * debug
	 *
	 * @param description
	 */
	public static void d(String description) {
		getLogger().debug("{}", description);
	}

	/**
	 * debug
	 *
	 * @param LazyStringAggregator
	 */
	public static void d(LazyStringAggregator LazyStringAggregator) {
		if (level.isLessSpecificThan(Level.DEBUG))
			getLogger().debug("{}", LazyStringAggregator.aggregate());
	}

	/**
	 * info
	 *
	 * @param description
	 */
	public static void i(String description) {
		getLogger().info("{}", description);
	}

	/**
	 * debug
	 *
	 * @param LazyStringAggregator
	 */
	public static void i(LazyStringAggregator LazyStringAggregator) {
		if (level.isLessSpecificThan(Level.INFO))
			getLogger().debug("{}", LazyStringAggregator.aggregate());
	}

	/**
	 * warning
	 *
	 * @param description
	 */
	public static void w(String description) {
		getLogger().warn("{}", description);
	}

	/**
	 * debug
	 *
	 * @param LazyStringAggregator
	 */
	public static void w(LazyStringAggregator LazyStringAggregator) {
		if (level.isLessSpecificThan(Level.WARN))
			getLogger().debug("{}", LazyStringAggregator.aggregate());
	}

	/**
	 * error
	 *
	 * @param description
	 */
	public static void e(String description) {
		getLogger().error("{}", description);
	}

	/**
	 * debug
	 *
	 * @param LazyStringAggregator
	 */
	public static void e(LazyStringAggregator LazyStringAggregator) {
		if (level.isLessSpecificThan(Level.ERROR))
			getLogger().debug("{}", LazyStringAggregator.aggregate());
	}

	/**
	 * verbose
	 *
	 * @param name
	 * @param description
	 */
	public static void t(String name, String description) {
		getLogger().trace("{}: {}", name, description);
	}

	/**
	 * debug
	 *
	 * @param name
	 * @param description
	 */
	public static void d(String name, String description) {
		getLogger().debug("{}: {}", name, description);
	}

	/**
	 * info
	 *
	 * @param name
	 * @param description
	 */
	public static void i(String name, String description) {
		getLogger().info("{}: {}", name, description);
	}

	/**
	 * warning
	 *
	 * @param name
	 * @param description
	 */
	public static void w(String name, String description) {
		getLogger().warn("{}: {}", name, description);
	}

	/**
	 * error
	 *
	 * @param name
	 * @param description
	 */
	public static void e(String name, String description) {
		getLogger().error("{}: {}", name, description);
	}

	/**
	 * verbose
	 *
	 * @param description
	 */
	public static void t(Class clazz, String description) {
		getLogger(clazz.getName()).trace("{}", description);
	}

	/**
	 * debug
	 *
	 * @param description
	 */
	public static void d(Class clazz, String description) {
		getLogger(clazz.getName()).debug("{}", description);
	}

	/**
	 * info
	 *
	 * @param description
	 */
	public static void i(Class clazz, String description) {
		getLogger(clazz.getName()).info("{}", description);
	}

	/**
	 * warning
	 *
	 * @param description
	 */
	public static void w(Class clazz, String description) {
		getLogger(clazz.getName()).warn("{}", description);
	}

	/**
	 * error
	 *
	 * @param description
	 */
	public static void e(Class clazz, String description) {
		getLogger(clazz.getName()).error("{}", description);
	}
}
