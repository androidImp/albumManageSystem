package view;

import java.io.IOException;
import java.util.prefs.Preferences;

import controller.AlbumBrowserController;
import controller.ShowAlbumInfoController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;

public class ShowAlbumStage extends BaseStage {
	public static int index;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private FXMLLoader loader;
	private Parent root;
	private String username;

	public ShowAlbumStage(String name) {
		// TODO Auto-generated constructor stub
		setUsername(name);
		initParameters();
		loadStageFromFXML();
		configureController();
		renderImage();
		configureCloseProperty();
	}

	private void renderImage() {
		// TODO Auto-generated method stub
		AlbumBrowserController controller = loader.getController();
		controller.configure();
	}

	@Override
	protected void loadStageFromFXML() {
		// TODO Auto-generated method stub
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("albumBrowser.fxml"));
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setScene();
	}

	private void setScene() {
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("album.css").toExternalForm());
		setScene(scene);
		show();
	}

	private void configureCloseProperty() {
		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				// 可以使用 JAXB
				Preferences preferences = Preferences.userNodeForPackage(getClass());
				preferences.putInt("index", index);
				// DBUtil.saveAlbums(tv_album.getItems(), getUserName());
				Platform.exit();
			}
		});
	}

	@Override
	protected void configureController() {
		// TODO Auto-generated method stub
		AlbumBrowserController controller = loader.getController();
		controller.configureStage(ShowAlbumStage.this);
	}

	private void initParameters() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		index = preferences.getInt("index", 0);
	}

}
