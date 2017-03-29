package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.Album;

public class DBUtil {
	private static Connection connection;
	private static PreparedStatement statement;
	public static int ID = 0;
	public static int NAME = 1;
	public static int PROFILE = 2;
	public static int COVERURI = 3;
	public static int CREATEDATE = 4;
	public static int PHOTONUMBER = 5;
	public static int SIZE = 6;
	public static int PHOTOSURI = 7;

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
				+ "profile text,coverUri text,createDate text," + "photoNumber integer,size real," + "photosUri text)";
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.e(DBUtil.class.getName(), "无法创建数据库查询");
			e.printStackTrace();
		}
		try {
			statement.executeUpdate();
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

	public static void saveData(List<Album> albums) {
		getConnection();
		String sql_select = "select id from albums where id=?";
		String sql_update = "update albums set name=?,profile=?,coverUri=?,createDate=?,photoNumber=?,size=?,photosUri=? where id=?";
		String sql_insert = "insert into albums(id,name,profile,coverUri,createDate,photoNumber,size,photosUri) values(?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement select = connection.prepareStatement(sql_select);
			PreparedStatement update = connection.prepareStatement(sql_update);
			PreparedStatement insert = connection.prepareStatement(sql_insert);
			for (Album album : albums) {
				select.setInt(1, album.getId());
				ResultSet resultSet = select.executeQuery();
				if (resultSet.getRow() < 1) {
					insert.setInt(1, album.getId());
					insert.setString(2, album.getAlbumName());
					insert.setString(3, album.getAlbumProfile());
					insert.setString(4, album.getCoverUri());
					insert.setString(5, album.getCreateDate());
					insert.setInt(6, album.getPhotosNumber());
					insert.setDouble(7, album.getSize());
					insert.setString(8, DataUtil.parseListToString(album.getPhotosUri()));
					insert.executeUpdate();
				} else {
					System.out.println("update");
					update.setString(1, album.getAlbumName());
					update.setString(2, album.getAlbumProfile());
					update.setString(3, album.getCoverUri());
					update.setString(4, album.getCreateDate());
					update.setInt(5, album.getPhotosNumber());
					update.setDouble(6, album.getSize());
					update.setString(7, DataUtil.parseListToString(album.getPhotosUri()));
					update.setInt(8, album.getId());
					insert.executeUpdate();
				}

			}
			insert.close();
			select.close();
			update.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteAlbum(int id) {
		String sql_delete = "delete from albums where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
			connection.close();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ResultSet queryPhotoByName(String name) {
		getConnection();
		String sql_select = "select * from photos where name like %" + name + "%";
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql_select);
			rs = statement.executeQuery();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
}
