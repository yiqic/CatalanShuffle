package edu.gatech.catalanshuffle.test;

import java.util.Arrays;

import edu.gatech.catalanshuffle.model.*;
import edu.gatech.catalanshuffle.model.DyckPath.InitType;

public class Main {

	public static void main(String[] args) {
		DyckPath model;
		double[] p = new double[12];
		for (int i = 0; i < 100; i++) {
			int itr = 1;
			model = new DyckPath(5, InitType.TOP);
			for (int j = 0; j < p.length; j++) {
				p[j] += model.testUniformDistribution(5, itr, false)[0];
				itr *= 2;
			}
			System.out.println("finish itr " + i);
			System.out.println(Arrays.toString(p));
		}
	}

}
