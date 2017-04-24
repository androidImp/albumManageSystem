package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Photo {
	/**
	 * 所属相册的索引号
	 */
	private SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id");
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
	private SimpleBooleanProperty modified = new SimpleBooleanProperty(this, "modified");

	// /**
	// * SIFT 特征值;
	// */
	public Integer getId() {
		return id.get();
	}

	public void setId(Integer id) {
		this.id.set(id);
	}

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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("MD5: ");
		builder.append(md5.get());
		builder.append(" ID: ");
		builder.append(id.get());
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Album)) {
			return false;
		}
		Photo photo = (Photo) obj;
		return photo.getId() == getId() && photo.getName().equals(getName()) && photo.getProfile().equals(getProfile())
				&& photo.getUri().equals(getUri()) && photo.getCreateDate().equals(getCreateDate())
				&& photo.getMd5() == photo.getMd5()
				&& Double.doubleToLongBits(photo.getSize()) == Double.doubleToLongBits(getSize());

	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = 17;
		result = result * 37 + getId();
		result = result * 37 + getName().hashCode();
		result = result * 37 + getProfile().hashCode();
		result = result * 37 + getUri().hashCode();
		result = result * 37 + getCreateDate().hashCode();
		long tmp = Double.doubleToLongBits(getSize());
		result = result * 37 + (int) (tmp ^ (tmp >>> 32));
		return result;
	}

	public Photo() {
		super();
		modified.set(false);
	}

	public Photo(int id, String uri, String createDate, String name, String md5, double size, String profile) {
		super();
		setId(id);
		setUri(uri);
		setCreateDate(createDate);
		setName(name);
		setMd5(md5);
		setSize(size);
		setProfile(profile);
		modified.set(false);
	}

	public final SimpleBooleanProperty modifiedProperty() {
		return this.modified;
	}

	public final boolean isModified() {
		return this.modifiedProperty().get();
	}

	public final void setModified(final boolean modified) {
		this.modifiedProperty().set(modified);
	}

}
