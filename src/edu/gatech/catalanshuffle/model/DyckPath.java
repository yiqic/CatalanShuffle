package edu.gatech.catalanshuffle.model;

import java.util.Random;

public class DyckPath {
	
	private boolean[] cur;
	public static Random rand = new Random();
	
	public DyckPath(int n) {
		this(n, true);
	}
	
	public DyckPath(int n, boolean randomInit) {
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
}
