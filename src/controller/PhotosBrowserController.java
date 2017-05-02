package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.controlsfx.control.GridView;

import com.alibaba.simpleimage.analyze.sift.SIFT;
import com.alibaba.simpleimage.analyze.sift.render.RenderImage;

import cluster.ClusterUtils;
import cluster.ImagePoint;
import cluster.KDSearchUtil;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.ImageCell;
import model.Photo;
import util.DBUtil;
import util.DateUtil;
import util.FileChooserUtil;
import util.ParseUtil;
import view.PhotoBrowserStage;

public class PhotosBrowserController implements ControllerInitializable<PhotoBrowserStage> {
	private static final int BRIGHTER = 1;
	private static final int DARKER = 2;
	private static final int GREY = 3;
	private static final int INVERT = 4;
	private static final int BATCH_OF_IMAGE_RENDER = 10;
	@FXML
	private Label ll_title;
	@FXML
	private Label ll_date;
	@FXML
	private Button btn_add;
	@FXML
	private TextField tf_search;
	@FXML
	private GridView<Photo> gv_photo;
	@FXML
	private HBox hb_scan;
	@FXML
	private ImageView img_scan;
	@FXML
	private PhotoBrowserStage stage;
	private FileChooser fileChooser;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		configurePhotoList();
		fileChooser = new FileChooser();
		configureScanOption();
		// System.out.println("stage: " + stage);
	}

	// TO DO 相片过大时,提供一个小的视图用于用户调整当前浏览的部分
	private void configureScanOption() {
		// TODO Auto-generated method stub
		ContextMenu menu = new ContextMenu();
		MenuItem rotateClockWiseItem = new MenuItem("顺时针旋转");
		MenuItem rotateAntiClockWiseItem = new MenuItem("逆时针旋转");
		MenuItem brighterItem = new MenuItem("调高色彩度");
		MenuItem darkerItem = new MenuItem("调低色彩度");
		MenuItem greyItem = new MenuItem("灰度处理");
		MenuItem invertItem = new MenuItem("颜色反转");
		MenuItem recoverItem = new MenuItem("还原");
		configuRotateClockWiseItem(rotateClockWiseItem);
		configuRotateAntiClockWiseItem(rotateAntiClockWiseItem);
		configureBrighterItem(brighterItem);
		configureDarkerItem(darkerItem);
		configureGreyItem(greyItem);
		configureInvertItem(invertItem);
		configureRecoverItem(recoverItem);
		menu.getItems().addAll(rotateClockWiseItem, rotateAntiClockWiseItem, brighterItem, darkerItem, greyItem,
				invertItem, recoverItem);
		img_scan.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				MouseEvent mouseEvent = (MouseEvent) event;
				if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
					if (!menu.isShowing()) {
						menu.show(img_scan, mouseEvent.getScreenX(), mouseEvent.getScreenY());

					}
				} else if (mouseEvent.getClickCount() >= 2) {
					gv_photo.setVisible(true);
				}

			}
		});

	}

	private void configureRecoverItem(MenuItem recoverItem) {
		// TODO Auto-generated method stub
		recoverItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				img_scan.setImage(new Image(gv_photo.getItems().get((int) gv_photo.getUserData()).getUri()));
			}
		});

	}

	private void configureInvertItem(MenuItem invertItem) {
		// TODO Auto-generated method stub
		invertItem.setOnAction(event -> processImage(INVERT));
	}

	private void configureGreyItem(MenuItem greyItem) {
		// TODO Auto-generated method stub
		greyItem.setOnAction(event -> processImage(GREY));
	}

	private void configureDarkerItem(MenuItem darkerItem) {
		// TODO Auto-generated method stub
		darkerItem.setOnAction(event -> processImage(DARKER));
	}

	private void configureBrighterItem(MenuItem brighterItem) {
		// TODO Auto-generated method stub
		brighterItem.setOnAction(event -> processImage(BRIGHTER));
	}

	private void configuRotateAntiClockWiseItem(MenuItem rotateAntiClockWiseItem) {
		// TODO Auto-generated method stub
		rotateAntiClockWiseItem.setOnAction(event -> img_scan.setRotate((img_scan.getRotate() + 270) % 360));
	}

	private void configuRotateClockWiseItem(MenuItem rotateClockWiseItem) {
		// TODO Auto-generated method stub
		rotateClockWiseItem.setOnAction(event -> img_scan.setRotate((img_scan.getRotate() + 90) % 360));
	}

	@Override
	public void configureStage(PhotoBrowserStage stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

	public void configurePhotoList() {
		gv_photo.setCellWidth(85);
		gv_photo.setCellHeight(85);
		gv_photo.setPadding(new Insets(10));
		gv_photo.setHorizontalCellSpacing(5);
		gv_photo.setVerticalCellSpacing(5);
		gv_photo.setCellFactory(param -> new ImageCell());
	}

	@FXML
	public void addPhoto() {
		FileChooserUtil.configureFileChooser(fileChooser);
		List<File> filesList = fileChooser.showOpenMultipleDialog(stage);
		importPicFromFiles(filesList);
	}

	/**
	 * 从文件列表中导入图片,并将其存储到数据库中
	 * 
	 * @param filesList
	 *            文件列表
	 */
	private void importPicFromFiles(List<File> filesList) {
		// TO DO 通过线程池的执行情况来添加加载情况的显示以便增加用户体验
		System.out.println("stage: " + stage);
		if (filesList != null) {
			fileChooser.setInitialFileName(filesList.get(0).getAbsolutePath());
			ThreadPoolExecutor executor = new ThreadPoolExecutor(8, Runtime.getRuntime().availableProcessors() * 4, 2,
					TimeUnit.MINUTES, new LinkedBlockingQueue<>());
			int size = filesList.size() / BATCH_OF_IMAGE_RENDER + 1;
			for (int i = 0; i < size; i++) {
				int startIndex = 10 * i;
				int endIndex = 10 * (i + 1);
				if (endIndex > filesList.size()) {
					endIndex = filesList.size();
				}
				if (stage == null) {
					stage = (PhotoBrowserStage) gv_photo.getScene().getWindow();
				}
				RenderImageRunnable runnable = new RenderImageRunnable(filesList.subList(startIndex, endIndex));
				executor.execute(runnable);

			}

		}
	}

	public class RenderImageRunnable implements Runnable {
		private List<File> files;

		public RenderImageRunnable(List<File> files) {
			// TODO Auto-generated constructor stub
			this.files = files;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ObservableList<String> uris = stage.getAlbum().getPhotosUri();
			List<Photo> photos = new ArrayList<>();
			List<String> urisToAdd = new ArrayList<>();
			List<String> expressionToAdd = new ArrayList<>();
			SIFT sift = new SIFT();
			double size = 0;
			for (File file : files) {
				String path = file.getAbsoluteFile().toURI().toString();
				if (uris.contains(path)) {
					// TO DO deal with existing photo
				} else {
					int index = file.getName().lastIndexOf(".");
					size += file.length();
					Photo photo = new Photo(stage.getAlbum().getId(), stage.getAlbum().getAlbumName(), path,
							DateUtil.getFormatDate(file.lastModified()), file.getName().substring(0, index),
							ParseUtil.getMD5(file), file.length(), "");
					photo.setModified(true);
					photos.add(photo);
					urisToAdd.add(path);
					BufferedImage image = null;
					try {
						image = ImageIO.read(file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					RenderImage ri = new RenderImage(image);
					sift.detectFeatures(ri.toPixelFloatArray(null));
					ImagePoint imagePoint = new ImagePoint(sift.getGlobalFeaturePoints());
					try {

						double[] express = ClusterUtils.distribute(imagePoint);
						String expression = ParseUtil.doubleArrayToExpression(express);
						expressionToAdd.add(expression);
						// DBUtil.addExpression(username.get(), album.getId(),
						// photo.getMd5(), photo.getUri(), expression);
						KDSearchUtil.insertNode(KDSearchUtil.constructKeyWithAlbumId(express, stage.getAlbum().getId()),
								photo);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			DBUtil.addExpressionBatch(stage.getUsername(), stage.getAlbum().getId(), photos, expressionToAdd);
			DBUtil.savePhotosData(photos, stage.getUsername());
			double s = size;
			Platform.runLater(new Runnable() {
				public void run() {
					stage.getAlbum().addAlbumSize(s);
					stage.getAlbum().addPhotoNumber(files.size());
					stage.getUser().addPhotoNumber(files.size());
				}
			});

			// stage.getAlbum().setSize(stage.getAlbum().getSize() + size);
			// stage.getAlbum().setPhotosNumber(stage.getAlbum().getPhotosNumber()
			// + files.size());
			uris.addAll(urisToAdd);
			Platform.runLater(new Runnable() {
				public void run() {
					gv_photo.getItems().addAll(photos);

				}
			});

		}

	}

	@FXML
	public void previous() {
		ObservableList<Photo> photos = gv_photo.getItems();
		int index = (int) gv_photo.getUserData();
		index = (index + 1) % photos.size();
		final int current = index;
		gv_photo.setUserData(index);
		playAnimation(photos, current);
	}

	@FXML
	public void next() {
		ObservableList<Photo> photos = gv_photo.getItems();
		int index = (int) gv_photo.getUserData();
		index = (index - 1 + photos.size()) % photos.size();
		final int current = index;
		gv_photo.setUserData(index);
		playAnimation(photos, current);

	}

	private void playAnimation(ObservableList<Photo> photos, final int current) {
		RotateTransition rotator = createRotator(img_scan, 0, 90);
		rotator.play();
		rotator.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				img_scan.setImage(new Image(photos.get(current).getUri()));
				createRotator(img_scan, 90, 180).play();
			}
		});
	}

	private void processImage(int type) {
		Image image = img_scan.getImage();
		PixelReader reader = image.getPixelReader();
		WritableImage writableImage = new WritableImage((int) (image.getWidth()), (int) (image.getHeight()));
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = reader.getColor(x, y);
				color = processPixel(type, color);
				pixelWriter.setColor(x, y, color);
			}
		img_scan.setImage(writableImage);

	}

	private Color processPixel(int type, Color color) {
		switch (type) {
		case 1:
			color = color.brighter();
			break;
		case 2:
			color = color.darker();
			break;
		case 3:
			color = color.grayscale();
			break;
		case 4:
			color = color.invert();
			break;
		default:
			break;
		}
		return color;
	}

	private RotateTransition createRotator(Node card, double from, double to) {
		RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
		rotator.setAxis(Rotate.Y_AXIS);
		rotator.setFromAngle(from);
		rotator.setToAngle(to);
		rotator.setInterpolator(Interpolator.LINEAR);
		return rotator;
	}
}
