package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.GridCell;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.DBUtil;
import view.AlbumInfoStage;
import view.PhotoBrowserStage;
import view.AlbumBrowser;

public class AlbumInfoCell extends GridCell<Album> {
	private Parent parent;
	private Label nameLabel;
	private Label photoNumberLabel;
	private ImageView firstImg;
	private ImageView secondImg;
	private ImageView thirdImg;
	private ImageView foutrhImg;
	private List<ImageView> imgs;

	public AlbumInfoCell() {
		// TODO Auto-generated constructor stub
		configureContextMenu();
		setOnMouseClicked(event -> {
			// TODO Auto-generated method stub
			if (((MouseEvent) event).getClickCount() >= 2) {
				AlbumBrowser stage = (AlbumBrowser) getGridView().getScene().getWindow();
				new PhotoBrowserStage(getItem(), stage.getUsername());
			}
		});

	}

	private void configureContextMenu() {
		// TODO Auto-generated method stub
		ContextMenu contextMenu = new ContextMenu();
		MenuItem scan_item = new MenuItem("浏览相册信息");
		MenuItem delete_item = new MenuItem("删除");
		MenuItem open_item = new MenuItem("打开");
		MenuItem update_item = new MenuItem("更新");
		configureItemScan(scan_item);
		configureItemDelete(delete_item);
		configureItemOpen(open_item);
		configureItemUpdate(update_item);
		contextMenu.getItems().addAll(open_item, scan_item, delete_item, update_item);
		setContextMenu(contextMenu);
	}

	private void configureItemUpdate(MenuItem update_item) {
		// TODO Auto-generated method stub
		update_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				updateItem(getItem(), false);

			}
		});
	}

	private void configureItemOpen(MenuItem open_item) {
		// TODO Auto-generated method stub
		open_item.setOnAction(event -> {
			// TODO Auto-generated method stub
			AlbumBrowser stage = (AlbumBrowser) getGridView().getScene().getWindow();
			new PhotoBrowserStage(getItem(), stage.getUsername());
		});
	}

	private void configureItemDelete(MenuItem delete_item) {
		// TODO Auto-generated method stub
		delete_item.setOnAction(event -> {
			// TODO Auto-generated method stub
			AlbumBrowser stage = (AlbumBrowser) getGridView().getScene().getWindow();
			getItem().delete(stage.getUsername());
			getGridView().getItems().remove(getIndex());
		});
	}

	private void configureItemScan(MenuItem scan_item) {
		// TODO Auto-generated method stub
		scan_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				new AlbumInfoStage(getItem());
			}
		});
	}

	@Override
	protected void updateItem(Album item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			try {
				parent = FXMLLoader.load(getClass().getResource("albumDetail.fxml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			nameLabel = (Label) parent.lookup("#ll_name");
			photoNumberLabel = (Label) parent.lookup("#ll_photoNumber");
			firstImg = (ImageView) parent.lookup("#img_first");
			secondImg = (ImageView) parent.lookup("#img_second");
			thirdImg = (ImageView) parent.lookup("#img_third");
			foutrhImg = (ImageView) parent.lookup("#img_fourth");
			imgs = new ArrayList<>();
			imgs.add(firstImg);
			imgs.add(secondImg);
			imgs.add(thirdImg);
			imgs.add(foutrhImg);
			ObservableList<String> uris = item.getPhotosUri();
			for (int i = 0; i < imgs.size(); i++) {
				if (uris.size() > i) {
					imgs.get(i).setImage(new Image(uris.get(i)));
				}

			}
			// setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);
			// -fx-effect: dropshadow(gaussian,"
			// +"orange, 10, 0, 0, 5);");
			nameLabel.setText(item.getAlbumName());
			photoNumberLabel.setText(String.valueOf(item.getPhotosNumber()));
			// parent.setStyle("-fx-background-color: white;" +
			// "-fx-effect: dropshadow(gaussian, red,10, 0, 0, 0);" +
			// "-fx-background-insets:10;");
			setGraphic(parent);
			getItem().photosUriProperty().addListener(new ListChangeListener<String>() {

				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
					// TODO Auto-generated method stub
					updateItem(getItem(), false);
				}
			});
		}
	}
}
