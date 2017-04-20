package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.controlsfx.control.GridView;

import com.alibaba.simpleimage.analyze.sift.SIFT;
import com.alibaba.simpleimage.analyze.sift.render.RenderImage;

import cluster.ClusterUtils;
import cluster.ImagePoint;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.Album;
import model.Photo;
import util.DBUtil;
import util.DataUtil;
import util.DateUtil;
import util.FileChooserUtil;

public class ShowPhotosStage extends BaseStage {
	SimpleStringProperty username = new SimpleStringProperty("name");
	private Album album;
	GridView<Photo> gv_photo;
	Parent root = null;
	private Label ll_title;
	private Label ll_date;
	private Button btn_add;
	private TextField tf_search;
	private FileChooser fileChooser;
	private HBox hb_scan;
	private ImageView img_scan;

	public ShowPhotosStage(Album album, String name) {
		// TODO Auto-generated constructor stub
		setUsername(name);
		this.album = album;
		initView();
		lookUpViewById();
		configurePhotoList();
		configureTitle();
		configureButtonAdd();
		configureCloseProperty();
		show();

	}

	private void initView() {
		// TODO Auto-generated method stub
		try {
			root = FXMLLoader.load(getClass().getResource("photosList.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		fileChooser = new FileChooser();
	}

	private void configureCloseProperty() {
		// TODO Auto-generated method stub
		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				DBUtil.savePhotos(gv_photo.getItems(), username.get());
			}
		});
	}

	private void configureTitle() {
		// TODO Auto-generated method stub
		ll_title.setText(album.getAlbumName());
		ll_date.setText(album.getCreateDate());

	}

	// To do background;
	@SuppressWarnings("unchecked")
	public void lookUpViewById() {
		gv_photo = (GridView<Photo>) root.lookup("#gv_photo");
		ll_title = (Label) root.lookup("#ll_title");
		ll_date = (Label) root.lookup("#ll_date");
		btn_add = (Button) root.lookup("#btn_add");
		tf_search = (TextField) root.lookup("#tf_search");
		hb_scan = (HBox) root.lookup("#hb_scan");
		img_scan = (ImageView) root.lookup("#img_scan");
		hb_scan.visibleProperty().bind(gv_photo.visibleProperty().not());
		Image image = new Image("/Pic/bg.jpeg");
		img_scan.setImage(image);

		img_scan.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if (((MouseEvent) event).getClickCount() >= 2) {
					gv_photo.setVisible(true);
				}
			}
		});

	}

	public void configurePhotoList() {
		gv_photo.setCellWidth(85);
		gv_photo.setCellHeight(85);
		gv_photo.setPadding(new Insets(10));
		gv_photo.setHorizontalCellSpacing(5);
		gv_photo.setVerticalCellSpacing(5);
		new Thread(new RenderImageTask()).start();
		gv_photo.setCellFactory(param -> new ImageCell());

	}

	public final SimpleStringProperty usernameProperty() {
		return this.username;
	}

	public final String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
	}

	class RenderImageTask extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			// TODO Auto-generated method stub
			ObservableList<Photo> photos = DBUtil.getPhotosByAlbum(album.getId(), username.get());
			gv_photo.setItems(photos);
			return null;
		}
	}

	class RenderExpressionTask extends Task<Void> {
		List<File> filesList;

		public RenderExpressionTask(List<File> filesList) {
			// TODO Auto-generated constructor stub
			this.filesList = filesList;
		}

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

	}

	private void configureButtonAdd() {
		// TODO Auto-generated method stub
		btn_add.setOnAction((final ActionEvent e) -> {
			FileChooserUtil.configureFileChooser(fileChooser);
			List<File> filesList = fileChooser.showOpenMultipleDialog(ShowPhotosStage.this);
			if (filesList != null) {
				fileChooser.setInitialFileName(filesList.get(0).getAbsolutePath());
				double size = album.getSize();
				for (int i = 0; i < filesList.size(); i++) {
					File file = filesList.get(i);
					String path = file.getAbsoluteFile().toURI().toString();
					size += file.length();
					Photo photo = new Photo();
					photo.setMd5(DataUtil.getMD5(file));
					photo.setId(album.getId());
					int index = file.getName().lastIndexOf(".");
					photo.setName(file.getName().substring(0, index));
					photo.setUri(path);
					photo.setCreateDate(DateUtil.getFormatDate(file.lastModified()));
					photo.setSize(file.length());
					album.getPhotosUri().add(path);
					gv_photo.getItems().add(photo);
				}
				DBUtil.savePhotos(gv_photo.getItems(), getUsername());
				album.setPhotosNumber(album.getPhotosNumber() + filesList.size());
				album.setSize(size);
			}

			// sift 特征直方图存储;
			new Thread(new RenderExpressionTask(filesList)).start();

		});
	}

	public void deletePhoto(int index) {
		Photo photo = gv_photo.getItems().get(index);
		if (photo != null) {
			album.getPhotosUri().remove(index);
			gv_photo.getItems().remove(index);
			DBUtil.deletePhoto(photo.getMd5(), photo.getId(), username.get());
			DBUtil.deleteExpressionOfPhoto(username.get(), photo.getId(), photo.getMd5());
			// 数据库中存储的文件路径为 file:(path),所以这里去掉了前5个字符:
			File file = new File(photo.getUri().substring(5, photo.getUri().length()));
			album.setSize(album.getSize() - file.length());

		}
	}
}
