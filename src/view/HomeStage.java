package view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Album;
import model.AlbumCell;
import util.DBUtil;
import util.DateUtil;

public class HomeStage extends Application {
	ListView<Album> ls_album;
	ObservableList<Album> albumsList;
	List<Album> albums;
	Parent root = null;

	@Override
	public void start(Stage primaryStage) {
		
		try {
			root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		initView(primaryStage);
		initAlbumList();
		DBUtil.getConnection();
		DBUtil.createTable();
	}

	@SuppressWarnings("unchecked")
	private void initView(Stage primaryStage) {
		ls_album = (ListView<Album>) root.lookup("#ls_album");
		albums = new ArrayList<>();
		primaryStage.show();
	}

	public void initAlbumList() {
		for (int i = 0; i < 10; i++) {
			Album album = new Album();
			album.setCoverUri("Pic/emptyCover.png");
			album.setAlbumName("album " + i);
			album.setPhotosUri(FXCollections.observableArrayList());
			// album.setPhotosUri(FXCollections.observableArrayList(album.getCoverUri()));
			album.setCreateDate(DateUtil.getFormatDate());
			albums.add(album);
		}
		albumsList = FXCollections.observableArrayList(albums);
		ls_album.setItems(albumsList);
		// java 8 lambda
		ls_album.setCellFactory((ListView<Album> l) -> new AlbumCell());
		ls_album.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if (((MouseEvent) event).getClickCount() >= 2) {
					Album album = ls_album.getSelectionModel().getSelectedItem();
					new PhotoStage(album).show();

				}
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
