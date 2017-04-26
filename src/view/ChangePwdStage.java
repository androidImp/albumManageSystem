package view;

import java.io.IOException;

import controller.ChangePwdController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChangePwdStage extends BaseStage {
	public SimpleStringProperty username = new SimpleStringProperty("name");
	FXMLLoader loader;
	Parent root;

	public ChangePwdStage(String name) {
		setUsername(name);
		loadStageFromFXML();
		configureController();
	}

	public final SimpleStringProperty usernameProperty() {
		return this.username;
	}

	public final String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
	}

	@Override
	protected void loadStageFromFXML() {
		// TODO Auto-generated method stub
		loader = new FXMLLoader();
		try {
			loader.setLocation(getClass().getResource("changePwd.fxml"));
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene scene = new Scene(root, 300, 300);
		setScene(scene);
		show();
	}

	@Override
	protected void configureController() {
		// TODO Auto-generated method stub

		ChangePwdController changePwdController = loader.getController();
		changePwdController.configureStage(this);

	}

}
