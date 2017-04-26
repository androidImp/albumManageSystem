package view;

import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.DBUtil;

public class LoginStage extends Stage {
	private RadioButton rb_login_free;
	private Parent root;
	private TextField tf_username;

	public LoginStage() {
		// TODO Auto-generated method stub
		try {
			root = FXMLLoader.load(getClass().getResource("login.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		setScene(scene);
		scene.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
		DBUtil.createUsersTable();
		initViews();
		configureBtnLoginFree();
		setResizable(false);
		show();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		rb_login_free = (RadioButton) root.lookup("#rb_login_free");
		tf_username = (TextField) root.lookup("#tf_username");
		if (rb_login_free != null) {
			Preferences preferences = Preferences.userNodeForPackage(LoginStage.class);
			rb_login_free.setSelected(preferences.getBoolean("loginFree", false));
		}

	}

	private void configureBtnLoginFree() {
		// TODO Auto-generated method stub
		if (rb_login_free.isSelected()) {
			Preferences preferences = Preferences.userNodeForPackage(LoginStage.class);
			String lastUser = preferences.get("lastUser", "");
			String username = preferences.get(lastUser, "");
			String password = preferences.get(lastUser + "pwd", "");
			String origin = preferences.get("origin", "");
			if (!origin.equals("HomeStage")) {
				if (DBUtil.verifyUser(username, password)) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							DBUtil.createAlbumsTable(username);
							tf_username.getScene().getWindow().hide();
							preferences.put("origin", "");
							new HomeStage(username).show();

						}
					});
				} else {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("警告");
					alert.setContentText("用户不存在或密码错误");
					alert.show();
				}
			}
		}
	}

}
