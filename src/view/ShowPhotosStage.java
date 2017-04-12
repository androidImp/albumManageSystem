package view;

import java.io.IOException;

import org.controlsfx.control.GridView;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Album;
import model.Photo;
import util.DBUtil;

public class ShowPhotosStage extends BaseStage {
	SimpleStringProperty username = new SimpleStringProperty("name");
	private Album album;
	GridView<Photo> gv_photo;
	Parent root = null;
	

	public ShowPhotosStage(Album album, String name) {
		// TODO Auto-generated constructor stub
		setUsername(name);
		this.album = album;

		try {
			root = FXMLLoader.load(getClass().getResource("photosList.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (root != null) {
			Scene scene = new Scene(root);
			setScene(scene);
			initViewsId();
			configurePhotoList();
			show();
		}

	}

	// To do background;
	@SuppressWarnings("unchecked")
	public void initViewsId() {
		gv_photo = (GridView<Photo>) root.lookup("#gv_photo");
		
	}

	public void configurePhotoList() {
		gv_photo.setPadding(new Insets(10));
		gv_photo.setCellFactory(param -> new ImageCell());
		new Thread(new RenderImageTask()).start();
	}

	public final SimpleStringProperty usernameProperty() {
		return this.username;
	}

	public final String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
	}

	

	class RenderImageTask extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			// TODO Auto-generated method stub
			ObservableList<Photo> photos = DBUtil.getPhotosByAlbum(album.getId(), username.get());
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					gv_photo.setItems(photos);
				}
			});
			return null;
		}
	}

}
