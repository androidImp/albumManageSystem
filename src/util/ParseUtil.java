package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ParseUtil {
	public static String STRING_SEPARATOR = ",";

	public static String convertSizeToString(double size) {
		int level = 0;
		double cur;
		cur = size;
		while (cur > 1024) {
			cur /= 1024;
			level++;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%.2f", cur));
		if (level == 0) {
			builder.append("B");
		} else if (level == 1) {
			builder.append("KB");
		} else if (level == 2) {
			builder.append("MB");
		} else if (level == 3) {
			builder.append("GB");
		} else {
			// 应该到达不了 TB 级别
		}
		return builder.toString();
	}

	public static ObservableList<String> parseUrlToList(String uris) {
		String[] strings = uris.split(STRING_SEPARATOR);
		List<String> list = new ArrayList<>();
		for (String string : strings) {
			list.add(string);
		}
		if (list.size() == 1 && list.get(0).equals("")) {
			list.remove(0);
		}
		return FXCollections.observableArrayList(list);
	}

	public static String parseListToString(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String string : list) {
			builder.append(string);
			builder.append(STRING_SEPARATOR);
		}
		// 删除最后一个分隔符
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		// System.out.println("还原 uri 列表: " + builder.toString() + " 长度: " +
		// builder.length());
		return builder.toString();
	}

	public static String getMD5(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		digest.update(password.getBytes());
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	public static String getMD5(File file) {
		if (!file.exists() || !file.isFile()) {
			LogUtil.e(ParseUtil.class.getName(), "当前地址的文件已经移动或者删除,无法获取!");
			return null;
		}

		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(ParseUtil.class.getName(), "使用的错误的加密算法名称");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.e(ParseUtil.class.getName(), "文件读取错误!");
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	public static double[] expressionTodoubleArray(String expression, int dimension) {
		double[] ex = new double[dimension];
		
		String[] array = expression.split(",");
		for (int i = 0; i < array.length; i++) {
			ex[i] = Double.parseDouble(array[i]);
		}
		return ex;
	}

	public static String doubleArrayToExpression(double[] expression) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < expression.length; i++) {
			builder.append(String.valueOf(expression[i]));
			builder.append(",");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
}
