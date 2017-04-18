package model;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
	private Label ll_name;
	@FXML
	private Label ll_created_date;
	@FXML
	private Label ll_space_usage;
	Parent root = null;

	public void configureLayout() {
		try {
			root = FXMLLoader.load(getClass().getResource("cell.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img_cover = (ImageView) root.lookup("#img_cover");
		ll_name = (Label) root.lookup("#ll_name");
		ll_space_usage = (Label) root.lookup("#ll_space_usage");
		ll_created_date = (Label) root.lookup("#ll_created_date");
	}

	public void configureItem(Album item) {
		item.coverUriProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				img_cover.setImage(new Image(newValue));
			}
		});
		item.albumNameProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				ll_name.setText(newValue);
			}
		});

		item.sizeProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				ll_space_usage.setText(DataUtil.convertSizeToString(newValue.doubleValue()));
			}
		});
	}

	private void addContent(Album item) {
		img_cover.setImage(new Image(item.getCoverUri()));
		ll_name.setText(item.getAlbumName());
		ll_space_usage.setText(DataUtil.convertSizeToString(item.getSize()));
		ll_created_date.setText(item.getCreateDate());
		setGraphic(root);
	}

	@Override
	protected void updateItem(Album item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		// create the view;
		if (!empty) {
			configureLayout();
			configureItem(item);
			addContent(item);
		} else {
			setGraphic(null);
		}

	}

}
