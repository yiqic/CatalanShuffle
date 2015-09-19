package edu.gatech.catalanshuffle.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class DyckPath extends CatalanModel {
	
	private Boolean[] cur;
	
	public DyckPath(int n) {
		this(n, true);
	}
	
	public DyckPath(int n, boolean randomInit) {
		super(n);
		this.cur = new Boolean[2 * n];
		if (randomInit) {
			int posi = 0;
			int diff = 0;
			for (int i = 0; i < 2 * n; i++) {
				if (diff == 0) {
					cur[i] = true;
				}
				else if (posi == n) {
					cur[i] = false;
				}
				else {
					cur[i] = rand.nextBoolean();
				}
				if (cur[i]) {
					posi++;
					diff++;
				}
				else {
					diff--;
				}
			}
		}
		else {
			for (int i = 0; i < n; i++) {
				cur[i] = true;
			}
		}
	}
	
	public void shuffleOnce() {
		int index1, index2;
		boolean satisfy = false;
		while (!satisfy) {
			index1 = rand.nextInt(2 * n);
			index2 = rand.nextInt(2 * n);
			swap(index1, index2);
			if (!(satisfy = checkCatalanProperty())) {
				swap(index1, index2);
			}
		}
	}
	
	public boolean checkCatalanProperty() {
		int posi = 0;
		int nega = 0;
		for (boolean i : cur) {
			if (i) {
				posi++;
			}
			else {
				nega++;
			}
			if (posi > n || posi < nega) {
				return false;
			}
		}
		return true;
	}
	
	public double testRandomness(int itr) {
		Map<List<Boolean>, Integer> freq = new HashMap<>();
		long cNumber = catalanNumber();
		for (long i = 0; i < cNumber * itr; i++) {
			shuffleOnce();
			List<Boolean> res = Arrays.asList(cur);
			if (freq.containsKey(res)) {
				freq.put(res, freq.get(res) + 1);
			}
			else {
				freq.put(res, 1);
			}
		}
		long[] observed = new long[(int)cNumber];
		int idx = 0;
		for (Integer l : freq.values()) {
			observed[idx] = l;
		}
		double[] expected = new double[(int)cNumber];
		Arrays.fill(expected, itr);
		return new ChiSquareTest().chiSquareTest(expected, observed);
	}
	
	public String toString() {
		return Arrays.toString(cur);
	}
	
	private void swap(int index1, int index2) {
		boolean temp = cur[index1];
		cur[index1] = cur[index2];
		cur[index2] = temp;
	}
}
