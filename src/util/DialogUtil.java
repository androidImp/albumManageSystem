package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DialogUtil {
	public static void showDialog(AlertType type,String info,String title){
		Alert alert = new Alert(type);
		alert.setContentText(info);
		alert.setHeaderText(title);
		alert.setTitle(title);
		alert.show();
	}
}
