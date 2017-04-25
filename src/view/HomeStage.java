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
import cluster.KDSearchUtil;
import cluster.Point;
import controller.homePageController;
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
import util.DBUtil;

public class HomeStage extends BaseStage {
	public static int index;


	TableView<Album> tv_album;
	Parent root = null;
	Label ll_name;
	TextField tf_search;
	TreeView<String> tv_menu;
	SimpleStringProperty username = new SimpleStringProperty("name");

	List<CentroidCluster<Point>> centers = new ArrayList<>();

	public HomeStage(String name) {
		// TODO Auto-generated constructor stub
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(getClass().getResource("mainStage.fxml"));
			// root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
			root = loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setName(name);

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		initParameters();
		initView();
		initAlbumList();
		configureData();
		configureSearchView();
		configureUserMenu();
		configureCloseProperty();
		addContextMenuToAlbums();
		homePageController controller = loader.getController();
		controller.setMainApp(this);
		DBUtil.createExpressionsTable(username.get());
		configureCluster();
		configureKDTree();
	}

	private void configureKDTree() {
		// TODO Auto-generated method stub
		KDSearchUtil.configureKDTree(username.get());

	}

	private void configureCluster() {
		// TODO Auto-generated method stub
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream("/Users/niuniumei/Documents/Java/MyAlbum/album.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (objectInputStream != null) {

			try {
				Object object = null;
				while ((object = objectInputStream.readObject()) != null) {
					Point point = (Point) object;
					CentroidCluster<Point> centroidCluster = new CentroidCluster<>(point);
					centers.add(centroidCluster);
				}

			} catch (EOFException e) {
				// TODO: handle exception
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			objectInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClusterUtils.setWords(centers);
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
				// DBUtil.saveAlbums(tv_album.getItems(), username.get());
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
						ObservableList<Photo> photos = DBUtil.queryPhotoByName(tf_search.getText(), username.get());
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
				KDSearchUtil.queryNearestPic(username.get(), photo);

			}
		});
	}

	/**
	 * 初始化用户数据
	 */
	private void configureData() {
		DBUtil.createPhotosTable(username.get());

	}

	@SuppressWarnings("unchecked")
	private void initView() {
		tv_album = (TableView<Album>) root.lookup("#tv_album");
		tf_search = (TextField) root.lookup("#tf_search");
		ll_name = (Label) root.lookup("#ll_name");
		ll_name.setText(username.get());
		tv_menu = (TreeView<String>) root.lookup("#tv_menu");
		show();
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
						// new PhotoStage(album, getName()).show();
						new ShowPhotosStage(album, getName());
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
					DBUtil.deleteAlbum(id, username.get());
					DBUtil.deletePhotoByAlbum(id, username.get());
					DBUtil.deleteExpressionsOfAlbum(username.get(), id);
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
				new PhotoStage(tv_album.getSelectionModel().getSelectedItem(), getName()).show();

			}
		});
		menu.getItems().addAll(open_item, scan_item, delete_item);
		tv_album.setContextMenu(menu);
	}

	public void setName(String name) {
		username.set(name);
	}

	public String getName() {
		return username.get();
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
					DBUtil.deleteAlbum(username.get());
					DBUtil.deletePhoto(username.get());
					DBUtil.deleteExpressions(username.get());
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
				new ChangePwdStage(username.get());
			}
		});
	}

	private void configureLinkChangePwd(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new ChangePwdStage(username.get());
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
		return DBUtil.getAlbums(username.get());
	}

}
