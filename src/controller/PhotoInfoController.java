package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Photo;
import view.PhotoInfoStage;

public class PhotoInfoController implements ControllerInitializable<PhotoInfoStage> {
	@FXML
	private TextField tf_name;
	@FXML
	private Label ll_path;
	@FXML
	private Label ll_createDate;
	@FXML
	private Label ll_size;
	@FXML
	private TextField tf_profile;
	private PhotoInfoStage stage;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureStage(PhotoInfoStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
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

}
