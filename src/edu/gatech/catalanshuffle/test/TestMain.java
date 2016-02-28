package edu.gatech.catalanshuffle.test;

import java.util.List;

import edu.gatech.catalanshuffle.model.*;
import edu.gatech.catalanshuffle.model.CatalanModel.DistanceMetric;

public class TestMain {

	public static void main(String[] args) {
		DyckPath model = new DyckPath(20);
		double[][] p = new double[29][32];
		int numExp = 1;
//		double[] p2 = new double[32];
//		double expectedNum = 5;
		long trials = 10000;
		int stepSize = 8;
		int n = 5;
		for (int k = 0; k < p.length; k++) {
			model = new DyckPath(n);
			System.out.print(n);
			for (int i = 0; i < numExp; i++) {
				int itr = 0;
				for (int j = 0; j < p[0].length; j++) {
					List<double[][]> res = model.distributionExperiment(trials*n, itr);
					double[] pvall = model.testUniformDistribution(res, false, DistanceMetric.AVERAGEDISTANCE);
					p[k][j] += pvall[2];
					itr += stepSize;
				}
			}
			for (int j = 0; j < p[0].length; j++) {
				System.out.print("\t" + (p[k][j]/(numExp*trials*n)));
			}
			System.out.println();
			n += 1;
		}

//		model = new DyckPath(14);
//		for (int i = 0; i < 50; i++) {
//			int itr = 0;
////			System.out.println();
//			for (int j = 0; j < p.length; j++) {
////				p[j] += model.testUniformDistribution(5, itr, false)[0];
//				List<double[][]> res = model.distributionExperiment(trials, itr);
////				System.out.print(itr + "\t" + Arrays.toString(res.get(1)[0]));
//				double[] pvall = model.testUniformDistribution(res, false, DistanceMetric.AVERAGEDISTANCE);
////				double pval = model.testUniformDistribution(res, false)[2];
////				System.out.println("\t" + pval);
//				p2[j] += pvall[3];
////				p2[j] += pvall[0];
//				itr += stepSize;
//			}
////			System.out.println("finish itr " + i);
////			System.out.println(Arrays.toString(p));
//		}

		int itr = 0;
		n = 5;
		for (int j = 0; j < p[0].length; j++) {
			System.out.print(itr);
			for (int i = 0; i < p.length; i++) {
//				List<int[][]> res = model.distributionExperiment(expectedNum, itr);
				System.out.print("\t" + (p[i][j]/(numExp*trials*n)));
			}
			itr += stepSize;
			n++;
			System.out.println();
		}
	}

}
