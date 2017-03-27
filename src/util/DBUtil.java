package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	private static Connection connection;
	private static Statement statement;

	public static void getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "无法打开数据库");
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:album.db");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "无法创建数据库连接");
			e.printStackTrace();
		}
	}

	public static void createTable() {
		String sql = "create table if not exists albums " + "(id integer primary key," + "name text not null,"
				+ "profile text,coverUri text,createDate text," + "photoNumber integer,size real" + "photosUri text)";
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "无法创建数据库查询");
			e.printStackTrace();
		}
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "无法执行对数据库的查询");
			e.printStackTrace();
		}
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "在关闭数据库资源时出错");
			e.printStackTrace();
		}

	}
}
