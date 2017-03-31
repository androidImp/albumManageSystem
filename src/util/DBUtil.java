package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	public static void saveAlbums(List<Album> albums) {
		getConnection();
		String sql_select = "select id from albums where id=?";
		String sql_update = "update albums set name=?,profile=?,coverUri=?,createDate=?,photoNumber=?,size=?,photosUri=? where id=?";
		String sql_insert = "insert into albums(id,name,profile,coverUri,createDate,photoNumber,size,photosUri) values(?,?,?,?,?,?,?,?)";
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
			e.printStackTrace();
		}
	}

	public static void savePhotos(List<Photo> photos) {
		getConnection();
		String sql_select = "select md5,id from photos where md5 = ? and id = ?";
		String sql_insert = "insert into photos(md5,id,name,uri,createDate,profile,size) values(?,?,?,?,?,?,?)";
		String sql_update = "update photos set name=?,uri=?,createDate=?,profile=?,size=? where md5=? and id = ?";
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
		}
	}

	public static ObservableList<Album> getAlbums() {
		getConnection();
		List<Album> albums = new ArrayList<>();
		String sql_select = "select * from albums";
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

			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FXCollections.observableArrayList(albums);
	}

	public static ObservableList<Photo> queryPhotoByName(String photoName) {
		getConnection();
		String sql_select = "select * from photos where name like '%" + photoName + "%'";
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

			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FXCollections.observableArrayList(photos);
	}

	public static void createPhotosTable() {
		String sql = "create table if not exists photos " + "(md5 text," + "id integer not null,"
				+ "name text not null," + "uri text,createDate text,profile text," + "size real,primary key(md5,id))";
		getConnection();
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

	public static ObservableList<Photo> getPhotosByAlbum(int index) {
		String sql_select = "select * from photos where id = " + index;
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
			e.printStackTrace();
		}
		return FXCollections.observableArrayList(photos);
	}

	public static void deleteAlbum(int id) {
		String sql_delete = "delete from albums where id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deletePhoto(String md5, int id) {
		String sql_delete = "delete from photos where md5 = ? and id = ?";
		getConnection();
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setString(1, md5);
			statement.setInt(2, id);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deletePhotoByAlbum(int id) {
		getConnection();
		String sql_delete = "delete from photos where id = ?";
		try {
			statement = connection.prepareStatement(sql_delete);
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void commit(){
		
	}
}
