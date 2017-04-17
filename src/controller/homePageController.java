package controller;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Album;
import util.DBUtil;
import util.DataUtil;
import util.DateUtil;
import view.HomeStage;
import view.ImageTableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class homePageController {
	// @FXML
	// private ListView<Album> ls_album;
	@FXML
	private Button btn_add;
	@FXML
	private ImageView img_usr_icon;
	@FXML
	private Label ll_albumName;
	@FXML
	private TreeView<String> tv_menu;
	@FXML
	Label ll_name;
	@FXML
	TableView<Album> tv_album;
	@FXML
	TableColumn<Album, String> columnCover;
	@FXML
	TableColumn<Album, String> columnName;
	@FXML
	TableColumn<Album, String> columnDate;
	@FXML
	TableColumn<Album, String> columnSize;
	@FXML
	TableColumn<Album, String> columnProfile;
	private HomeStage homeStage;
	@FXML
	private void initialize() {
		columnCover.setCellValueFactory(new PropertyValueFactory<>("coverUri"));
		columnCover.setCellFactory(new Callback<TableColumn<Album, String>, TableCell<Album, String>>() {

			@Override
			public TableCell<Album, String> call(TableColumn<Album, String> param) {
				// TODO Auto-generated method stub
				return new ImageTableCell();
			}
		});
		columnName.setCellValueFactory(new PropertyValueFactory<>("albumName"));
		
		columnDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
		columnSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		columnSize.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Album, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Album, String> param) {
						// TODO Auto-generated method stub
						return new SimpleStringProperty(DataUtil.convertSizeToString(param.getValue().getSize()));
					}
				});

		columnProfile.setCellValueFactory(new PropertyValueFactory<>(" profile"));
	}

	@FXML
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
			Album album = new Album();
			album.setId(HomeStage.index++);
			album.setAlbumName(albumNamePassword.getKey());
			album.setAlbumProfile(albumNamePassword.getValue());
			album.setCreateDate(DateUtil.getFormatDate());
			album.setPhotosNumber(0);
			album.setSize(0.0);
			album.setCoverUri("Pic/emptyCover.png");
			album.setPhotosUri(FXCollections.observableArrayList());
			// ls_album.getItems().add(album);
			// String name = ((HomeStage)
			// ls_album.getScene().getWindow()).getName();
			// DBUtil.saveAlbums(ls_album.getItems(), name);
			tv_album.getItems().add(album);
			String name = ((HomeStage) tv_album.getScene().getWindow()).getName();
			DBUtil.saveAlbums(tv_album.getItems(), name);
		});
	}
	 public void setMainApp(HomeStage homeStage) {
	        this.homeStage = homeStage;
	        // Add observable list data to the table
	        tv_album.setItems(homeStage.getAlbumData());
	    }
}
