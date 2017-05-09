package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.controlsfx.control.GridView;

import com.alibaba.simpleimage.analyze.sift.SIFT;
import com.alibaba.simpleimage.analyze.sift.render.RenderImage;

import cluster.ClusterUtils;
import cluster.ImagePoint;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.ImageCell;
import model.Photo;
import model.User;
import search.KDSearchUtil;
import util.DBUtil;
import util.DateUtil;
import util.FileChooserUtil;
import util.LoadImageThreadPoolExecutor;
import util.ParseUtil;

public class PhotoBrowserStage extends Stage {
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	private static final int BATCH_OF_IMAGE_RENDER = 10;
	private User user;
	private Album album;
	GridView<Photo> gv_photo;
	Parent root = null;
	private Label ll_title;
	private Label ll_date;
	private Button btn_add;
	private TextField tf_search;
	private FileChooser fileChooser;
	private HBox hb_scan;
	private ImageView img_scan;

	public PhotoBrowserStage(Album album, User user) {
		// TODO Auto-generated constructor stub
		setAlbum(album);
		setUser(user);
		initView();
		lookUpViewById();
		configurePhotoList();
		configureTitle();
		configureButtonAdd();
		configureCloseProperty();
		show();
		// calculateAccuracy();
	}

	private void calculateAccuracy() {
		// TODO Auto-generated method stub
		int success = 0, fail = 0;
		ObservableList<Photo> observableList = gv_photo.getItems();
		for (int i = 0; i < observableList.size(); i++) {
			Photo photo = observableList.get(i);
			List<Photo> photos = KDSearchUtil.queryNearestPhoto(getUsername(), observableList.get(i));
			if (photos != null) {
				for (int j = 0; j < photos.size(); j++) {
					if (photos.get(j).getAlbumName().equals(photo.getAlbumName())) {
						success++;
					} else {
						fail++;
					}
				}
			}
		}
		System.out.println("album name:" + album.getAlbumName() + " total: " + observableList.size() * 10);
		System.out.println("success: " + success + "  fail: " + fail);
	}

	private void initView() {
		// TODO Auto-generated method stub
		try {
			root = FXMLLoader.load(getClass().getResource("photoBrowser.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		setScene(scene);
		scene.setCamera(new PerspectiveCamera());
		fileChooser = new FileChooser();
	}

	private void configureCloseProperty() {
		// TODO Auto-generated method stub
		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				// DBUtil.savePhotosData(gv_photo.getItems(), getUsername());
				DBUtil.updateAlbumInfo(getUsername(), album);
				// Platform.exit();
			}
		});
	}

	private void configureTitle() {
		// TODO Auto-generated method stub
		ll_title.setText(album.getAlbumName());
		ll_date.setText(album.getCreateDate());

	}

