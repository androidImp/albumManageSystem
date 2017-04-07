package cluster;

import java.io.Serializable;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class Point implements Clusterable,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1724586718364177013L;
	public double[] features;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	String url;

	@Override
	public double[] getPoint() {
		// TODO Auto-generated method stub
		return features;
	}

	public Point(double features[]) {
		this.features = features;
	}

	public Point(float features[]) {
		double f[] = new double[features.length];
		for (int i = 0; i < f.length; i++) {
			f[i] = features[i];
		}
		this.features = f;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < features.length; i++) {
			builder.append(features[i] + ",");
		}
		builder.append("]");
		return builder.toString();
	}
}
