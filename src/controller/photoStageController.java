package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.StageStyle;
import model.Photo;
import util.DBUtil;
import util.LogUtil;

public class photoStageController {
	@FXML
	private Button btn_add;
	@FXML
	private ListView<Photo> lv_photo;

}
