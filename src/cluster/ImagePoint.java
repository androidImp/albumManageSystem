package cluster;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.simpleimage.analyze.sift.scale.FeaturePoint;

/**
 * 存储一幅图像的所有特征向量,并且存储其直方图表示;
 * 
 * @author niuniumei
 *
 */
public class ImagePoint {

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	String url;
	public double[] getExpression() {
		return expression;
	}

	public void setExpression(double[] expression) {
		this.expression = expression;
	}

	/**
	 * 特征点的直方图表示
	 */
	private double[] expression;

	/**
	 * 图像的特征值集合
	 */
	private List<Point> points;

	public ImagePoint(List<FeaturePoint> fp) {
		points = new ArrayList<>();
		for (FeaturePoint featurePoint : fp) {
			points.add(new Point(featurePoint.getFeatures()));
		}
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("直方图表示: ");
		for (int i = 0; i < expression.length; i++) {
			builder.append(expression[i]);
		}
		builder.append("\n");
		for (int i = 0; i < points.size(); i++) {
			builder.append(points.get(i));
			builder.append("\n");
		}
		return builder.toString();
	}
}
