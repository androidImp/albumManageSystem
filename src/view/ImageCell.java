package view;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import cluster.KDSearchUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Album;
import model.Photo;

public class ImageCell extends GridCell<Photo> {
	public ImageCell() {
		// TODO Auto-generated constructor stub
		configureImageMenu();
		setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				if (((MouseEvent) event).getClickCount() >= 2) {
					getGridView().setVisible(false);

				}

			}
		});
	}

	private void configureImageMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem scan_item = new MenuItem("浏览信息");
		MenuItem delete_item = new MenuItem("删除");
		MenuItem rotate_item = new MenuItem("旋转");
		MenuItem search_item = new MenuItem("查找相似图片");
		configureItemScan(scan_item);
		configureItemDelete(delete_item);
		configureItemRotate(rotate_item);
		configureItemSearch(search_item);
		contextMenu.getItems().addAll(scan_item, delete_item, rotate_item, search_item);
		setContextMenu(contextMenu);
	}

	private void configureItemSearch(MenuItem search_item) {
		// TODO Auto-generated method stub
		search_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				KDSearchUtil.queryNearestPic(((PhotoBrowserStage) getGridView().getScene().getWindow()).getUsername(),
						getItem());
			}
		});

	}

	private void configureItemRotate(MenuItem rotate_item) {
		rotate_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				double rotate = getGraphic().getRotate();
				rotate = (rotate + 90) % 360;
				getGraphic().setRotate(rotate);

			}
		});
	}

	private void configureItemDelete(MenuItem delete_item) {
		delete_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				int index = getIndex();
				GridView<Photo> gridView = getGridView();
				gridView.getItems().remove(index);
				PhotoBrowserStage stage = (PhotoBrowserStage) gridView.getScene().getWindow();
				stage.getAlbum().getPhotosUri().remove(index);
				Album album = stage.getAlbum();
				// 数据库中存储的文件路径为 file:(path),所以这里去掉了前5个字符:
				Photo photo = getItem();
				album.setSize(album.getSize() - photo.getSize());
				photo.deletePhoto(stage.getUsername());

			}
		});
	}

	private void configureItemScan(MenuItem scan_item) {
		scan_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO
				new PhotoInfoStage(getItem());

			}
		});
	}

	@Override
	protected void updateItem(Photo item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		if (!empty) {
			VBox vBox = new VBox();
			vBox.setAlignment(Pos.CENTER);
			Image image = new Image(item.getUri(), true);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(80);
			imageView.setFitWidth(80);
			vBox.getChildren().add(imageView);
			setGraphic(vBox);
		}
	}
}
