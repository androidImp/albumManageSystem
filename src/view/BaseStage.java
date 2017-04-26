package view;

import javafx.stage.Stage;

public abstract class BaseStage extends Stage {
	protected abstract void loadStageFromFXML();

	protected abstract void configureController();
	// TO DO configureCloseProperty
}
