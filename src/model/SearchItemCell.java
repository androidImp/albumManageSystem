package model;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 
 * @author niuniumei 用来表示搜索框中的搜索结果的每一个item.
 */
public class SearchItemCell extends ListCell<Photo> {
	@Override
	protected void updateItem(Photo item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		HBox hBox = new HBox();
		hBox.setSpacing(20);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		ImageView img_photo = new ImageView();
		img_photo.setFitHeight(50);
		img_photo.setFitWidth(50);
		if (!empty) {
			img_photo.setImage(new Image(item.getUri()));
			Label ll_name = new Label(item.getName());
			hBox.getChildren().addAll(ll_name, img_photo);
			setGraphic(hBox);
		}

	}
}
