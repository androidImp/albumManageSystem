package view;

import javafx.application.Application;
import javafx.stage.Stage;

public class AppStart extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		setUserAgentStylesheet(STYLESHEET_MODENA);
		new LoginStage();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
