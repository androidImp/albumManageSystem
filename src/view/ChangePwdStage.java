package view;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChangePwdStage extends BaseStage{
	public SimpleStringProperty username = new SimpleStringProperty("name");
	public ChangePwdStage(String name){
		Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("changePwd.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setUsername(name);
		Scene scene = new Scene(parent,300,300);
		setScene(scene);
		show();
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
	
}
