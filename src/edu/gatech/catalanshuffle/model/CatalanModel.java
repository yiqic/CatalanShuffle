package edu.gatech.catalanshuffle.model;

import java.util.Random;

import org.apache.commons.math3.util.CombinatoricsUtils;

public abstract class CatalanModel {
	
	protected final int n;
	protected boolean weighted;
	
	public static final Random rand = new Random();
	
	public CatalanModel(int n) {
		this(n, false);
	}
	
	public CatalanModel(int n, boolean weighted) {
		this.n = n;
		this.weighted = weighted;
	}
	
	public int getN() {
		return n;
	}
	
	public long catalanNumber() {
		return CombinatoricsUtils.binomialCoefficient(2 * n, n - 1) / n;
	}
	
	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}
	
	public void shuffle(int itr) {
		for (int i = 0; i < itr; i++) {
			shuffleOnce();
		}
	}
	
	public double averageDistance(double[] expected, long[] observed) {
		double res = 0;
		for (int i = 0; i < expected.length; i++) {
			res += Math.abs(expected[i] - observed[i]);
		}
		return res / expected.length;
	}
	
	public abstract void reset();
	public abstract void shuffleOnce();
	public abstract boolean checkCatalanProperty();
	public abstract double[] testUniformDistribution(int expectedNum, int shuffleItr, boolean report, DistanceMetric metric);
	
	public enum DistanceMetric {
		CHISQUARE, 
		AVERAGEDISTANCE
	}

}
