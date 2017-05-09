package cluster;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class ClusterUtils {
	/**
	 * 特征词集合
	 */
	public static List<CentroidCluster<Point>> words;

	/**
	 * 获取当前图像的直方图表示
	 * 
	 * @param imagePoint
	 * @return
	 * @throws Exception
	 */
	public static void readClusterPoints() {
		words = new ArrayList<>();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream("album.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (objectInputStream != null) {

			try {
				Object object = null;
				while ((object = objectInputStream.readObject()) != null) {
					Point point = (Point) object;
					CentroidCluster<Point> centroidCluster = new CentroidCluster<>(point);
					words.add(centroidCluster);
				}

			} catch (EOFException e) {
				// TODO: handle exception
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			objectInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double[] distribute(ImagePoint imagePoint) throws Exception {
		List<Point> points = imagePoint.getPoints();
		int[] expression = new int[words.size()];
		for (int i = 0; i < points.size(); i++) {
			double minDistance = Double.MAX_VALUE;
			int minIndex = -1;
			for (int j = 0; j < words.size(); j++) {
				double distance = getDistance(words.get(j).getCenter().getPoint(), points.get(i).getPoint());
				if (distance < minDistance) {
					minDistance = distance;
					minIndex = j;
				}
			}
			if (minIndex != -1) {
				expression[minIndex]++;
			}
		}
		double ex[] = new double[words.size()];
		for (int i = 0; i < words.size(); i++) {
			ex[i] = (expression[i] + 0.0) / points.size();
		}
		// for(int i = 0; i < words.size(); i++){
		// System.out.print(ex[i] + ",");
		// }
		// System.out.println();
		return ex;
	}

	/**
	 * 获取欧拉距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static double getDistance(double[] a, double[] b) throws Exception {
		if (a == null || b == null) {
			throw new Exception("Point cann't be null");
		}
		if (a.length != b.length) {
			throw new Exception("获取两点间的距离出错,两个点的数量不一致");
		}
		double distance = 0.0;
		for (int i = 0; i < a.length; i++) {
			distance += Math.pow(a[i] - b[i], 2);
		}
		return Math.sqrt(distance);
	}

	public static double getSum(double[] key) {
		// TODO Auto-generated method stub
		double sum = 0.0;
		for (int i = 0; i < key.length; i++) {
			sum += key[i] * key[i];
		}
		return Math.sqrt(sum);
	}
}
