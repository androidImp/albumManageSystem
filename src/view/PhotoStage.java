package view;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.ImageCell;
import util.FileChooserUtil;

public class PhotoStage extends Stage {
	public int EMPTY = 2147483647;
	private Album album;
	@FXML
	Pagination pg_photo;
	@FXML
	ListView<String> lv_photo;
	@FXML
	Button btn_shrink;
	@FXML
	Button btn_add;
	FileChooser fileChooser;

	public PhotoStage(Album album) {
		// TODO Auto-generated constructor stub

		Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("photos.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pg_photo = (Pagination) parent.lookup("#pg_photo");
		lv_photo = (ListView<String>) parent.lookup("#lv_photo");
		Scene scene = new Scene(parent);
		setScene(scene);
		// 初始化 listview
		lv_photo.setItems(album.getPhotosUri());
		lv_photo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

			@Override
			public ListCell<String> call(ListView<String> param) {
				// TODO Auto-generated method stub
				return new ImageCell();
			}
		});
		lv_photo.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				int index = lv_photo.getSelectionModel().getSelectedIndex();
				pg_photo.setCurrentPageIndex(index);
			}
		});
		// 设置 pagination 的内容;
		pg_photo.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer index) {
				// TODO Auto-generated method stub
				VBox vBox = new VBox();
				vBox.setAlignment(Pos.CENTER);
				ImageView imageView = new ImageView();
				imageView.setFitHeight(200);
				imageView.setFitWidth(300);
				if (album.getPhotosUri().size() > index) {
					Image image = null;
					try {
						image = new Image(album.getPhotosUri().get(index));
						imageView.setImage(image);
					} catch (Exception e) {
						// TODO: handle exception
						Logger.getLogger(PhotoStage.class.getName()).log(Level.SEVERE, "该图片可能已被删除或者移动到新位置", e);
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
		// 当 pagination 的 pagecount 为0时不显示.
		pg_photo.pageCountProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				if (oldValue.intValue() == EMPTY && newValue.intValue() != EMPTY) {
					pg_photo.setVisible(true);
				} else if (newValue.intValue() == EMPTY) {
					pg_photo.setVisible(false);
				}
			}
		});
		// 绑定相册地址集合的大小和 pagination 的 count 属性.
		album.getPhotosUri().addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
				// TODO Auto-generated method stub
				pg_photo.setPageCount(album.getPhotosUri().size());
			}
		});
		btn_add = (Button) parent.lookup("#btn_add");
		fileChooser = new FileChooser();
		btn_add.setOnAction((final ActionEvent e) -> {
			FileChooserUtil.configureFileChooser(fileChooser);
			List<File> filesList = fileChooser.showOpenMultipleDialog(PhotoStage.this);
			if (filesList != null) {
				for (int i = 0; i < filesList.size(); i++) {
					File file = filesList.get(i);
					album.getPhotosUri().add(file.getAbsoluteFile().toURI().toString());
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
}
