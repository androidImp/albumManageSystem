package util;

public class DataUtil {
	public static String convertSizeToString(double size){
		int level = 0;
		double cur,tmp;
		cur = tmp = size;
		while(cur > 0){
			tmp = cur;
			cur /= 1024;
			level ++;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%.2f", tmp));
		if(level == 0){
			builder.append("B");
		}else if(level == 1){
			builder.append("KB");
		}else if(level == 2){
			builder.append("MB");
		}else if(level == 3){
			builder.append("GB");
		}else{
			//应该到达不了 TB 级别
		}
		return builder.toString();
	}
}
