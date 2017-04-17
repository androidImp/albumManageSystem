package view;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Photo;
import util.DataUtil;

public class PhotoInfoStage extends BaseStage {
	Parent root = null;
	TextField tf_name;
	TextField tf_profile;
	Label ll_path;
	Label ll_createDate;
	Label ll_size;

	public PhotoInfoStage(Photo photo) {

		try {
			root = FXMLLoader.load(getClass().getResource("photoInfo.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lookUpViewId();
		showPhotoInfo(photo);
		Scene scene = new Scene(root);
		setScene(scene);
		show();
	}

	private void showPhotoInfo(Photo photo) {
		// TODO Auto-generated method stub
		tf_name.setText(photo.getName());
		configureName(photo);
		tf_profile.setText(photo.getProfile());
		configureProfile(photo);
		ll_createDate.setText(photo.getCreateDate());
		ll_path.setText(photo.getUri());
		ll_size.setText(DataUtil.convertSizeToString(photo.getSize()));
	}

	private void configureName(Photo photo) {
		// TODO Auto-generated method stub
		tf_name.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				photo.setName(newValue);
			}
		});
	}

	private void configureProfile(Photo photo) {
		// TODO Auto-generated method stub
		tf_profile.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				photo.setProfile(newValue);
			}
		});
	}

	private void lookUpViewId() {
		// TODO Auto-generated method stub
		tf_name = (TextField) root.lookup("#tf_name");
		tf_profile = (TextField) root.lookup("#tf_profile");
		ll_path = (Label) root.lookup("#ll_path");
		ll_createDate = (Label) root.lookup("#ll_createDate");
		ll_size = (Label) root.lookup("#ll_size");

	}
}
