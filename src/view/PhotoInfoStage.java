package view;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Album;
import util.DataUtil;

public class PhotoInfoStage extends Stage {
	private Album album;
	Label ll_date;
	Label ll_count;
	Label ll_size;
	TextField tf_name;
	TextField tf_profile;
	Parent root;

	public PhotoInfoStage(Album album) {
		// TODO Auto-generated constructor stub
		this.album = album;

		try {
			root = FXMLLoader.load(getClass().getResource("showAlbumInfo.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(root, 300, 300);
		setScene(scene);
		findViewById();
		configureName();
		configureProfile();
		configureInfo();
		show();
	}

	private void configureInfo() {
		// TODO Auto-generated method stub
		tf_name.setText(album.getAlbumName());
		tf_profile.setText(album.getAlbumProfile());
		ll_date.setText(album.getCreateDate());
		ll_count.setText(album.getPhotosNumber().toString());
		ll_size.setText(DataUtil.convertSizeToString(album.getSize()));
	}

	public void configureName() {
		tf_name.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				tf_name.setText(newValue);
				album.setAlbumName(newValue);
			}
		});
	}

	public void configureProfile() {
		tf_profile.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				tf_profile.setText(newValue);
				album.setAlbumProfile(newValue);
			}
		});
	}

	public void findViewById() {
		ll_date = (Label) root.lookup("#ll_date");
		ll_count = (Label) root.lookup("#ll_count");
		ll_size = (Label) root.lookup("#ll_size");
		tf_name = (TextField) root.lookup("#tf_name");
		tf_profile = (TextField) root.lookup("#tf_profile");
	}
}
