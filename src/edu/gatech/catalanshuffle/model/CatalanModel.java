package edu.gatech.catalanshuffle.model;

import java.util.Random;

import org.apache.commons.math3.util.CombinatoricsUtils;

public abstract class CatalanModel {
	
	protected final int n;
	// whether the stationary distribution should be weighted by some factor or uniform
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

	public abstract void reset();
	public abstract void shuffleOnce();
	public abstract boolean checkCatalanProperty();
	/**
	 * Using statistical test, calculate the distance between sample distribution and uniform distribution
	 * after some iterations of shuffles. 
	 * 
	 * @param expectedNum number of samples to use in the statistical test should be catalanNumber(n) * expectedNum
	 * @param shuffleItr results are recorded after shuffleItr steps of random walk
	 * @param report whether to print result to console
	 * @param metric which metric to use in calculating the distance (l1 distance or chi square)
	 * @return length should be len(TestStatistics)+1, the first index records distance between sample distribution 
	 * and uniform distribution, and the other indices records distances for all test statistics. 
	 */
	public abstract double[] testUniformDistribution(int expectedNum, int shuffleItr, boolean report, DistanceMetric metric);
	
	public static double averageDistance(double[] expected, long[] observed) {
		double res = 0;
		for (int i = 0; i < expected.length; i++) {
			res += Math.abs(expected[i] - observed[i]);
		}
		return res / expected.length;
	}
	
	public enum DistanceMetric {
		CHISQUARE, 
		AVERAGEDISTANCE
	}

}
