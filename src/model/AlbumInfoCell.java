package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.GridCell;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AlbumInfoCell extends GridCell<Album> {
	private Parent parent;
	private Label nameLabel;
	private Label photoNumberLabel;
	private ImageView firstImg;
	private ImageView secondImg;
	private ImageView thirdImg;
	private ImageView foutrhImg;
	private List<ImageView> imgs;

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
//			setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-effect: dropshadow(gaussian," 
//	+"orange, 10, 0, 0, 5);");
			nameLabel.setText(item.getAlbumName());
			photoNumberLabel.setText(String.valueOf(item.getPhotosNumber()));
			// parent.setStyle("-fx-background-color: white;" +
			// "-fx-effect: dropshadow(gaussian, red,10, 0, 0, 0);" +
			// "-fx-background-insets:10;");
			
			setGraphic(parent);

		}
	}
}
