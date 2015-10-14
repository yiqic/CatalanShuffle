package edu.gatech.catalanshuffle.test;

import java.util.Arrays;

import edu.gatech.catalanshuffle.model.*;
import edu.gatech.catalanshuffle.model.DyckPath.InitType;

public class Main {

	public static void main(String[] args) {
		Triangulation model;
		double[] p = new double[12];
		for (int i = 0; i < 1; i++) {
			int itr = 1;
			model = new Triangulation(8);
			System.out.println(Arrays.toString(model.distributionExperiment(5, itr).get(1)[1]));
			System.out.println();
			for (int j = 0; j < p.length; j++) {
//				p[j] += model.testUniformDistribution(5, itr, false)[0];
				System.out.println(itr + "\t" + Arrays.toString(model.distributionExperiment(5, itr).get(1)[0]));
				itr *= 2;
			}
//			System.out.println("finish itr " + i);
//			System.out.println(Arrays.toString(p));
		}
	}

}
