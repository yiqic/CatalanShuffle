package edu.gatech.catalanshuffle.test;

import java.util.Arrays;
import java.util.List;

import edu.gatech.catalanshuffle.model.*;
import edu.gatech.catalanshuffle.model.DyckPath.InitType;

public class Main {

	public static void main(String[] args) {
		DyckPath model = new DyckPath(8);
		double[] p = new double[32];
		double[] p2 = new double[32];
		int expectedNum = 5;
		int stepSize = 16;
		System.out.println(Arrays.toString(model.distributionExperiment(expectedNum, 0).get(2)[1]));
		for (int i = 0; i < 50; i++) {
			int itr = 0;
//			System.out.println();
			for (int j = 0; j < p.length; j++) {
//				p[j] += model.testUniformDistribution(5, itr, false)[0];
				List<int[][]> res = model.distributionExperiment(expectedNum,itr);
//				System.out.print(itr + "\t" + Arrays.toString(res.get(1)[0]));
				double[] pvall = model.testUniformDistribution(res, false);
//				double pval = model.testUniformDistribution(res, false)[2];
//				System.out.println("\t" + pval);
				p[j] += pvall[1];
				p2[j] += pvall[2];
				itr += stepSize;
			}
//			System.out.println("finish itr " + i);
//			System.out.println(Arrays.toString(p));
		}
		int itr = 0;
		for (int j = 0; j < p.length; j++) {
			List<int[][]> res = model.distributionExperiment(expectedNum, itr);
			System.out.println(itr + "\t" + Arrays.toString(res.get(2)[0]) + "\t" + (p[j]/50) + "\t" + (p2[j]/50));
			itr += stepSize;
		}
	}

}
