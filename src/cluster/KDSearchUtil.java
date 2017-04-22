package cluster;

import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Photo;
import util.DBUtil;

public class KDSearchUtil {
	private static final int DIMENSIONS_OF_KDTREE = 100;
	private static KDTree<String> kdTree = new KDTree<>(DIMENSIONS_OF_KDTREE + 1);

	public static void configureKDTree(String username) {
		List<Point> points = DBUtil.getExpressions(username, DIMENSIONS_OF_KDTREE);
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			try {

				double[] p = point.getPoint();
				int len = p.length;
				double[] key = new double[len + 1];
				for (int j = 0; j < len; j++) {
					key[j] = p[j];
				}
				// 增加一个维度用以解决不同相册中相同图片相同键的问题;
				key[len] = (point.getId() + 0.0) / 10000;
				// System.out.println(key[len]);
				kdTree.insert(key, point.getUrl());
			} catch (KeySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyDuplicateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void queryNearestPic(String username, Photo photo) {
		double[] key = DBUtil.queryExpression(username, DIMENSIONS_OF_KDTREE, photo.getId(), photo.getMd5());
		if (key != null) {
			try {
				double[] query = new double[key.length + 1];
				for (int i = 0; i < key.length; i++) {
					query[i] = key[i];
				}
				query[key.length] = (photo.getId() + 0.0) / 1000;
				List<String> uris = kdTree.nearest(query, 10);
				Stage stage = new Stage();
				VBox root = new VBox();
				Scene scene = new Scene(root, 400, 400);
				stage.setScene(scene);
				ListView<ImageView> listView = new ListView<ImageView>();
				root.getChildren().add(listView);
				listView.getItems().add(new ImageView(photo.getUri()));
				for (String string : uris) {
					ImageView imageView = new ImageView(string);
					listView.getItems().add(imageView);
				}
				stage.show();
			} catch (KeySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
