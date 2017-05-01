package model;

import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7351729135012380019L;
	private SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id");
	private SimpleStringProperty username = new SimpleStringProperty(this, "username");
	private SimpleStringProperty password = new SimpleStringProperty(this, "password");
	private SimpleStringProperty nickname = new SimpleStringProperty(this, "nickname");
	private SimpleIntegerProperty photoNumber = new SimpleIntegerProperty(this, "photoNumber");
	private SimpleIntegerProperty albumNumber = new SimpleIntegerProperty(this, "albumNumber");

	public final SimpleIntegerProperty idProperty() {
		return this.id;
	}

	public final int getId() {
		return this.idProperty().get();
	}

	public final void setId(final int id) {
		this.idProperty().set(id);
	}

	public final SimpleStringProperty usernameProperty() {
		return this.username;
	}

	public final java.lang.String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final java.lang.String username) {
		this.usernameProperty().set(username);
	}

	public final SimpleStringProperty passwordProperty() {
		return this.password;
	}

	public final java.lang.String getPassword() {
		return this.passwordProperty().get();
	}

	public final void setPassword(final java.lang.String password) {
		this.passwordProperty().set(password);
	}

	public final SimpleStringProperty nicknameProperty() {
		return this.nickname;
	}

	public final java.lang.String getNickname() {
		return this.nicknameProperty().get();
	}

	public final void setNickname(final java.lang.String nickname) {
		this.nicknameProperty().set(nickname);
	}

	public final SimpleIntegerProperty photoNumberProperty() {
		return this.photoNumber;
	}

	public final int getPhotoNumber() {
		return this.photoNumberProperty().get();
	}

	public final void setPhotoNumber(final int photoNumber) {
		this.photoNumberProperty().set(photoNumber);
	}

	public final SimpleIntegerProperty albumNumberProperty() {
		return this.albumNumber;
	}

	public final int getAlbumNumber() {
		return this.albumNumberProperty().get();
	}

	public final void setAlbumNumber(final int albumNumber) {
		this.albumNumberProperty().set(albumNumber);
	}

}
