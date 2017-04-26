package controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public interface ControllerInitializable<T extends Stage> {
	@FXML
	public void initialize();

	public void configureStage(T stage);
}
