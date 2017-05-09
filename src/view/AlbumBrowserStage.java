package view;

import java.io.IOException;
import java.util.prefs.Preferences;

import cluster.ClusterUtils;
import controller.AlbumBrowserController;
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
import javafx.stage.WindowEvent;
import model.Photo;
import model.SearchItemCell;
import model.User;
import search.KDSearchUtil;
import util.DBUtil;

public class AlbumBrowserStage extends BaseStage {
	public static int index;
	private TextField tf_search;

	public String getUsername() {
		return user.getUsername();
	}

	private FXMLLoader loader;
	private Parent root;
	private User user;

	public AlbumBrowserStage(User user) {
		// TODO Auto-generated constructor stub
		setUser(user);
		initParameters();
		loadStageFromFXML();
		configureController();
		renderImage();
		configureCloseProperty();
		configureSearchView();
		configureCluster();
		configureKDTree();

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
		controller.configureStage(AlbumBrowserStage.this);
	}

	private void initParameters() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		index = preferences.getInt("index", 0);
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
		tf_search = (TextField) root.lookup("#tf_search");
		tf_search.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						// TODO Auto-generated method stub
						ObservableList<Photo> photos = DBUtil.queryPhotoByName(tf_search.getText(), getUsername());
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
				KDSearchUtil.queryNearestPic(getUsername(), photo);

			}
		});
	}

	private void configureKDTree() {
		// TODO Auto-generated method stub
		KDSearchUtil.configureKDTree(getUsername());
	}

	private void configureCluster() {
		// TODO Auto-generated method stub
		ClusterUtils.readClusterPoints();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
