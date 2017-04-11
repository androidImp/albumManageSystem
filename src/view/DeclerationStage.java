package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DeclerationStage extends BaseStage {
	public DeclerationStage() {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("declaration.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(root != null){
			Scene scene = new Scene(root,300,200);
			setScene(scene);
			show();
		}
		
	}
}
