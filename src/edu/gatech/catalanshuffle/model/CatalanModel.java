package edu.gatech.catalanshuffle.model;

import java.util.Random;

import org.apache.commons.math3.util.CombinatoricsUtils;

public abstract class CatalanModel {
	
	protected final int n;
	public static final Random rand = new Random();
	
	public CatalanModel(int n) {
		this(n, true);
	}
	
	public CatalanModel(int n, boolean randomInit) {
		this.n = n;
	}
	
	public long catalanNumber() {
		return CombinatoricsUtils.binomialCoefficient(2 * n, n - 1) / n;
	}
	
	public void shuffle(int itr) {
		for (int i = 0; i < itr; i++) {
			shuffleOnce();
		}
	}
	
	public abstract void shuffleOnce();
	public abstract boolean checkCatalanProperty();
	public abstract double testRandomness(int itr);

}
