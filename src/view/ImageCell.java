package view;

import org.controlsfx.control.GridCell;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Photo;
import util.DBUtil;

public class ImageCell extends GridCell<Photo> {
	public ImageCell() {
		// TODO Auto-generated constructor stub
		configureImageMenu();
	}

	private void configureImageMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem scan_item = new MenuItem("浏览信息");
		MenuItem delete_item = new MenuItem("删除");
		MenuItem rotate_item = new MenuItem("旋转");
		configureItemScan(scan_item);
		configureItemDelete(delete_item);
		configureItemRotate(rotate_item);
		contextMenu.getItems().addAll(scan_item, delete_item, rotate_item);
		setContextMenu(contextMenu);
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
				getGridView().getItems().remove(getIndex());
				Photo photo = getItem();
				DBUtil.deletePhoto(photo.getMd5(), photo.getId(),
						((ShowPhotosStage) getGridView().getScene().getWindow()).getUsername());
			}
		});
	}

	private void configureItemScan(MenuItem scan_item) {
		scan_item.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO
				// show photoInfoStage
				// getIndex();
			}
		});
	}

	@Override
	protected void updateItem(Photo item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		if (!empty) {
			VBox vBox = new VBox();
			Image image = new Image(item.getUri(), true);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(80);
			imageView.setFitWidth(80);
			vBox.getChildren().add(imageView);
			setGraphic(vBox);
		}
	}
}
