package util;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.binding.StringFormatter;

import cluster.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Album;
import model.Photo;

public class DBUtil {
	private static Connection connection;
	private static PreparedStatement statement;
	// albums 表中元素的列号
	public static int ID = 1;
	public static int NAME = 2;
	public static int PROFILE = 3;
	public static int COVERURI = 4;
	public static int CREATEDATE = 5;
	public static int PHOTONUMBER = 6;
	public static int SIZE = 7;
	// photos 表中元素的列号
	public static int PHOTOSURI = 8;
	public static int MD5 = 1;
	public static int PHOTO_ID = 2;
	public static int PHOTO_NAME = 3;
	public static int PHOTO_URI = 4;
	public static int PHOTO_CREATEDATE = 5;
	public static int PHOTO_PROFILE = 6;
	public static int PHOTO_SIZE = 7;

	public static void getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法打开数据库");
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:album.db");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法创建数据库连接");
			e.printStackTrace();
		}
	}

	public static void createAlbumsTable(String ownerName) {
		String sql_create = "create table if not exists albums_" + ownerName + "(id integer primary key,"
				+ "name text not null," + "profile text,coverUri text,createDate text,"
				+ "photoNumber integer,size real," + "photosUri text)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_create);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法创建数据库查询");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}

	}

	public static void saveAlbums(List<Album> albums, String username) {
		getConnection();
		String sql_select = "select id from albums_" + username + " where id=?";
		String sql_update = "update albums_" + username
				+ " set name=?,profile=?,coverUri=?,createDate=?,photoNumber=?,size=?,photosUri=? where id=?";
		String sql_insert = "insert into albums_" + username
				+ "(id,name,profile,coverUri,createDate,photoNumber,size,photosUri) values(?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement update = connection.prepareStatement(sql_update);
			PreparedStatement insert = connection.prepareStatement(sql_insert);
			for (Album album : albums) {
				PreparedStatement select = connection.prepareStatement(sql_select);
				select.setInt(1, album.getId());
				ResultSet resultSet = select.executeQuery();
				resultSet.next();
				if (resultSet.getRow() == 0) {
					insert.setInt(1, album.getId());
					insert.setString(2, album.getAlbumName());
					insert.setString(3, album.getAlbumProfile());
					insert.setString(4, album.getCoverUri());
					insert.setString(5, album.getCreateDate());
					insert.setInt(6, album.getPhotosNumber());
					insert.setDouble(7, album.getSize());
					insert.setString(8, DataUtil.parseListToString(album.getPhotosUri()));
					select.close();
					insert.executeUpdate();
				} else {
					update.setString(1, album.getAlbumName());
					update.setString(2, album.getAlbumProfile());
					update.setString(3, album.getCoverUri());
					update.setString(4, album.getCreateDate());
					update.setInt(5, album.getPhotosNumber());
					update.setDouble(6, album.getSize());
					update.setString(7, DataUtil.parseListToString(album.getPhotosUri()));
					update.setInt(8, album.getId());
					select.close();
					update.executeUpdate();
				}
			}
			insert.close();
			update.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "保存相册信息出错");
			e.printStackTrace();
		} finally {
			// releaseConnection(getCurrentMethod());
		}
	}

	public static void savePhotos(List<Photo> photos, String username) {
		getConnection();
		String sql_select = "select md5,id from photos_" + username + " where md5 = ? and id = ?";
		String sql_insert = "insert into photos_" + username
				+ "(md5,id,name,uri,createDate,profile,size) values(?,?,?,?,?,?,?)";
		String sql_update = "update photos_" + username
				+ " set name=?,uri=?,createDate=?,profile=?,size=? where md5=? and id = ?";
		try {
			PreparedStatement insert = connection.prepareStatement(sql_insert);
			PreparedStatement update = connection.prepareStatement(sql_update);
			for (Photo photo : photos) {
				PreparedStatement select = connection.prepareStatement(sql_select);
				select.setString(1, photo.getMd5());
				select.setInt(2, photo.getId());
				ResultSet resultSet = select.executeQuery();
				resultSet.next();
				if (resultSet.getRow() == 0) {
					insert.setString(1, photo.getMd5());
					insert.setInt(2, photo.getId());
					insert.setString(3, photo.getName());
					insert.setString(4, photo.getUri());
					insert.setString(5, photo.getCreateDate());
					insert.setString(6, photo.getProfile());
					insert.setDouble(7, photo.getSize());
					select.close();
					insert.executeUpdate();
				} else {
					update.setString(1, photo.getName());
					update.setString(2, photo.getUri());
					update.setString(3, photo.getCreateDate());
					update.setString(4, photo.getProfile());
					update.setDouble(5, photo.getSize());
					update.setString(6, photo.getMd5());
					update.setInt(7, photo.getId());
					select.close();
					update.executeUpdate();

				}

			}
			insert.close();
			update.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法保存相册信息");
		}
	}

	public static ObservableList<Album> getAlbums(String username) {
		getConnection();
		List<Album> albums = new ArrayList<>();
		String sql_select = "select * from albums_" + username;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql_select);
			rs = statement.executeQuery();
			while (rs.next()) {
				Album album = new Album();
				album.setId(rs.getInt(ID));
				album.setAlbumName(rs.getString(NAME));
				album.setAlbumProfile(rs.getString(PROFILE));
				album.setCoverUri(rs.getString(COVERURI));
				album.setCreateDate(rs.getString(CREATEDATE));
				album.setSize(rs.getDouble(SIZE));
				ObservableList<String> photosUri = DataUtil.parseUrlToList(rs.getString(PHOTOSURI));
				album.setPhotosUri(photosUri);
				albums.add(album);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "获取相册时出错");
		} finally {
			releaseConnection(getCurrentMethod());
		}
		return FXCollections.observableArrayList(albums);
	}

	public static ObservableList<Photo> queryPhotoByName(String photoName, String username) {
		getConnection();
		String sql_select = "select * from photos_" + username + " where name like '%" + photoName + "%'";
		ResultSet rs = null;
		List<Photo> photos = new ArrayList<>();
		try {
			statement = connection.prepareStatement(sql_select);
			rs = statement.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				String md5 = rs.getString(MD5);
				int id = rs.getInt(PHOTO_ID);
				String name = rs.getString(PHOTO_NAME);
				String uri = rs.getString(PHOTO_URI);
				String date = rs.getString(PHOTO_CREATEDATE);
				String profile = rs.getString(PHOTO_PROFILE);
				double size = rs.getDouble(PHOTO_SIZE);
				photo.setMd5(md5);
				photo.setId(id);
				photo.setName(name);
				photo.setUri(uri);
				photo.setCreateDate(date);
				photo.setProfile(profile);
				photo.setSize(size);
				photos.add(photo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "查询相册时出错");
		} finally {
			releaseConnection(getCurrentMethod());
		}
		return FXCollections.observableArrayList(photos);
	}

	public static void createPhotosTable(String username) {
		String sql = "create table if not exists photos_" + username + "(md5 text," + "id integer not null,"
				+ "name text not null," + "uri text,createDate text,profile text," + "size real,primary key(md5,id))";
		getConnection();
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法创建数据库查询");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static ObservableList<Photo> getPhotosByAlbum(int index, String username) {
		String sql_select = "select * from photos_" + username + " where id = " + index;
		List<Photo> photos = new ArrayList<>();
		ResultSet rs;
		getConnection();
		try {
			statement = connection.prepareStatement(sql_select);
			rs = statement.executeQuery();
			while (rs.next()) {
				Photo photo = new Photo();
				photo.setMd5(rs.getString(MD5));
				photo.setId(rs.getInt(PHOTO_ID));
				photo.setName(rs.getString(PHOTO_NAME));
				photo.setUri(rs.getString(PHOTO_URI));
				photo.setCreateDate(rs.getString(PHOTO_CREATEDATE));
				photo.setProfile(rs.getString(PHOTO_PROFILE));
				photo.setSize(rs.getDouble(PHOTO_SIZE));
				photos.add(photo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法获取相册信息");
			e.printStackTrace();

		} finally {
			releaseConnection(getCurrentMethod());
		}
		return FXCollections.observableArrayList(photos);
	}

	public static void deleteAlbum(int id, String username) {
		String sql_delete = "delete from albums_" + username + " where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法删除相册");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void deletePhoto(String md5, int id, String username) {
		String sql_delete = "delete from photos_" + username + " where md5 = ? and id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setString(1, md5);
			statement.setInt(2, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法删除相片");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void deletePhotoByAlbum(int id, String username) {
		getConnection();
		String sql_delete = "delete from photos_" + username + " where id = ?";
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法删除相片");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	/**
	 * 验证用户的账户和密码
	 * 
	 * @param name
	 *            用户账户
	 * @param password
	 *            用户密码使用 MD5 {@link MessageDigest} 算法编码过的字符串
	 * @return
	 */
	public static boolean verifyUser(String name, String password) {
		String sql_query = "select id from users where name = ? and password = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, name);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			rs.next();
			if (rs.getRow() == 0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		} finally {
			releaseConnection(getCurrentMethod());
		}
		return false;
	}

	/**
	 * 创建用户数据表用于验证用户信息
	 */
	public static void createUsersTable() {
		String sql_create = "create table if not exists users(id integer primary key autoincrement,"
				+ "name text unique not null,password text not null,nickname text not null)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_create);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void addUser(String name, String password, String nickname) {
		String sql_insert = "insert into users(name,password,nickname) values(?,?,?)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_insert);
			statement.setString(1, name);
			statement.setString(2, password);
			statement.setString(3, nickname);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法添加新用户");
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	private static void releaseConnection(String curPos) {
		try {
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.e(curPos, "无法关闭数据库连接");
		}
	}

	public static String getCurrentMethod() {
		return Thread.currentThread().getStackTrace()[1].getMethodName();
	}
	/**
	 * 创建属于当前用户的直方图存储.
	 * @param name 当前用户姓名
	 */
	public static void createExpressionsTable(String name) {
		String sql_create = "create table if not exists expressions_" + name
				+ " (id integer,md5 text,uri text,expression text,primary key(id,md5))";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_create);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			releaseConnection(getCurrentMethod());
		}
	}
	public static void deleteExpressionsOfAlbum(String name,int id){
		String sql_delete = "delete from expressions_" + name + " where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			releaseConnection(getCurrentMethod());
		}
		
	}
	public static void deleteExpressionOfPhoto(String name,int id,String md5){
		String sql_delete = "delete from expressions_" + name + " where id = ? and md5 = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.setString(2, md5);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			releaseConnection(getCurrentMethod());
		}
	}
	/**
	 * 查询当前用户数据库中的所有直方图表示的集合并返回
	 * @param name 当前用户姓名
	 * @param dimension 直方图表示的维度
	 * @return
	 */
	public static List<Point> getExpressions(String name, int dimension) {
		String sql_query = "select uri,expression from expressions_" + name;
		getConnection();
		List<Point> points = new ArrayList<>();
		try {
			statement = connection.prepareStatement(sql_query);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Point point = new Point(DataUtil.expressionTodoubleArray(rs.getString(2), dimension));
				point.setUrl(rs.getString(1));
				points.add(point);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}

		return points;
	}
	/**
	 * 通过给定的相册 id 和 图片的 md5 查询该图片的直方图表示并返回
	 * @param name 当前用户姓名
	 * @param dimension 直方图表示的维度
	 * @param id 相片所属的相册 id
	 * @param md5 相片的 md5
	 * @return
	 */
	public static double[] queryExpression(String name, int dimension, int id, String md5) {
		String sql_query = "select expression from expressions_" + name + " where id = ? and md5 = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setInt(1, id);
			statement.setString(2, md5);
			ResultSet rs = statement.executeQuery();
			rs.next();
			if(rs.getRow() != 0){
				String string = rs.getString(1);
				return DataUtil.expressionTodoubleArray(string, dimension);
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
		return null;
	}
	/**
	 * 向数据库中添加当前图像的直方图表示
	 * @param name 当前用户姓名
	 * @param photo 存储的图片对象
	 * @param expression 需要存储的直方图表示
	 */
	public static void addExpression(String name, int id,String md5,String uri, String expression) {
		String sql_insert = "insert into expressions_" + name + " (id,md5,uri,expression) values(?,?,?,?)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_insert);
			statement.setInt(1, id);
			statement.setString(2, md5);
			statement.setString(3, uri);
			statement.setString(4, expression);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}
}
