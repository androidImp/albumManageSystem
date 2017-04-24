package model;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ImageListCell extends ListCell<Photo> {
	@Override
	protected void updateItem(Photo item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		ImageView img_photo = new ImageView();
		img_photo.setFitHeight(80);
		img_photo.setFitWidth(80);
		if (!empty) {
			img_photo.setImage(new Image(item.getUri()));
		}
		vBox.getChildren().add(img_photo);
		setGraphic(vBox);
	}

}
