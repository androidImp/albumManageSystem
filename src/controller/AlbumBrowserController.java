package controller;

import java.util.Optional;

import org.controlsfx.control.GridView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.Album;
import model.AlbumInfoCell;
import util.DBUtil;
import util.DateUtil;
import util.DialogUtil;
import view.HomeStage;
import view.ShowAlbumStage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class AlbumBrowserController implements ControllerInitializable<ShowAlbumStage> {
	@FXML
	private ImageView img_userIcon;
	@FXML
	private Label ll_userName;
	@FXML
	private Label ll_photoNumber;
	@FXML
	private Label ll_albumNumber;
	private ShowAlbumStage stage;
	@FXML
	private GridView<Album> gv_album;
	@FXML
	Button btn_add;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	private void configureGridView() {
		gv_album.setCellFactory(param -> new AlbumInfoCell());
		gv_album.setItems(DBUtil.getAlbums(stage.getUsername()));
	}

	@Override
	public void configureStage(ShowAlbumStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

	private void configureData() {
		DBUtil.createPhotosTable(stage.getUsername());
		DBUtil.createExpressionsTable(stage.getUsername());
	}

	public void configure() {
		configureData();
		configureGridView();
		configureTitle();
	}

	private void configureTitle() {
		// TODO Auto-generated method stub
		img_userIcon.setImage(new Image("/Pic/pic.jpeg"));
	}

	public void addAlbum() {
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
		TextField albumName = new TextField();
		albumName.setPromptText("albumName");
		TextField profile = new TextField();
		albumName.setPromptText("profile");

		grid.add(new Label("albumName:"), 0, 0);
		grid.add(albumName, 1, 0);
		grid.add(new Label("profile:"), 0, 1);
		grid.add(profile, 1, 1);

		// Enable/Disable login button depending on whether a albumName was
		// entered.
		Node confirmButton = dialog.getDialogPane().lookupButton(btn_confirm);
		confirmButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		albumName.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the albumName field by default.
		Platform.runLater(() -> albumName.requestFocus());

		// Convert the result to a albumName-password-pair when the login button
		// is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == btn_confirm) {
				return new Pair<>(albumName.getText(), profile.getText());
			}
			return null;
		});
		Optional<Pair<String, String>> result = dialog.showAndWait();
		result.ifPresent(albumNamePassword -> {
			if (DBUtil.queryAlbum(stage.getUsername(), albumNamePassword.getKey())) {
				Album album = new Album();
				album.setId(HomeStage.index++);
				album.setAlbumName(albumNamePassword.getKey());
				album.setAlbumProfile(albumNamePassword.getValue());
				album.setCreateDate(DateUtil.getFormatDate());
				album.setPhotosNumber(0);
				album.setSize(0.0);
				album.setCoverUri("Pic/emptyCover.png");
				album.setPhotosUri(FXCollections.observableArrayList());
				gv_album.getItems().add(album);
				String name = stage.getUsername();
				album.saveAlbum(name);
				// DBUtil.saveAlbumInfo(album, name);

			} else {
				DialogUtil.showDialog(AlertType.CONFIRMATION, "已存在同名相册,请更改你想要的相册名后再试", "添加相册情况");
			}

		});
	}

}
