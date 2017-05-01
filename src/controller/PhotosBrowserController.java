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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import model.Photo;
import util.DBUtil;
import util.DateUtil;
import util.FileChooserUtil;
import util.ParseUtil;
import view.ImageCell;
import view.PhotoBrowserStage;

public class PhotosBrowserController implements ControllerInitializable<PhotoBrowserStage> {
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
		// System.out.println("stage: " + stage);
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
			stage.getAlbum().setSize(stage.getAlbum().getSize() + size);
			stage.getAlbum().setPhotosNumber(stage.getAlbum().getPhotosNumber() + files.size());
			uris.addAll(urisToAdd);
			Platform.runLater(new Runnable() {
				public void run() {
					gv_photo.getItems().addAll(photos);
				}
			});

		}

	}
}
