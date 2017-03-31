package view;

import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.AlbumCell;
import model.Photo;
import model.SearchItemCell;
import util.DBUtil;

public class HomeStage extends Stage {
	ListView<Album> ls_album;
	Parent root = null;
	public static int index;
	TextField tf_search;

	public HomeStage() {
		// TODO Auto-generated constructor stub
		try {
			root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		initParameters();
		initView();
		initAlbumList();
		configureData();
		configureSearchView();
		configureCloseProperty();
		addContextMenuToAlbums();
	}

	private void initParameters() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		index = preferences.getInt("index", 0);
	}

	private void configureCloseProperty() {
		setOnCloseRequest(new EventHandler<WindowEvent>() {

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
	}

	private void configureSearchView() {
		ContextMenu menu = new ContextMenu();
		MenuItem suggestion = new MenuItem();
		menu.getItems().addAll(suggestion);
		ListView<Photo> lv = new ListView<Photo>();
		configureSearchItem(lv);
		configureSearchAction(menu, suggestion, lv);
	}

	private void configureSearchAction(ContextMenu menu, MenuItem suggestion, ListView<Photo> lv) {
		tf_search.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						// TODO Auto-generated method stub
						ObservableList<Photo> photos = DBUtil.queryPhotoByName(tf_search.getText());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								lv.setItems(photos);
								suggestion.setGraphic(lv);
								menu.show(tf_search, Side.BOTTOM, 0, 0);
							}
						});
						return null;
					}
				};
				new Thread(task).start();
			}
		});
	}

	private void configureSearchItem(ListView<Photo> lv) {
		lv.setCellFactory(param -> new SearchItemCell());
		lv.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				Photo photo = lv.getSelectionModel().getSelectedItem();

			}
		});
	}

	private void configureData() {
		DBUtil.getConnection();
		DBUtil.createTable();
		DBUtil.createPhotosTable();
		ls_album.setItems(DBUtil.getAlbums());
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		ls_album = (ListView<Album>) root.lookup("#ls_album");
		tf_search = (TextField) root.lookup("#tf_search");
		show();
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

	public void addContextMenuToAlbums() {
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

}
