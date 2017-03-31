package view;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.Album;
import model.AlbumCell;
import util.DBUtil;

public class HomeStage extends Application {
	ListView<Album> ls_album;
	Parent root = null;
	public static int index;
	TextField tf_search;

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
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		index = preferences.getInt("index", 0);
		initView(primaryStage);
		initAlbumList();
		configureData();
		tf_search = (TextField) root.lookup("#tf_search");
		TextFields.bindAutoCompletion(tf_search,
				new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {

					@Override
					public Collection<String> call(ISuggestionRequest param) {
						// TODO Auto-generated method stub
						param.getUserText();
						return null;
					}
				});
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				// 可以使用 JAXB
				Preferences preferences = Preferences.userNodeForPackage(getClass());
				preferences.putInt("index", index);
				DBUtil.saveAlbums(ls_album.getItems());
				Platform.exit();
			}
		});
		addContextMenu();
	}

	private void configureData() {
		DBUtil.getConnection();
		DBUtil.createTable();
		DBUtil.createPhotosTable();
		ls_album.setItems(DBUtil.getAlbums());
	}

	@SuppressWarnings("unchecked")
	private void initView(Stage primaryStage) {
		ls_album = (ListView<Album>) root.lookup("#ls_album");
		primaryStage.show();
	}

	public void initAlbumList() {
		ls_album.setCellFactory((ListView<Album> l) -> new AlbumCell());
		ls_album.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if (((MouseEvent) event).getClickCount() >= 2) {
					Album album = ls_album.getSelectionModel().getSelectedItem();
					if (album != null) {
						new PhotoStage(album).show();
					}

				}
			}
		});

	}

	public void addContextMenu() {
		ContextMenu menu = new ContextMenu();
		MenuItem delete_item = new MenuItem("删除");
		MenuItem scan_item = new MenuItem("浏览信息");
		MenuItem open_item = new MenuItem("打开");
		delete_item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				int index = ls_album.getSelectionModel().getSelectedIndex();
				if (index != -1) {
					int id = ls_album.getSelectionModel().getSelectedItem().getId();
					ls_album.getItems().remove(index);
					DBUtil.deleteAlbum(id);
					DBUtil.deletePhotoByAlbum(id);
				}
			}
		});
		scan_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new PhotoInfoStage(ls_album.getSelectionModel().getSelectedItem());
			}
		});
		open_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new PhotoStage(ls_album.getSelectionModel().getSelectedItem()).show();

			}
		});
		menu.getItems().addAll(open_item, scan_item, delete_item);
		ls_album.setContextMenu(menu);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
