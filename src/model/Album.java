package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Album {
	/**
	 * 相册索引号
	 */
	private SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id");
	/**
	 * 记录相册名
	 */
	private SimpleStringProperty albumName = new SimpleStringProperty(this, "albumName");
	/**
	 * 记录相册简介
	 */
	private SimpleStringProperty albumProfile = new SimpleStringProperty(this, "albumProfile");
	/**
	 * 记录相片的外存地址
	 */
	private ListProperty<String> photosUri = new SimpleListProperty<>(this, "photosUri");
	/**
	 * 记录相册封面相册的地址
	 */
	private SimpleStringProperty coverUri = new SimpleStringProperty(this, "coverUri");
	/**
	 * 记录相册的创建时间
	 */
	private SimpleStringProperty createDate = new SimpleStringProperty(this, "createDate");
	/**
	 * 记录相册的相片总数
	 */
	private SimpleIntegerProperty photosNumber = new SimpleIntegerProperty(this, "photosNumber");
	/**
	 * 记录相册所占用的空间大小
	 */
	private SimpleDoubleProperty size = new SimpleDoubleProperty(this, "size");

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null) {
			return false;
		}
		Album album = (Album) obj;
		return getId() == album.getId();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public SimpleIntegerProperty idProperty() {
		return this.id;
	}

	public int getId() {
		return this.idProperty().get();
	}

	public void setId(int id) {
		this.idProperty().set(id);
	}

	public SimpleStringProperty albumNameProperty() {
		return this.albumName;
	}

	public java.lang.String getAlbumName() {
		return this.albumNameProperty().get();
	}

	public void setAlbumName(java.lang.String albumName) {
		this.albumNameProperty().set(albumName);
	}

	public SimpleStringProperty albumProfileProperty() {
		return this.albumProfile;
	}

	public java.lang.String getAlbumProfile() {
		return this.albumProfileProperty().get();
	}

	public void setAlbumProfile(java.lang.String albumProfile) {
		this.albumProfileProperty().set(albumProfile);
	}

	public ListProperty<String> photosUriProperty() {
		return this.photosUri;
	}

	public javafx.collections.ObservableList<java.lang.String> getPhotosUri() {
		return this.photosUriProperty().get();
	}

	public void setPhotosUri(javafx.collections.ObservableList<java.lang.String> photosUri) {
		this.photosUriProperty().set(photosUri);
	}

	public SimpleStringProperty coverUriProperty() {
		return this.coverUri;
	}

	public java.lang.String getCoverUri() {
		return this.coverUriProperty().get();
	}

	public void setCoverUri(java.lang.String coverUri) {
		this.coverUriProperty().set(coverUri);
	}

	public SimpleStringProperty createDateProperty() {
		return this.createDate;
	}

	public java.lang.String getCreateDate() {
		return this.createDateProperty().get();
	}

	public void setCreateDate(java.lang.String createDate) {
		this.createDateProperty().set(createDate);
	}

	public SimpleIntegerProperty photosNumberProperty() {
		return this.photosNumber;
	}

	public int getPhotosNumber() {
		return this.photosNumberProperty().get();
	}

	public void setPhotosNumber(int photosNumber) {
		this.photosNumberProperty().set(photosNumber);
	}

	public SimpleDoubleProperty sizeProperty() {
		return this.size;
	}

	public double getSize() {
		return this.sizeProperty().get();
	}

	public void setSize(double size) {
		this.sizeProperty().set(size);
	}

}