package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.ParseUtil;
import view.AlbumInfoStage;

public class ShowAlbumInfoController implements ControllerInitializable<AlbumInfoStage> {

	@FXML
	private Label ll_date;
	@FXML
	private Label ll_count;
	@FXML
	private Label ll_size;
	@FXML
	private TextField tf_name;
	@FXML
	private TextField tf_profile;
	@FXML
	AlbumInfoStage stage;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		configureName();
		configureProfile();
		configureInfo();
	}

	@Override
	public void configureStage(AlbumInfoStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

	private void configureInfo() {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {
			public void run() {
				tf_name.setText(stage.getAlbum().getAlbumName());
				tf_profile.setText(stage.getAlbum().getAlbumProfile());
				ll_date.setText(stage.getAlbum().getCreateDate());
				ll_count.setText(String.valueOf(stage.getAlbum().getPhotosNumber()));
				ll_size.setText(ParseUtil.convertSizeToString(stage.getAlbum().getSize()));
			}
		});

	}

	public void configureName() {

		tf_name.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				stage.getAlbum().setAlbumName(newValue);
			}
		});

	}

	public void configureProfile() {
		tf_profile.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				stage.getAlbum().setAlbumProfile(newValue);
			}
		});

	}
}
