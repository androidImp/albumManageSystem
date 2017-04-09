package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import util.DBUtil;
import util.DataUtil;
import view.ChangePwdStage;

public class changePwdController {
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

	@FXML
	public void changePwd() {
		String originalPwd = pf_pwd.getText();
		String newPwd = pf_new_pwd.getText();
		String confirmPwd = pf_confirm_pwd.getText();
		String username = ((ChangePwdStage) pf_pwd.getScene().getWindow()).getUsername();
		if (newPwd.equals(confirmPwd)) {
			if (DBUtil.verifyUser(username, DataUtil.getMD5(originalPwd))) {
				DBUtil.updateUserInfo(username, DataUtil.getMD5(newPwd), "");
			} else {

			}
		} else {
			showPrompt(AlertType.ERROR, "你输入的两次密码不一致");
		}
	}

	@FXML
	public void cancelOperation() {

	}

	public void showPrompt(AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setTitle("错误提示");
		alert.setContentText(message);
		alert.showAndWait();
	}

}