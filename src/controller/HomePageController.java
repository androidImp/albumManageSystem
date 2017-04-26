package controller;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

import cluster.KDSearchUtil;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.SearchItemCell;
import util.DBUtil;
import util.ParseUtil;
import util.DateUtil;
import util.DialogUtil;
import view.AlbumInfoStage;
import view.ChangePwdStage;
import view.DeclerationStage;
import view.HomeStage;
import view.ImageTableCell;
import view.LoginStage;
import view.PhotoBrowserStage;
import view.PhotoStage;
import view.PhotoBrowserStage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class HomePageController implements ControllerInitializable<HomeStage> {
	@FXML
	private Button btn_add;
	@FXML
	private ImageView img_usr_icon;
	@FXML
	private Label ll_albumName;
	@FXML
	private TreeView<String> tv_menu;
	@FXML
	Label ll_name;
	@FXML
	TextField tf_search;
	@FXML
	TableView<Album> tv_album;
	@FXML
	TableColumn<Album, String> columnCover;
	@FXML
	TableColumn<Album, String> columnName;
	@FXML
	TableColumn<Album, String> columnDate;
	@FXML
	TableColumn<Album, String> columnSize;
	@FXML
	TableColumn<Album, String> columnProfile;
	public HomeStage homeStage;

	@FXML
	public void initialize() {
		columnCover.setCellValueFactory(new PropertyValueFactory<>("coverUri"));
		columnCover.setCellFactory(new Callback<TableColumn<Album, String>, TableCell<Album, String>>() {

			@Override
			public TableCell<Album, String> call(TableColumn<Album, String> param) {
				// TODO Auto-generated method stub
				return new ImageTableCell();
			}
		});

		columnName.setCellValueFactory(new PropertyValueFactory<>("albumName"));
		columnDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
		columnSize.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Album, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Album, String> param) {
						// TODO Auto-generated method stub

						return Bindings.createStringBinding(new Callable<String>() {

							@Override
							public String call() throws Exception {
								// TODO Auto-generated method stub
								return ParseUtil.convertSizeToString(param.getValue().getSize());

							}
						}, param.getValue().sizeProperty());

					}
				});
		columnProfile.setCellValueFactory(new PropertyValueFactory<>("albumProfile"));

	}

	@FXML
	public void addAlbum() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("提示");
		dialog.setHeaderText("添加相片");
		// dialog.setGraphic(new
		// ImageView(this.getClass().getResource("/Pic/emptyCover.png").toString()));
		// Set the button types.
		ButtonType btn_confirm = new ButtonType("确认", ButtonData.OK_DONE);
		ButtonType btn_cancel = new ButtonType("取消", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(btn_confirm, btn_cancel);
		// Create the albumName and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		TextField albumName = new TextField();
		albumName.setPromptText("albumName");
		TextField profile = new TextField();
		albumName.setPromptText("profile");

		grid.add(new Label("albumName:"), 0, 0);
		grid.add(albumName, 1, 0);
		grid.add(new Label("profile:"), 0, 1);
		grid.add(profile, 1, 1);

		// Enable/Disable login button depending on whether a albumName was
		// entered.
		Node confirmButton = dialog.getDialogPane().lookupButton(btn_confirm);
		confirmButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		albumName.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the albumName field by default.
		Platform.runLater(() -> albumName.requestFocus());

		// Convert the result to a albumName-password-pair when the login button
		// is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == btn_confirm) {
				return new Pair<>(albumName.getText(), profile.getText());
			}
			return null;
		});
		Optional<Pair<String, String>> result = dialog.showAndWait();
		result.ifPresent(albumNamePassword -> {
			if (DBUtil.queryAlbum(homeStage.getUsername(), albumNamePassword.getKey())) {
				Album album = new Album();
				album.setId(HomeStage.index++);
				album.setAlbumName(albumNamePassword.getKey());
				album.setAlbumProfile(albumNamePassword.getValue());
				album.setCreateDate(DateUtil.getFormatDate());
				album.setPhotosNumber(0);
				album.setSize(0.0);
				album.setCoverUri("Pic/emptyCover.png");
				album.setPhotosUri(FXCollections.observableArrayList());
				tv_album.getItems().add(album);
				String name = homeStage.getUsername();
				DBUtil.saveAlbumInfo(album, name);

			} else {
				DialogUtil.showDialog(AlertType.CONFIRMATION, "已存在同名相册,请更改你想要的相册名后再试", "添加相册情况");
			}

		});
	}

	@FXML
	public void browsePhotos(MouseEvent event) {
		if (((MouseEvent) event).getClickCount() >= 2) {
			Album album = tv_album.getSelectionModel().getSelectedItem();
			if (album != null) {
				// new PhotoBrowserStage(album, homeStage.getUsername());
				new PhotoBrowserStage(album, homeStage.getUsername());
			}

		}
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
						ObservableList<Photo> photos = DBUtil.queryPhotoByName(tf_search.getText(),
								homeStage.getUsername());
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
				KDSearchUtil.queryNearestPic(homeStage.getUsername(), photo);

			}
		});
	}

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
					DBUtil.deleteAlbum(homeStage.getUsername());
					DBUtil.deletePhoto(homeStage.getUsername());
					DBUtil.deleteExpressions(homeStage.getUsername());
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
				new ChangePwdStage(homeStage.getUsername());
			}
		});
	}

	private void configureLinkChangePwd(Hyperlink link) {
		// TODO Auto-generated method stub
		link.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new ChangePwdStage(homeStage.getUsername());
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
				homeStage.hide();
				new LoginStage();
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
					DBUtil.deleteAlbum(id, homeStage.getUsername());
					DBUtil.deletePhotoByAlbum(id, homeStage.getUsername());
					DBUtil.deleteExpressionsOfAlbum(homeStage.getUsername(), id);
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
				new PhotoStage(tv_album.getSelectionModel().getSelectedItem(), homeStage.getUsername()).show();

			}
		});
		menu.getItems().addAll(open_item, scan_item, delete_item);
		tv_album.setContextMenu(menu);
	}

	@Override
	public void configureStage(HomeStage stage) {
		// TODO Auto-generated method stub
		this.homeStage = stage;

	}

}
