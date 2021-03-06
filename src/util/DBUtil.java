package util;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.rtf.RTFEditorKit;

import cluster.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.Album;
import model.Photo;
import model.User;
import search.KDSearchUtil;

public class DBUtil {

	private static Connection connection = null;
	private static PreparedStatement statement = null;
	// albums 表中元素的列号
	public final static int ID = 1;
	public final static int NAME = 2;
	public final static int PROFILE = 3;
	public final static int COVERURI = 4;
	public final static int CREATEDATE = 5;
	public final static int PHOTONUMBER = 6;
	public final static int SIZE = 7;
	public final static int PHOTOSURI = 8;
	// photos 表中元素的列号
	public final static int MD5 = 1;
	public final static int PHOTO_ID = 2;
	private static final int ALBUMNAME = 3;
	public final static int PHOTO_NAME = 4;
	public final static int PHOTO_URI = 5;
	public final static int PHOTO_CREATEDATE = 6;
	public final static int PHOTO_PROFILE = 7;
	public final static int PHOTO_SIZE = 8;

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
		String sql_create = "create table if not exists albums_" + ownerName + "(id integer primary key ,"
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
					insert.setString(8, ParseUtil.parseListToString(album.getPhotosUri()));
					select.close();
					insert.executeUpdate();
				} else {
					update.setString(1, album.getAlbumName());
					update.setString(2, album.getAlbumProfile());
					update.setString(3, album.getCoverUri());
					update.setString(4, album.getCreateDate());
					update.setInt(5, album.getPhotosNumber());
					update.setDouble(6, album.getSize());
					update.setString(7, ParseUtil.parseListToString(album.getPhotosUri()));
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

	/**
	 * 将该用户所用户的图片存储到数据库中;
	 * 
	 * @param photos
	 *            将要存储的图片对象
	 * @param username
	 *            图片的所有者
	 */
	public static void savePhotos(List<Photo> photos, String username) {
		getConnection();
		String sql_select = "select md5,id from photos_" + username + " where md5 = ? and id = ?";
		String sql_insert = "insert into photos_" + username
				+ "(md5,id,name,uri,createDate,profile,size,albumName) values(?,?,?,?,?,?,?,?)";
		String sql_update = "update photos_" + username
				+ " set name=?,uri=?,createDate=?,profile=?,size=?,albumname=? where md5=? and id = ?";
		PreparedStatement insert = null;
		PreparedStatement update = null;
		PreparedStatement select = null;
		try {
			insert = connection.prepareStatement(sql_insert);
			update = connection.prepareStatement(sql_update);
			for (Photo photo : photos) {
				if (photo.isModified()) {
					select = connection.prepareStatement(sql_select);
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
						insert.setString(8, photo.getAlbumName());
						select.close();
						insert.executeUpdate();
					} else {
						update.setString(1, photo.getName());
						update.setString(2, photo.getUri());
						update.setString(3, photo.getCreateDate());
						update.setString(4, photo.getProfile());
						update.setDouble(5, photo.getSize());
						update.setString(6, photo.getAlbumName());
						update.setString(7, photo.getMd5());
						update.setInt(8, photo.getId());
						select.close();
						update.executeUpdate();

					}
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法保存相册信息");
		} finally {
			if (select != null) {
				try {
					select.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (insert != null) {
				try {
					insert.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (update != null) {
				try {
					update.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			releaseConnection(getCurrentMethod());
		}
	}

	public static void savePhotosData(List<Photo> photos, String username) {
		getConnection();
		String sql_select = "select md5,id from photos_" + username + " where md5 = ? and id = ?";
		String sql_insert = "insert into photos_" + username
				+ "(md5,id,albumName,name,uri,createDate,profile,size) values(?,?,?,?,?,?,?,?)";
		String sql_update = "update photos_" + username
				+ " set name=?,albumName=?,uri=?,createDate=?,profile=?,size=? where md5=? and id = ?";
		PreparedStatement insert = null;
		PreparedStatement update = null;
		PreparedStatement select = null;
		try {
			insert = connection.prepareStatement(sql_insert);
			update = connection.prepareStatement(sql_update);
			connection.setAutoCommit(false);
			for (Photo photo : photos) {
				if (photo.isModified()) {
					select = connection.prepareStatement(sql_select);
					select.setString(1, photo.getMd5());
					select.setInt(2, photo.getId());
					ResultSet resultSet = select.executeQuery();
					resultSet.next();
					if (resultSet.getRow() == 0) {
						insert.setString(1, photo.getMd5());
						insert.setInt(2, photo.getId());
						insert.setString(3, photo.getAlbumName());
						insert.setString(4, photo.getName());
						insert.setString(5, photo.getUri());
						insert.setString(6, photo.getCreateDate());
						insert.setString(7, photo.getProfile());
						insert.setDouble(8, photo.getSize());
						select.close();
						insert.addBatch();
						// insert.executeUpdate();
					} else {
						update.setString(1, photo.getName());
						update.setString(2, photo.getAlbumName());
						update.setString(3, photo.getUri());
						update.setString(4, photo.getCreateDate());
						update.setString(5, photo.getProfile());
						update.setDouble(6, photo.getSize());
						update.setString(7, photo.getMd5());
						update.setInt(8, photo.getId());
						select.close();
						update.addBatch();
						// update.executeUpdate();

					}
				}

			}
			insert.executeBatch();
			update.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "无法保存相册信息");
		} finally {
			if (select != null) {
				try {
					select.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (insert != null) {
				try {
					insert.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (update != null) {
				try {
					update.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			releaseConnection(getCurrentMethod());
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
				album.setPhotosNumber(rs.getInt(PHOTONUMBER));
				ObservableList<String> photosUri = ParseUtil.parseUrlToList(rs.getString(PHOTOSURI));
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
				Photo photo = new Photo(rs.getInt(PHOTO_ID), rs.getString(ALBUMNAME), rs.getString(PHOTO_URI),
						rs.getString(PHOTO_CREATEDATE), rs.getString(PHOTO_NAME), rs.getString(MD5),
						rs.getDouble(PHOTO_SIZE), rs.getString(PHOTO_PROFILE));
				// String md5 = rs.getString(MD5);
				// int id = rs.getInt(PHOTO_ID);
				// String name = rs.getString(PHOTO_NAME);
				// String uri = rs.getString(PHOTO_URI);
				// String date = rs.getString(PHOTO_CREATEDATE);
				// String profile = rs.getString(PHOTO_PROFILE);
				// double size = rs.getDouble(PHOTO_SIZE);
				//
				// photo.setMd5(md5);
				// photo.setId(id);
				// photo.setName(name);
				// photo.setUri(uri);
				// photo.setCreateDate(date);
				// photo.setProfile(profile);
				// photo.setSize(size);
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
				+ "albumName name text not null," + "name text not null," + "uri text,createDate text,profile text,"
				+ "size real,primary key(md5,id))";
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
				photo.setAlbumName(rs.getString(ALBUMNAME));
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

	public static ObservableList<Photo> queryPhoto(String username) {
		String sql_select = "select * from photos_" + username;
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
				photo.setAlbumName(rs.getString(ALBUMNAME));
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

	public static boolean queryAlbum(String username, String albumName) {
		getConnection();
		String sql_query = "select name from albums_" + username + " where name =? ";
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, albumName);
			ResultSet rs = statement.executeQuery();
			rs.next();
			if (rs.getRow() > 0) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			releaseConnection(getCurrentMethod());
		}

	}

	public static void deleteAlbum(String name) {
		String sql_delete = "delete from albums_" + name;
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
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

	public static void deletePhoto(String username) {
		String sql_delete = "delete from photos_" + username;
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
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

	public static void updateUserInfo(String username, String password, String nickname) {
		getConnection();
		String sql_update = "update users set password = ? and nickname = ? where name = ?";
		try {
			statement = connection.prepareStatement(sql_update);
			statement.setString(1, password);
			statement.setString(2, nickname);
			statement.setString(3, username);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static boolean queryUser(String username) {
		getConnection();
		String sql_query = "select username from users where username = ?";
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			rs.next();
			if (rs.getRow() == 0) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				+ "name text unique not null,password text not null,nickname text not null,albumNumber integer,photoNumber integer,albumNames text)";
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

	public static void addUser(User user) {
		String sql_insert = "insert into users(name,password,nickname,albumNumber,photoNumber,albumNames) values(?,?,?,?,?,?)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_insert);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getNickname());
			statement.setInt(4, user.getAlbumNumber());
			statement.setInt(5, user.getPhotoNumber());
			statement.setString(6, ParseUtil.parseListToString(user.getAlbumNames()));
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

			if (statement != null && !statement.isClosed())
				statement.close();

			if (connection != null && !connection.isClosed())
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
	 * 
	 * @param name
	 *            当前用户姓名
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

	public static void deleteExpressions(String username) {
		String sql_delete = "delete from expressions_" + username;
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);

			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void deleteExpressionsOfAlbum(String name, int id) {
		String sql_delete = "delete from expressions_" + name + " where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}

	}

	public static void deleteExpressionOfPhoto(String name, int id, String md5) {
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
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	/**
	 * 查询当前用户数据库中的所有直方图表示的集合并返回
	 * 
	 * @param name
	 *            当前用户姓名
	 * @param dimension
	 *            直方图表示的维度
	 * @return
	 */
	public static List<Point> getExpressions(String name, int dimension) {
		String sql_query = "select id,uri,expression from expressions_" + name;
		getConnection();
		List<Point> points = new ArrayList<>();
		try {
			statement = connection.prepareStatement(sql_query);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Point point = new Point(ParseUtil.expressionTodoubleArray(rs.getString(3), dimension));
				point.setId(rs.getInt(1));
				point.setUrl(rs.getString(2));
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
	 * 
	 * @param name
	 *            当前用户姓名
	 * @param dimension
	 *            直方图表示的维度
	 * @param id
	 *            相片所属的相册 id
	 * @param md5
	 *            相片的 md5
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

			if (rs.getRow() != 0) {
				String string = rs.getString(1);

				return ParseUtil.expressionTodoubleArray(string, dimension);
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
	 * 
	 * @param name
	 *            当前用户姓名
	 * @param photo
	 *            存储的图片对象
	 * @param expression
	 *            需要存储的直方图表示
	 */
	public static void addExpression(String name, int id, String md5, String uri, String expression) {
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

	public static double[] getExpression(String username, String md5, int id) {
		String sql_query = "select expression from expressions_" + username + " where id = ? and md5 = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setInt(1, id);
			statement.setString(2, md5);
			ResultSet rs = statement.executeQuery();
			rs.next();
			if (rs.getRow() != 0) {
				String expression = rs.getString(1);
				return ParseUtil.expressionTodoubleArray(expression, KDSearchUtil.DIMENSIONS_OF_KDTREE);
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
		return null;
	}

	public static void addExpressionBatch(String name, int id, List<Photo> photos, List<String> expressionToAdd) {
		// TODO Auto-generated method stub
		String sql_insert = "insert into expressions_" + name + " (id,md5,uri,expression) values(?,?,?,?)";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_insert);
			connection.setAutoCommit(false);
			for (int i = 0; i < photos.size(); i++) {
				Photo photo = photos.get(i);
				statement.setInt(1, id);
				statement.setString(2, photo.getMd5());
				statement.setString(3, photo.getUri());
				statement.setString(4, expressionToAdd.get(i));
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				LogUtil.e(getCurrentMethod(), "批量插入直方图表示时出现问题,回滚失败");
			}

		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void saveAlbumInfo(Album album, String username) {
		getConnection();
		String sql_insert = "insert into albums_" + username
				+ "(id,name,profile,coverUri,createDate,photoNumber,size,photosUri) values(?,?,?,?,?,?,?,?)";
		try {
			statement = connection.prepareStatement(sql_insert);
			statement.setInt(1, album.getId());
			statement.setString(2, album.getAlbumName());
			statement.setString(3, album.getAlbumProfile());
			statement.setString(4, album.getCoverUri());
			statement.setString(5, album.getCreateDate());
			statement.setInt(6, album.getPhotosNumber());
			statement.setDouble(7, album.getSize());
			statement.setString(8, ParseUtil.parseListToString(album.getPhotosUri()));
			statement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "保存相册信息出错");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static void updateAlbumInfo(String username, Album album) {
		// TODO Auto-generated method stub
		getConnection();
		String sql_update = "update albums_" + username
				+ " set name=?,profile=?,coverUri=?,createDate=?,photoNumber=?,size=?,photosUri=? where id=?";
		try {
			statement = connection.prepareStatement(sql_update);
			statement.setString(1, album.getAlbumName());
			statement.setString(2, album.getAlbumProfile());
			statement.setString(3, album.getCoverUri());
			statement.setString(4, album.getCreateDate());
			statement.setInt(5, album.getPhotosNumber());
			statement.setDouble(6, album.getSize());
			statement.setString(7, ParseUtil.parseListToString(album.getPhotosUri()));
			statement.setInt(8, album.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String methodName = getCurrentMethod();
			LogUtil.e(methodName, "更新相册信息出错");
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
	}

	public static User getUser(String username) {
		// TODO Auto-generated method stub
		String sql_query = "select * from users where name = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			rs.next();
			return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6),
					ParseUtil.parseUrlToList(rs.getString(7)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}
		return null;
	}

	public static void updateUserInfo(User user) {
		// TODO Auto-generated method stub
		String sql_update = "update users set nickname = ?,albumNumber = ?, photoNumber = ?, albumNames = ? where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_update);
			statement.setString(1, user.getNickname());
			statement.setInt(2, user.getAlbumNumber());
			statement.setInt(3, user.getPhotoNumber());
			statement.setString(4, ParseUtil.parseListToString(user.getAlbumNames()));
			statement.setInt(5, user.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseConnection(getCurrentMethod());
		}

	}
}
