package view;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import org.apache.commons.math3.ml.clustering.CentroidCluster;

import cluster.ClusterUtils;
import cluster.Point;
import controller.HomePageController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import model.Album;
import model.Photo;
import model.SearchItemCell;
import model.User;
import search.KDSearchUtil;
import util.DBUtil;

public class HomeStage extends BaseStage {

	public static int index;
	TableView<Album> tv_album;
	Label ll_name;
	TextField tf_search;
	TreeView<String> tv_menu;
	String username;
	FXMLLoader loader;
	Parent root;
	List<CentroidCluster<Point>> centers = new ArrayList<>();

	public HomeStage(String name) {
		// TODO Auto-generated constructor stub
		setUsername(name);
		initParameters();
		// initAlbumList();
		loadStageFromFXML();
		configureController();
		initView();
		configureData();
		configureSearchView();
		addContextMenuToAlbums();
		configureCloseProperty();
		configureSearchView();
		configureUserMenu();
		addContextMenuToAlbums();
		configureCluster();
		configureKDTree();
	}

	private void configureKDTree() {
		// TODO Auto-generated method stub
		KDSearchUtil.configureKDTree(getUsername());

	}

	private void configureCluster() {
		// TODO Auto-generated method stub
		ClusterUtils.readClusterPoints();
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
				// DBUtil.saveAlbums(tv_album.getItems(), getUserName());
				Platform.exit();
			}
		});
	}

	/**
	 * 配置图片查询的控件
	 */
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

	// todo 处理单击 item 之后的跳转
	/**
	 * 查询图片之后对图片的点击事件的处理
	 * 
	 * @param lv
	 */
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

	/**
	 * 初始化用户数据
	 */
	private void configureData() {
		DBUtil.createPhotosTable(getUsername());
		tv_album.setItems(DBUtil.getAlbums(getUsername()));
		DBUtil.createExpressionsTable(getUsername());
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		tv_album = (TableView<Album>) root.lookup("#tv_album");
		tf_search = (TextField) root.lookup("#tf_search");
		ll_name = (Label) root.lookup("#ll_name");
		ll_name.setText(getUsername());
		tv_menu = (TreeView<String>) root.lookup("#tv_menu");
	}

	/**
	 * 初始化用户菜单
	 */
	private void configureUserMenu() {
		// 根节点
		TreeItem<String> rootItem = new TreeItem<>("用户菜单");
		rootItem.setExpanded(true);
		//
		// TreeItem<String> item_scan = new TreeItem<String>("");
		// Hyperlink link_scan = new Hyperlink("查看用户信息");
		// item_scan.setGraphic(link_scan);
		// 2
		TreeItem<String> item_clear = new TreeItem<String>("");
		Hyperlink link_clear = new Hyperlink("清除信息");
		configureLinkClear(link_clear);
		item_clear.setGraphic(link_clear);
		// 3
		TreeItem<String> item_setting = new TreeItem<String>("");
		Hyperlink link_setting = new Hyperlink("系统设置");
		configureLinkSetting(link_setting);
		item_setting.setGraphic(link_setting);
		// 4
		TreeItem<String> item_modify = new TreeItem<String>("");
		Hyperlink link_modify = new Hyperlink("修改密码");
		configureLinkChangePwd(link_modify);
		item_modify.setGraphic(link_modify);
		// 5
		TreeItem<String> item_info = new TreeItem<String>("");
		Hyperlink link_info = new Hyperlink("关于");
		configureLinkInfo(link_info);
		item_info.setGraphic(link_info);
		// 6
		TreeItem<String> item_out = new TreeItem<String>("");
		Hyperlink link_out = new Hyperlink("退出登录");
		configureLinkOut(link_out);
		item_out.setGraphic(link_out);
		rootItem.getChildren().addAll(item_clear, item_setting, item_modify, item_info, item_out);
		tv_menu.setRoot(rootItem);
	}

	public void initAlbumList() {

		tv_album.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if (((MouseEvent) event).getClickCount() >= 2) {
					Album album = tv_album.getSelectionModel().getSelectedItem();
					if (album != null) {
						new PhotoBrowserStage(album, new User());

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
				int index = tv_album.getSelectionModel().getSelectedIndex();
				if (index != -1) {
					int id = tv_album.getSelectionModel().getSelectedItem().getId();
					tv_album.getItems().remove(index);
					DBUtil.deleteAlbum(id, getUsername());
					DBUtil.deletePhotoByAlbum(id, getUsername());
					DBUtil.deleteExpressionsOfAlbum(getUsername(), id);
				}
			}
		});
		scan_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new AlbumInfoStage(tv_album.getSelectionModel().getSelectedItem());
			}
		});
		open_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new PhotoStage(tv_album.getSelectionModel().getSelectedItem(), getUsername()).show();

			}
		});
		menu.getItems().addAll(open_item, scan_item, delete_item);
		tv_album.setContextMenu(menu);
	}

	private void configureLinkClear(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("删除相册");
				alert.setContentText("确定要清除当前存储的内容吗?");
				Optional<ButtonType> resultType = alert.showAndWait();
				if (resultType.isPresent()) {
					tv_album.getItems().clear();
					DBUtil.deleteAlbum(getUsername());
					DBUtil.deletePhoto(getUsername());
					DBUtil.deleteExpressions(getUsername());
					KDSearchUtil.clearNode();
				}

			}
		});

	}

	private void configureLinkSetting(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new ChangePwdStage(getUsername());
			}
		});
	}

	private void configureLinkChangePwd(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new ChangePwdStage(getUsername());
			}
		});

	}

	private void configureLinkInfo(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new DeclerationStage();
			}
		});

	}

	private void configureLinkOut(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Preferences preferences = Preferences.userNodeForPackage(LoginStage.class);
				preferences.put("origin", "HomeStage");
				hide();
				new LoginStage();
			}
		});
	}

	public ObservableList<Album> getAlbumData() {
		// TODO Auto-generated method stub
		return DBUtil.getAlbums(getUsername());
	}

	@Override
	protected void loadStageFromFXML() {
		// TODO Auto-generated method stub
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("mainStage.fxml"));
		try {
			root = loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		show();
	}

	@Override
	protected void configureController() {
		// TODO Auto-generated method stub
		HomePageController controller = loader.getController();
		controller.configureStage(this);

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
