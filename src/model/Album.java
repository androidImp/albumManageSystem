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

	public Integer getId() {
		return id.get();
	}

	public void setId(Integer id) {
		this.id.set(id);
	}

	public ObservableList<String> getPhotosUri() {
		return photosUri.get();
	}

	public void setPhotosUri(ObservableList<String> photosUri) {
		this.photosUri.set(photosUri);
	}

	public String getCoverUri() {
		return coverUri.get();
	}

	public void setCoverUri(String coverUri) {
		this.coverUri.set(coverUri);
	}

	public String getCreateDate() {
		return createDate.get();
	}

	public void setCreateDate(String createDate) {
		this.createDate.set(createDate);

	}

	public Integer getPhotosNumber() {
		return photosNumber.get();
	}

	public void setPhotosNumber(Integer photosNumber) {
		this.photosNumber.set(photosNumber);
	}

	public String getAlbumName() {
		return albumName.get();
	}

	public void setAlbumName(String albumName) {
		this.albumName.set(albumName);
	}

	public String getAlbumProfile() {
		return albumProfile.get();
	}

	public void setAlbumProfile(String albumProfile) {
		this.albumProfile.set(albumProfile);
	}

	public Double getSize() {
		return size.get();
	}

	public void setSize(Double size) {
		this.size.set(size);
	}
	public StringProperty coverUriProperty(){
		return coverUri;
	}
	public StringProperty nameProperty(){
		return albumName;
	}
	public DoubleProperty sizeProperty(){
		return size;
	}
}