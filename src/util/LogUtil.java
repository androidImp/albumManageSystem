package util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
	/**
	 * 记录相关信息
	 * 
	 * @param 指示信息相关的类
	 * @param 想要输出的信息
	 */
	public static void i(String where, String msg) {
		Logger.getLogger(where).log(Level.INFO, msg);
	}

	/**
	 * 记录出错信息
	 * 
	 * @param 指示信息相关的类
	 * @param 想要输出的信息
	 */
	public static void e(String where, String msg) {
		Logger.getLogger(where).log(Level.SEVERE, msg);
	}

	/**
	 * 记录警告信息
	 * 
	 * @param 指示信息相关的类
	 * @param 想要输出的信息
	 */
	public static void w(String where, String msg) {
		Logger.getLogger(where).log(Level.WARNING, msg);
	}

	/**
	 * 记录调试信息
	 * 
	 * @param 指示信息相关的类
	 * @param 想要输出的信息
	 */
	public static void d(String where, String msg) {
		Logger.getLogger(where).log(Level.FINE, msg);
	}

	/**
	 * 记录配置信息
	 * 
	 * @param 指示信息相关的类
	 * @param 想要输出的信息
	 */
	public static void c(String where, String msg) {
		Logger.getLogger(where).log(Level.CONFIG, msg);
	}
}
