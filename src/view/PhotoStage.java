package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.alibaba.simpleimage.analyze.sift.SIFT;
import com.alibaba.simpleimage.analyze.sift.render.RenderImage;

import cluster.ClusterUtils;
import cluster.ImagePoint;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.Album;
import model.ImageCell;
import model.Photo;
import util.DBUtil;
import util.DataUtil;
import util.DateUtil;
import util.FileChooserUtil;
import util.LogUtil;

public class PhotoStage extends BaseStage {
	public int EMPTY = 2147483647;
	public int DEFAULT_HEIGHT = 200;
	public int DEFAULT_WIDTH = 250;
	public int MAX_HEIGHT = 300;
	public int MAX_WIDTH = 375;
	public int MIN_HEIGHT = 100;
	public int MIN_WIDTH = 125;
	public int HEIGHT_INCREAMENT = 20;
	public int WIDTH_INCREAMENT = 25;
	SimpleStringProperty username = new SimpleStringProperty("name");
	private Album album;
	@FXML
	Pagination pg_photo;
	@FXML
	ListView<Photo> lv_photo;
	@FXML
	Button btn_add;
	@FXML
	Button btn_shrink;
	@FXML
	Button btn_enlarge;
	@FXML
	Button btn_set_cover;
	@FXML
	Button btn_delete;
	IntegerProperty default_height = new SimpleIntegerProperty(DEFAULT_HEIGHT);
	IntegerProperty default_width = new SimpleIntegerProperty(DEFAULT_WIDTH);
	FileChooser fileChooser;
	Parent parent = null;

	public void initView() {
		try {
			parent = FXMLLoader.load(getClass().getResource("photos.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pg_photo = (Pagination) parent.lookup("#pg_photo");
		lv_photo = (ListView<Photo>) parent.lookup("#lv_photo");
		btn_add = (Button) parent.lookup("#btn_add");
		btn_enlarge = (Button) parent.lookup("#btn_enlarge");
		btn_shrink = (Button) parent.lookup("#btn_shrink");
		btn_set_cover = (Button) parent.lookup("#btn_set_cover");
		btn_delete = (Button) parent.lookup("#btn_delete");
		fileChooser = new FileChooser();
		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				DBUtil.savePhotos(lv_photo.getItems(), username.get());
			}
		});
	}

	public PhotoStage(Album album, String name) {
		// TODO Auto-generated constructor stub
		this.album = album;
		setName(name);
		initView();
		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		configureListView();
		configurePagination();
		// 绑定相册地址集合的大小和 pagination 的 count 属性.
		configurePaginationCount();
		configureButtonAdd();
		configureButtonEnlarge();
		configureButtonShrink();
		configureButtonSetCover();
		configureButtonDelete();
	}

