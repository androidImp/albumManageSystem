package util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataUtil {
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
		ObservableList<String> list = FXCollections.observableArrayList();
		for (String string : strings) {
			list.add(string);
		}
		return list;
	}

	public static String parseListToString(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String string : list) {
			builder.append(STRING_SEPARATOR);
		}
		// 删除最后一个分隔符
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}
}
