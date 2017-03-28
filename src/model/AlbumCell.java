package model;

import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.DataUtil;

public class AlbumCell extends ListCell<Album> {
	@FXML
	private ImageView img_cover;
	@FXML
	private TextField tf_name;
	@FXML
	private Label ll_created_date;
	@FXML
	private Label ll_space_usage;

	@Override
	protected void updateItem(Album item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		// create the view;
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("cell.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img_cover = (ImageView) root.lookup("#img_cover");
		tf_name = (TextField) root.lookup("#tf_name");
		ll_space_usage = (Label) root.lookup("#ll_space_usage");
		ll_created_date = (Label) root.lookup("#ll_created_date");
		tf_name.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				item.setAlbumName(newValue);
			}
		});
		if (!empty) {
			img_cover.setImage(new Image(item.getCoverUri()));
			tf_name.setText(item.getAlbumName());
			ll_space_usage.setText(DataUtil.convertSizeToString(item.getSize()));
			ll_created_date.setText(item.getCreateDate());
		}
		setGraphic(root);
	}

}