	private void configurePaginationCount() {
		pg_photo.pageCountProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				if (oldValue.intValue() == EMPTY && newValue.intValue() != EMPTY) {
					pg_photo.setVisible(true);
				} else if (newValue.intValue() == EMPTY || newValue.intValue() == 0) {
					pg_photo.setVisible(false);
				}
			}
		});
		pg_photo.currentPageIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				lv_photo.getSelectionModel().select(newValue.intValue());
			}
		});
	}

	public void configureListView() {
		lv_photo.setItems(DBUtil.getPhotosByAlbum(album.getId(), username.get()));
		lv_photo.setCellFactory((ListView<Photo> l) -> new ImageCell());
		lv_photo.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				int index = lv_photo.getSelectionModel().getSelectedIndex();
				pg_photo.setCurrentPageIndex(index);
			}
		});
	}

	public void configurePagination() {
		// 设置 pagination 的内容;
		pg_photo.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer index) {
				// TODO Auto-generated method stub
				VBox vBox = new VBox();
				vBox.setAlignment(Pos.CENTER);
				ImageView imageView = new ImageView();
				imageView.fitWidthProperty().bind(default_width);
				imageView.fitHeightProperty().bind(default_width);
				if (album.getPhotosUri().size() > index) {
					if (index != -1) {
						String url = album.getPhotosUri().get(index);
						if (url.length() > 0) {
							imageView.setImage(new Image(url));
						}
					}
				}
				vBox.getChildren().add(imageView);
				return vBox;
			}
		});
		if (album.getPhotosUri().size() <= 0) {
			pg_photo.setVisible(false);
		}

		pg_photo.setPageCount(album.getPhotosUri().size());
		album.getPhotosUri().addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
				// TODO Auto-generated method stub
				int size = c.getList().size();
				if (size > 0) {
					pg_photo.setPageCount(size);
				} else {
					pg_photo.setPageCount(EMPTY);
				}
			}
		});

	}

	private void configureButtonSetCover() {
		btn_set_cover.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Photo photo = lv_photo.getSelectionModel().getSelectedItem();
				if (photo != null) {
					album.setCoverUri(photo.getUri());
				}
			}
		});
	}

	private void configureButtonShrink() {
		btn_shrink.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				int height = default_height.get();
				int width = default_width.get();
				height -= HEIGHT_INCREAMENT;
				width -= WIDTH_INCREAMENT;
				if (height < MIN_HEIGHT)
					height = MIN_HEIGHT;
				if (width < MIN_WIDTH)
					width = MIN_WIDTH;
				default_height.set(height);
				default_width.set(width);
			}
		});
	}

	private void configureButtonEnlarge() {
		btn_enlarge.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				int height = default_height.get();
				int width = default_width.get();
				height += HEIGHT_INCREAMENT;
				width += WIDTH_INCREAMENT;
				if (height > MAX_HEIGHT)
					height = MAX_HEIGHT;
				if (width > MAX_WIDTH)
					width = MAX_WIDTH;
				default_height.set(height);
				default_width.set(width);
			}
		});
	}

	private void configureButtonAdd() {
		btn_add.setOnAction((final ActionEvent e) -> {
			FileChooserUtil.configureFileChooser(fileChooser);
			List<File> filesList = fileChooser.showOpenMultipleDialog(PhotoStage.this);
			if (filesList != null) {
				double size = album.getSize();
				for (int i = 0; i < filesList.size(); i++) {
					File file = filesList.get(i);
					String path = file.getAbsoluteFile().toURI().toString();
					size += file.length();
					Photo photo = new Photo();
					photo.setMd5(DataUtil.getMD5(file));
					photo.setId(album.getId());
					photo.setName(file.getName());
					photo.setUri(path);
					photo.setCreateDate(DateUtil.getFormatDate(file.lastModified()));
					photo.setSize(file.length());
					album.getPhotosUri().add(path);
					lv_photo.getItems().add(photo);
				}
				DBUtil.savePhotos(lv_photo.getItems(), getName());
				album.setSize(size);
			}

			// sift 特征直方图存储;
			new Thread(new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					// TODO Auto-generated method stub
					SIFT sift = new SIFT();
					for (File file : filesList) {
						BufferedImage image = null;
						image = ImageIO.read(file);
						RenderImage ri = new RenderImage(image);
						sift.detectFeatures(ri.toPixelFloatArray(null));
						ImagePoint imagePoint = new ImagePoint(sift.getGlobalFeaturePoints());
						try {
							DBUtil.addExpression(username.get(), album.getId(), DataUtil.getMD5(file),
									file.getAbsoluteFile().toURI().toString(),
									DataUtil.doubleArrayToExpression(ClusterUtils.distribute(imagePoint)));
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					return null;
				}
			}).start();

		});
	}

	private void configureButtonDelete() {
		// TODO Auto-generated method stub
		btn_delete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				int index = lv_photo.getSelectionModel().getSelectedIndex();
				if (index == -1) {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.initStyle(StageStyle.UTILITY);
					alert.setTitle("Warning");
					alert.setHeaderText("删除相片");
					alert.setContentText("没有选中相片可以被删除");
					alert.showAndWait();
					LogUtil.i(getClass().getName(), "没有选中相片可以被删除");
				} else {
					if (index < lv_photo.getItems().size()) {
						Photo photo = lv_photo.getSelectionModel().getSelectedItem();
						if (photo != null) {
							album.getPhotosUri().remove(index);
							lv_photo.getItems().remove(index);
							DBUtil.deletePhoto(photo.getMd5(), photo.getId(), username.get());
							DBUtil.deleteExpressionOfPhoto(username.get(), photo.getId(), photo.getMd5());
							File file = new File(photo.getUri());
							album.setSize(album.getSize() - file.length());
						}
					}
				}
			}
		});

	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public void setName(String name) {
		username.set(name);
	}

	public String getName() {
		return username.get();
	}
}
