package view;

import java.io.IOException;

import controller.DeclarationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class DeclerationStage extends BaseStage {
	Parent root;
	FXMLLoader loader;

	public DeclerationStage() {
		loadStageFromFXML();
		configureController();
	}

	@Override
	protected void loadStageFromFXML() {
		// TODO Auto-generated method stub
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("declaration.fxml"));
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (root != null) {
			Scene scene = new Scene(root, 300, 200);
			setScene(scene);
			show();
		}
	}

	@Override
	protected void configureController() {
		// TODO Auto-generated method stub
		DeclarationController controller = loader.getController();
		controller.configureStage(this);
	}
}
