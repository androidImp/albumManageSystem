package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import util.DBUtil;
import util.ParseUtil;
import view.ChangePwdStage;

public class ChangePwdController implements ControllerInitializable<ChangePwdStage> {
	@FXML
	private PasswordField pf_pwd;
	@FXML
	private PasswordField pf_new_pwd;
	@FXML
	private PasswordField pf_confirm_pwd;
	@FXML
	private Button btn_cancel;
	@FXML
	private Button btn_confirm;
	ChangePwdStage stage;

	@FXML
	public void changePwd() {
		String originalPwd = pf_pwd.getText();
		String newPwd = pf_new_pwd.getText();
		String confirmPwd = pf_confirm_pwd.getText();
		String username = ((ChangePwdStage) pf_pwd.getScene().getWindow()).getUsername();
		if (newPwd.equals(confirmPwd)) {
			if (DBUtil.verifyUser(username, ParseUtil.getMD5(originalPwd))) {
				DBUtil.updateUserInfo(username, ParseUtil.getMD5(newPwd), "");
			} else {
				showPrompt(AlertType.ERROR, "输入的原始密码错误");
			}
		} else {
			showPrompt(AlertType.ERROR, "你输入的两次密码不一致");
		}
	}

	@FXML
	public void cancelOperation() {
		ChangePwdStage stage = (ChangePwdStage) btn_cancel.getScene().getWindow();
		stage.close();
	}

	public void showPrompt(AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setTitle("错误提示");
		alert.setContentText(message);
		alert.showAndWait();
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureStage(ChangePwdStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

}
