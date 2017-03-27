package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Photo {

	/**
	 * 相册地址
	 */
	private SimpleStringProperty uri = new SimpleStringProperty(this, "uri");
	/**
	 * 创建时间
	 */
	private SimpleStringProperty createDate = new SimpleStringProperty(this, "createDate");
	/**
	 * 相片名
	 */
	private SimpleStringProperty name = new SimpleStringProperty(this, "name");
	/**
	 * 相片名
	 */
	private SimpleStringProperty md5 = new SimpleStringProperty(this, "md5");
	/**
	 * 相片大小
	 */
	private SimpleDoubleProperty size = new SimpleDoubleProperty(this, "size");
	/**
	 * 相片描述
	 */
	private SimpleStringProperty profile = new SimpleStringProperty(this, "profile");

	/**
	 * SIFT 特征值;
	 */
	public String getUri() {
		return uri.get();
	}

	public void setUri(String uri) {
		this.uri.set(uri);
	}

	public String getCreateDate() {
		return createDate.get();
	}

	public void setCreateDate(String createDate) {
		this.createDate.set(createDate);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getMd5() {
		return md5.get();
	}

	public void setMd5(String md5) {
		this.md5.set(md5);
	}

	public double getSize() {
		return size.get();
	}

	public void setSize(double size) {
		this.size.set(size);
	}

	public String getProfile() {
		return profile.get();
	}

	public void setProfile(String profile) {
		this.profile.set(profile);
	}
}