	// To do background;
	@SuppressWarnings("unchecked")
	public void lookUpViewById() {
		gv_photo = (GridView<Photo>) root.lookup("#gv_photo");
		ll_title = (Label) root.lookup("#ll_title");
		ll_date = (Label) root.lookup("#ll_date");
		btn_add = (Button) root.lookup("#btn_add");
		tf_search = (TextField) root.lookup("#tf_search");
		hb_scan = (HBox) root.lookup("#hb_scan");
		img_scan = (ImageView) root.lookup("#img_scan");
		hb_scan.visibleProperty().bind(gv_photo.visibleProperty().not());
		hb_scan.visibleProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if (newValue) {
					img_scan.setImage(new Image(gv_photo.getItems().get((int) gv_photo.getUserData()).getUri()));
				}
			}
		});
		Image image = new Image("/Pic/bg.jpeg");
		img_scan.setImage(image);

	}

	public void configurePhotoList() {
		gv_photo.setCellWidth(85);
		gv_photo.setCellHeight(85);
		gv_photo.setPadding(new Insets(10));
		gv_photo.setHorizontalCellSpacing(5);
		gv_photo.setVerticalCellSpacing(5);
		new Thread(new RenderImageTask()).start();
		gv_photo.setCellFactory(param -> new ImageCell());

	}

	public String getUsername() {
		return getUser().getUsername();
	}

	class RenderImageTask extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			// TODO Auto-generated method stub
			ObservableList<Photo> photos = DBUtil.getPhotosByAlbum(album.getId(), getUsername());
			gv_photo.setItems(photos);
			// System.out.println("大小: " + gv_photo.getItems().size());
			return null;
		}
	}

	class RenderExpressionTask extends Task<Void> {
		List<File> filesList;
		int startIndex;

		public RenderExpressionTask(List<File> filesList, int startIndex) {
			// TODO Auto-generated constructor stub
			this.filesList = filesList;
			this.startIndex = startIndex;
		}

		@Override
		protected Void call() {
			// TODO Auto-generated method stub
			SIFT sift = new SIFT();
			ObservableList<Photo> photos = gv_photo.getItems();
			for (int i = startIndex; i < photos.size(); i++) {
				File file = new File(photos.get(i).getUri().substring(5));
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

					DBUtil.addExpression(getUsername(), album.getId(), ParseUtil.getMD5(file),
							file.getAbsoluteFile().toURI().toString(), expression);
					KDSearchUtil.insertNode(KDSearchUtil.constructKeyWithAlbumId(express, album.getId()),
							photos.get(startIndex + i));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	private void configureButtonAdd() {
		// TODO Auto-generated method stub
		btn_add.setOnAction((final ActionEvent e) -> {
			FileChooserUtil.configureFileChooser(fileChooser);
			List<File> filesList = fileChooser.showOpenMultipleDialog(PhotoBrowserStage.this);
			importPicFromFiles(filesList);
			// sift 特征直方图存储;

		});
	}

	/**
	 * 从文件列表中导入图片,并将其存储到数据库中
	 * 
	 * @param filesList
	 *            文件列表
	 */
	private void importPicFromFiles(List<File> filesList) {
		// TO DO 通过线程池的执行情况来添加加载情况的显示以便增加用户体验
		if (filesList != null) {
			fileChooser.setInitialFileName(filesList.get(0).getAbsolutePath());
			// ExecutorService threadPool = Executors.newCachedThreadPool();
			// ThreadPoolExecutor executor = new ThreadPoolExecutor(8,
			// Runtime.getRuntime().availableProcessors() * 4, 2,
			// TimeUnit.MINUTES, new LinkedBlockingQueue<>());
			LoadImageThreadPoolExecutor executor = new LoadImageThreadPoolExecutor(8,
					Runtime.getRuntime().availableProcessors() * 4, 2, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
					this);
			int size = filesList.size() / BATCH_OF_IMAGE_RENDER + 1;
			for (int i = 0; i < size; i++) {
				int startIndex = 10 * i;
				int endIndex = 10 * (i + 1);
				if (endIndex > filesList.size()) {
					endIndex = filesList.size();
				}
				RenderImageRunnable runnable = new RenderImageRunnable(filesList.subList(startIndex, endIndex), album);
				executor.execute(runnable);
			}
		}
	}

	public void deletePhoto(int index) {
		Photo photo = gv_photo.getItems().get(index);
		if (photo != null) {
			album.getPhotosUri().remove(index);
			gv_photo.getItems().remove(index);
			DBUtil.deletePhoto(photo.getMd5(), photo.getId(), getUsername());
			DBUtil.deleteExpressionOfPhoto(getUsername(), photo.getId(), photo.getMd5());
			// 数据库中存储的文件路径为 file:(path),所以这里去掉了前5个字符:
			File file = new File(photo.getUri().substring(5, photo.getUri().length()));
			album.setSize(album.getSize() - file.length());
			double[] key = DBUtil.getExpression(getUsername(), photo.getMd5(), photo.getId());
			KDSearchUtil.deleteNode(KDSearchUtil.constructKeyWithAlbumId(key, photo.getId()));
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	class RenderImageRunnable implements Runnable {
		private List<File> files;
		private Album album;

		public RenderImageRunnable(List<File> files, Album album) {
			// TODO Auto-generated constructor stub
			this.files = files;
			this.album = album;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ObservableList<String> uris = album.getPhotosUri();
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
					Photo photo = new Photo(album.getId(), album.getAlbumName(), path,
							DateUtil.getFormatDate(file.lastModified()), file.getName().substring(0, index),
							ParseUtil.getMD5(file), file.length(), "");
					photo.setModified(true);
					photos.add(photo);
					urisToAdd.add(path);
					insertNode(expressionToAdd, sift, file, photo);
				}
			}
			DBUtil.addExpressionBatch(getUsername(), album.getId(), photos, expressionToAdd);
			DBUtil.savePhotosData(photos, getUsername());
			DBUtil.updateAlbumInfo(getUsername(), album);
			// album.setSize(album.getSize() + size);
			// album.setPhotosNumber(album.getPhotosNumber() + files.size());
			double s = size;
			Platform.runLater(new Runnable() {
				public void run() {
					getAlbum().addAlbumSize(s);
					getAlbum().addPhotoNumber(files.size());
					getUser().addPhotoNumber(files.size());
					uris.addAll(urisToAdd);
					gv_photo.getItems().addAll(photos);
				}
			});
		}

		private void insertNode(List<String> expressionToAdd, SIFT sift, File file, Photo photo) {
			ImagePoint imagePoint = extracteImagePoint(sift, file);
			try {

				double[] express = ClusterUtils.distribute(imagePoint);
				String expression = ParseUtil.doubleArrayToExpression(express);
				expressionToAdd.add(expression);
				// DBUtil.addExpression(getUsername(), album.getId(),
				// photo.getMd5(), photo.getUri(), expression);
				KDSearchUtil.insertNode(KDSearchUtil.constructKeyWithAlbumId(express, album.getId()), photo);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private ImagePoint extracteImagePoint(SIFT sift, File file) {
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
			return imagePoint;
		}

	}
}