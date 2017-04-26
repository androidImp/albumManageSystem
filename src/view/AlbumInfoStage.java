package view;

import java.io.IOException;

import controller.ShowAlbumInfoController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Album;
import util.ParseUtil;

public class AlbumInfoStage extends BaseStage {

	private Album album;

	Parent root;
	FXMLLoader loader;

	public AlbumInfoStage(Album album) {
		// TODO Auto-generated constructor stub
		setAlbum(album);
		loadStageFromFXML();
		configureController();

	}

	@Override
	protected void configureController() {
		ShowAlbumInfoController controller = loader.getController();
		controller.configureStage(this);
	}

	@Override
	protected void loadStageFromFXML() {
		loader = new FXMLLoader();
		try {
			loader.setLocation(getClass().getResource("showAlbumInfo.fxml"));
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(root, 300, 300);
		setScene(scene);
		show();
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
}
