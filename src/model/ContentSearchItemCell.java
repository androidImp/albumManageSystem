package model;

import cluster.ClusterUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.DBUtil;

/**
 * 1.由于数据量比较小,所以在每次计算图片与给定图片的相似度时,需要从数据库中读取其对应的直方图表示并计算相似度. 2.可以修改在给
 * {@link ListView} 设置 userdata 时设置计算结果,然后每次更新读取即可. 3.不可以在Cell 中一次计算并存储,因为Cell
 * 的复用会导致相似度存储的错位,所以需要每次计算.
 */
public class ContentSearchItemCell extends ListCell<Photo> {

	private double resemblance;

	public ContentSearchItemCell() {
		// TODO Auto-generated constructor stub
		super();

		resemblance = 0;
	}

	@Override
	protected void updateItem(Photo item, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			HBox hBox = new HBox(10);
			VBox vBox_img = new VBox();
			vBox_img.setAlignment(Pos.CENTER);
			ImageView imageView = new ImageView(item.getUri());
			imageView.setFitWidth(100);
			imageView.setPreserveRatio(true);
			vBox_img.getChildren().addAll(imageView);
			VBox vBox_info = new VBox(10);
			hBox.getChildren().addAll(vBox_img, vBox_info);
			Label ll_name = new Label("相片名: " + item.getName());
			// TO DO 改成相册名而不是相册 ID;
			Label ll_album_name = new Label("相册名: " + item.getAlbumName());
			Label ll_resemblance = new Label();

			double[] key = (double[]) getListView().getUserData();
			// TO DO "123" 改为当前用户名
			double expression[] = DBUtil.getExpression("123", getItem().getMd5(), getItem().getId());

			try {
				resemblance = (1 - ClusterUtils.getDistance(key, expression)/ ClusterUtils.getSum(key)) * 100;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ll_resemblance.setText("相似度: " + String.format("%.3f%%", resemblance));
			vBox_info.getChildren().addAll(ll_name, ll_album_name, ll_resemblance);
			setGraphic(hBox);

		}

	}
}
