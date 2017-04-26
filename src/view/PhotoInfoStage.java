package view;

import java.io.IOException;

import controller.PhotoInfoController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Photo;
import util.ParseUtil;

public class PhotoInfoStage extends BaseStage {

	TextField tf_name;
	TextField tf_profile;
	Label ll_path;
	Label ll_createDate;
	Label ll_size;
	Parent root;
	FXMLLoader loader;
	Photo photo;

	public PhotoInfoStage(Photo photo) {
		setPhoto(photo);
		loadStageFromFXML();
		configureController();
		lookUpViewId();

		showPhotoInfo(photo);
	}

	private void showPhotoInfo(Photo photo) {
		// TODO Auto-generated method stub
		tf_name.setText(photo.getName());
		configureName(photo);
		tf_profile.setText(photo.getProfile());
		configureProfile(photo);
		ll_createDate.setText(photo.getCreateDate());
		ll_path.setText(photo.getUri());
		ll_size.setText(ParseUtil.convertSizeToString(photo.getSize()));
	}

	private void configureName(Photo photo) {
		// TODO Auto-generated method stub
		tf_name.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				photo.setName(newValue);
				photo.setModified(true);
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
				photo.setModified(true);
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

	@Override
	protected void loadStageFromFXML() {
		// TODO Auto-generated method stub
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("photoInfo.fxml"));
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		setScene(scene);
		show();
	}

	@Override
	protected void configureController() {
		// TODO Auto-generated method stub
		if (loader != null) {
			PhotoInfoController controller = loader.getController();
			controller.configureStage(this);
		}

	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
}
