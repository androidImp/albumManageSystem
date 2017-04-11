package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.commons.math3.ml.clustering.CentroidCluster;

import cluster.ClusterUtils;
import cluster.Point;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.AlbumCell;
import model.Photo;
import model.SearchItemCell;
import util.DBUtil;

public class HomeStage extends BaseStage {
	public static int index;
	private static final int DIMENSIONS_OF_KDTREE = 100;
	ListView<Album> ls_album;
	Parent root = null;
	Label ll_name;
	TextField tf_search;
	TreeView<String> tv_menu;
	SimpleStringProperty username = new SimpleStringProperty("name");
	KDTree<String> kdTree = new KDTree<>(DIMENSIONS_OF_KDTREE);
	List<CentroidCluster<Point>> centers = new ArrayList<>();

	public HomeStage(String name) {
		// TODO Auto-generated constructor stub
		try {
			root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
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
		DBUtil.createExpressionsTable(username.get());
		configureCluster();
		configureKDTree();
	}

	private void configureKDTree() {
		// TODO Auto-generated method stub
		List<Point> points = DBUtil.getExpressions(username.get(), DIMENSIONS_OF_KDTREE);
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			try {
				kdTree.insert(points.get(i).getPoint(), point.getUrl());
			} catch (KeySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyDuplicateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

			} catch (ClassNotFoundException | IOException e) {
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
				DBUtil.saveAlbums(ls_album.getItems(), username.get());
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
	private void configureSearchItem(ListView<Photo> lv) {
		lv.setCellFactory(param -> new SearchItemCell());
		lv.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				Photo photo = lv.getSelectionModel().getSelectedItem();
				double[] key = DBUtil.queryExpression(username.get(), DIMENSIONS_OF_KDTREE, photo.getId(),
						photo.getMd5());
				if (key != null) {
					try {
						List<String> uris = kdTree.nearest(key, 10);
						Stage stage = new Stage();
						VBox root = new VBox();
						Scene scene = new Scene(root, 400, 400);
						stage.setScene(scene);
						ListView<ImageView> listView = new ListView<ImageView>();
						root.getChildren().add(listView);
						listView.getItems().add(new ImageView(photo.getUri()));
						for (String string : uris) {
							ImageView imageView = new ImageView(string);
							listView.getItems().add(imageView);
						}
						stage.show();
					} catch (KeySizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}

	/**
	 * 初始化用户数据
	 */
	private void configureData() {
		DBUtil.createPhotosTable(username.get());
		ls_album.setItems(DBUtil.getAlbums(username.get()));
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		ls_album = (ListView<Album>) root.lookup("#ls_album");
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
		rootItem.getChildren().addAll(item_clear, item_setting, item_modify, item_info);
		tv_menu.setRoot(rootItem);
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
//						new PhotoStage(album, getName()).show();
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
				int index = ls_album.getSelectionModel().getSelectedIndex();
				if (index != -1) {
					int id = ls_album.getSelectionModel().getSelectedItem().getId();
					ls_album.getItems().remove(index);
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
				new AlbumInfoStage(ls_album.getSelectionModel().getSelectedItem());
			}
		});
		open_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new PhotoStage(ls_album.getSelectionModel().getSelectedItem(), getName()).show();

			}
		});
		menu.getItems().addAll(open_item, scan_item, delete_item);
		ls_album.setContextMenu(menu);
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
				ls_album.getItems().clear();
				DBUtil.deleteAlbum(username.get());
				DBUtil.deletePhoto(username.get());
				DBUtil.deleteExpressions(username.get());
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
}
