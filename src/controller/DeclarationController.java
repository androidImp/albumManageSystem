package controller;

import view.DeclerationStage;

public class DeclarationController implements ControllerInitializable<DeclerationStage> {
	DeclerationStage stage;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureStage(DeclerationStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

}
