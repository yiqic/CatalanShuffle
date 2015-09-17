package edu.gatech.catalanshuffle.model;

public class DyckPath extends CatalanModel {
	
	private boolean[] cur;
	
	public DyckPath(int n, boolean randomInit) {
		super(n, randomInit);
		this.cur = new boolean[2 * n];
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
	
	public String toString() {
		if (n == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(cur[0]);
		for (int i = 1; i < cur.length; i++) {
			sb.append(" ").append(cur[i]);
		}
		return sb.toString();
	}
	
	private void swap(int index1, int index2) {
		boolean temp = cur[index1];
		cur[index1] = cur[index2];
		cur[index2] = temp;
	}
}
