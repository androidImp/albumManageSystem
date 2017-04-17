package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Album;

public class ImageTableCell extends TableCell<Album, String> {
	@Override
	protected void updateItem(String item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		if (!empty) {
			VBox vBox = new VBox();
			vBox.setAlignment(Pos.CENTER);
			ImageView imageView = new ImageView(item);
			imageView.setFitHeight(100);
			imageView.setFitWidth(100);
			vBox.getChildren().add(imageView);
			VBox.setMargin(imageView, new Insets(10));
			setGraphic(vBox);
		} else {
			setGraphic(null);
		}

	}
}
