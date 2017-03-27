package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getFormatDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

}
