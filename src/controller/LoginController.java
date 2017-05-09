package controller;

import java.util.Optional;
import java.util.prefs.Preferences;

import javax.jws.soap.SOAPBinding.Use;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.User;
import util.DBUtil;
import util.ParseUtil;
import view.AlbumBrowserStage;
import view.HomeStage;
import view.LoginStage;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;

public class LoginController implements ControllerInitializable<LoginStage> {
	@FXML
	private TextField tf_username;
	@FXML
	private PasswordField tf_passsword;
	@FXML
	Hyperlink link_signUp;
	@FXML
	RadioButton rb_login_free;
	LoginStage stage;

	@FXML
	public void login() {
		String username = tf_username.getText();
		String password = tf_passsword.getText();
		String encodedPassword;
		encodedPassword = ParseUtil.getMD5(password);
		if (DBUtil.verifyUser(username, encodedPassword)) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Preferences preferences = Preferences.userNodeForPackage(LoginStage.class);
					preferences.put("lastUser", username);
					preferences.put(username, username);
					preferences.put(username + "pwd", encodedPassword);
					preferences.putBoolean("loginFree", rb_login_free.isSelected());
					preferences.put("origin", "");
					DBUtil.createAlbumsTable(username);
					tf_username.getScene().getWindow().hide();
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

	@FXML
	public void signUp() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("提示");
		dialog.setHeaderText("添加相片");
		// dialog.setGraphic(new
		// ImageView(this.getClass().getResource("/Pic/emptyCover.png").toString()));
		// Set the button types.
		ButtonType btn_confirm = new ButtonType("确认", ButtonData.OK_DONE);
		ButtonType btn_cancel = new ButtonType("取消", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(btn_confirm, btn_cancel);
		// Create the albumName and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("用户名");
		TextField password = new TextField();
		password.setPromptText("密码");
		TextField nickname = new TextField();
		nickname.setPromptText("用户昵称");
		grid.add(new Label("用户名:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("密码:"), 0, 1);
		grid.add(password, 1, 1);
		grid.add(new Label("昵称:"), 0, 2);
		grid.add(nickname, 1, 2);
		// Enable/Disable login button depending on whether a albumName was
		// entered.
		Node confirmButton = dialog.getDialogPane().lookupButton(btn_confirm);
		confirmButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});
		password.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});
		dialog.getDialogPane().setContent(grid);

		// Request focus on the albumName field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a albumName-password-pair when the login button
		// is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == btn_confirm) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});
		Optional<Pair<String, String>> result = dialog.showAndWait();
		result.ifPresent(albumNamePassword -> {
			User user = new User(username.getText(), ParseUtil.getMD5(password.getText()), nickname.getText(), 0, 0,
					FXCollections.observableArrayList());
			DBUtil.addUser(user);
		});
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		DBUtil.createUsersTable();
		configureBtnLoginFree();
	}

	@Override
	public void configureStage(LoginStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
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
							// new HomeStage(username).show();
							User user = DBUtil.getUser(username);
							new AlbumBrowserStage(user);
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
