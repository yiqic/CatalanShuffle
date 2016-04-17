package edu.gatech.catalanshuffle.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class DyckPath extends CatalanModel {
	
	protected Boolean[] cur;
	private InitType initType;
	// if chain is lazy, 50% of the time chain does not move (used for coupling)
	private boolean lazyChain;
	private Map<Integer, Long>[] dist;
	
	public static final InitType DEFAULT_INIT_TYPE = InitType.TOP;
	
	public DyckPath(int n) {
		this(n, DEFAULT_INIT_TYPE);
	}
	
	public DyckPath(int n, InitType initType) {
		this(n, initType, false, true, 1);
	}
	
	public DyckPath(int n, InitType initType, boolean lazyChain, boolean initDist, double weightedLambda) {
		super(n, weightedLambda);
		this.initType = initType;
		this.lazyChain = lazyChain;
		reset();
		if (initDist) {
			loadTestStatisticsDist();
		}
	}
	
	public void reset() {
		this.cur = new Boolean[2 * n];
		if (initType == InitType.RANDOM) {
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
		else if (initType == InitType.TOP) {
			for (int i = 0; i < n; i++) {
				cur[i] = true;
				cur[n + i] = false;
			}
		}
		else {
			for (int i = 0; i < n; i++) {
				cur[2 * i] = true;
				cur[2 * i + 1] = false;
			}
		}
	}
	
//	public void loadTestStatisticsDist() {
//		List<Boolean[]> all = generateAllCatalanStructures(n);
//		dist = new Map[TestStatistics.values().length];
//		for (TestStatistics ts : TestStatistics.values()) {
//			dist[ts.ordinal()] = new HashMap<>();
//			for (Boolean[] b : all) {
//				int value = testStatisticsValue(ts, b);
//				if (dist[ts.ordinal()].containsKey(value)) {
//					dist[ts.ordinal()].put(value, dist[ts.ordinal()].get(value)+1);
//				}
//				else {
//					dist[ts.ordinal()].put(value, 1);
//				}
//			}
//		}
//	}
	
	public void loadTestStatisticsDist() {
		dist = new Map[TestStatistics.values().length];
		for (TestStatistics ts : TestStatistics.values()) {
			dist[ts.ordinal()] = new HashMap<>();
		}
//		generateAllCatalanStructures(n, dist);
		generateHalfCatalanStructures(n, dist);
	}	
	
	public Boolean[] getModel() {
		return cur;
	}
	
	public void shuffleOnce() {
		shuffleOnce(rand.nextInt(2 * n), rand.nextInt(2 * n));
	}
	
	public void shuffleOnce(int index1, int index2) {
		shuffleOnce(index1, index2, rand.nextBoolean());
	}
	
	/**
	 * 
	 * @param index1
	 * @param index2
	 * @param firstIndexValue In coupling, chain is lazy, and we manually set whether the first
	 * index should go up or down in both chains, in order to make the two chains closer
	 */
	public void shuffleOnce(int index1, int index2, boolean firstIndexValue) {
		if (index1 > index2) {
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		if (!lazyChain) {
			firstIndexValue = cur[index2];
		}
		if (cur[index1] != cur[index2]) {
			double a1 = Math.pow(weightedLambda, testStatisticsValue(TestStatistics.AREA, cur));
			cur[index1] = firstIndexValue;
			cur[index2] = !firstIndexValue;
			int newArea = testStatisticsValue(TestStatistics.AREA, cur);
			double a2 = Math.pow(weightedLambda, newArea);
			// Metropolis–Hastings algorithm in order to make stationary distribution weighted by area underneath
			double acceptProb = Math.min(1, a2/a1);
			if (newArea < 0 || rand.nextDouble() > acceptProb) {
				cur[index1] = !firstIndexValue;
				cur[index2] = firstIndexValue;
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
	
	public List<double[][]> distributionExperiment(long trials, int shuffleItr) {
		return distributionExperiment(((double)trials) / catalanNumber(), shuffleItr);
	}
	
	public List<double[][]> distributionExperiment(double expectedNum, int shuffleItr) {
		long cNumber = catalanNumber();
		long trials = (long) (cNumber * expectedNum);
		List<double[][]> dis = new ArrayList<>();
		
		Boolean[] start = cur.clone();
		Map<List<Boolean>, Integer> freq = new HashMap<>();
		Map<Integer, Integer>[] tsFreq = new Map[TestStatistics.values().length];
		for (TestStatistics ts : TestStatistics.values()) {
			tsFreq[ts.ordinal()] = new HashMap<>();
		}
		for (long i = 0; i < trials; i++) {
			shuffle(shuffleItr);
			List<Boolean> res = Arrays.asList(cur);
			if (freq.containsKey(res)) {
				freq.put(res, freq.get(res) + 1);
			}
			else {
				freq.put(res, 1);
			}
			for (TestStatistics ts : TestStatistics.values()) {
				int value = testStatisticsValue(ts, cur);
				if (tsFreq[ts.ordinal()].containsKey(value)) {
					tsFreq[ts.ordinal()].put(value, tsFreq[ts.ordinal()].get(value)+1);
				}
				else {
					tsFreq[ts.ordinal()].put(value, 1);
				}
			}
			cur = start.clone();
		}
		
//		double[][] data = new double[2][(int)cNumber];
//		int idx = 0;
//		for (Integer l : freq.values()) {
//			data[0][idx] = l;
//			idx++;
//		}
//		
//		Arrays.fill(data[1], expectedNum);
//		dis.add(data);
		
		for (TestStatistics ts : TestStatistics.values()) {
			double[][] data = new double[2][dist[ts.ordinal()].size()];
			int i = 0;
			for (int val : dist[ts.ordinal()].keySet()) {
				data[0][i] = tsFreq[ts.ordinal()].containsKey(val) ? tsFreq[ts.ordinal()].get(val) : 0;
				data[1][i] = dist[ts.ordinal()].get(val) * expectedNum;
				i++;
			}
			dis.add(data);
		}
		return dis;
	}
	
	public double[] testUniformDistribution(List<double[][]> dis, boolean report, DistanceMetric metric) {
		double[] res = new double[dis.size()];
		for (int i = 0; i < dis.size(); i++) {
			if (report) {
				if (i == 0) {
					System.out.println("catalan number: " + catalanNumber());
				}
				else {
					System.out.println("test statistics: " + TestStatistics.values()[i-1].toString());
				}
				System.out.println("observed: ");
				System.out.println(Arrays.toString(dis.get(i)[0]));
				System.out.println("expected: ");
				System.out.println(Arrays.toString(dis.get(i)[1]));
			}
			long[] observed = new long[dis.get(i)[0].length];
			double[] expected = new double[dis.get(i)[0].length];
			for (int j = 0; j < dis.get(i)[0].length; j++) {
				observed[j] = (long) dis.get(i)[0][j];
				expected[j] = dis.get(i)[1][j];
			}
			res[i] = metric == DistanceMetric.CHISQUARE ? new ChiSquareTest().chiSquareTest(expected, observed) : 
				averageDistance(expected, observed);
		}
		return res;
	}

	public double[] testUniformDistribution(int expectedNum, int shuffleItr, boolean report, DistanceMetric metric) {
		List<double[][]> dis = distributionExperiment((double)expectedNum, shuffleItr);
		return testUniformDistribution(dis, report, metric);
	}
	
	public String toString() {
		return Arrays.toString(cur);
	}
	
	public static List<Boolean[]> generateAllCatalanStructures(int n) {
        List<List<List<Boolean>>> dp = new ArrayList<>();
        List<List<Boolean>> list = new ArrayList<>();
        list.add(new ArrayList<Boolean>());
        dp.add(list);
        for (int i = 1; i <= n; i++) {
            list = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                for (List<Boolean> x : dp.get(j)) {
                    for (List<Boolean> y : dp.get(i - j - 1)) {
                    	List<Boolean> g = new ArrayList<>();
                    	g.add(true);
                    	g.addAll(x);
                    	g.add(false);
                    	g.addAll(y);
                        list.add(g);
                    }
                }
            }
            dp.add(list);
            
        }
        List<Boolean[]> res = new ArrayList<>();
        for (List<Boolean> b : dp.get(n)) {
        	Boolean[] ba = new Boolean[b.size()];
        	b.toArray(ba);
        	res.add(ba);
        }
        return res;
    }
	
	public static void generateHalfCatalanStructures(int n, Map<Integer, Long>[] dist) {
		generateHalfCatalanStructures(n, dist, new ArrayList<>(), 0, 0);
		for (int key : dist[2].keySet()) {
			dist[2].put(key, dist[2].get(key) * dist[2].get(key));
		}
	}
	
	public static void generateHalfCatalanStructures(int n, Map<Integer, Long>[] dist, List<Boolean> cur, int numTrue, int height) {
		if (cur.size() == n) {
			int value = height;
			if (dist[2].containsKey(value)) {
				dist[2].put(value, dist[2].get(value)+1);
			}
			else {
				dist[2].put(value, (long) 1);
			}
		}
		else {
			cur.add(true);
			numTrue++;
			height++;
			generateHalfCatalanStructures(n, dist, cur, numTrue, height);
			cur.remove(cur.size() - 1);
			numTrue--;
			height--;
			if (height > 0) {
				cur.add(false);
				height--;
				generateHalfCatalanStructures(n, dist, cur, numTrue, height);
				cur.remove(cur.size() - 1);
				height++;
			}
		}
	}
	
	
	public static void generateAllCatalanStructures(int n, Map<Integer, Long>[] dist) {
		generateAllCatalanStructures(n, dist, new ArrayList<>(), 0, 0);
	}
	
	public static void generateAllCatalanStructures(int n, Map<Integer, Long>[] dist, List<Boolean> cur, int numTrue, int height) {
		if (cur.size() == n * 2) {
			Boolean[] res = new Boolean[n * 2];
			cur.toArray(res);
			for (TestStatistics ts : TestStatistics.values()) {
				int value = testStatisticsValue(ts, res);
				if (dist[ts.ordinal()].containsKey(value)) {
					dist[ts.ordinal()].put(value, dist[ts.ordinal()].get(value)+1);
				}
				else {
					dist[ts.ordinal()].put(value, (long) 1);
				}
			}
		}
		else {
			if (numTrue < n) {
				cur.add(true);
				numTrue++;
				height++;
				generateAllCatalanStructures(n, dist, cur, numTrue, height);
				cur.remove(cur.size() - 1);
				numTrue--;
				height--;
			}
			if (height > 0) {
				cur.add(false);
				height--;
				generateAllCatalanStructures(n, dist, cur, numTrue, height);
				cur.remove(cur.size() - 1);
				height++;
			}
		}
	}
	
	public int testStatisticsValue(TestStatistics ts) {
		return testStatisticsValue(ts, cur);
	}
	
	private static int testStatisticsValue(TestStatistics ts, Boolean[] input) {
		int height = 0;
		switch (ts) {
			case PEEK: 
				int res = 0;
				int temp = 0;
				for (Boolean b : input) {
					temp = b ? temp + 1 : temp - 1;
					if (temp > res) {
						res = temp;
					}
				}
				return res;
			case PEEKMINUSAVG: 
				int peek = 0;
				int sum = 0;
				temp = 0;
				for (Boolean b : input) {
					temp = b ? temp + 1 : temp - 1;
					if (temp > peek) {
						peek = temp;
					}
					sum += temp;
				}
				return peek - sum / input.length;
			case MIDDLEHEIGHT: 
				for (int i = 0; i < input.length / 2; i++) {
					height += input[i] ? 1 : -1;
				}
				return height;
			case AREA: 
				int area = input.length / 2;
				for (Boolean b : input) {
					if (height < 0) {
						return -1;
					}
					if (b) {
						area += height;
						height++;
					}
					else {
						height--;
						area += height;
					}
				}
				return area;
			default: 
				return 0;
		}
	}
	
//	private void swap(int index1, int index2) {
//		boolean temp = cur[index1];
//		cur[index1] = cur[index2];
//		cur[index2] = temp;
//	}
	
	public enum TestStatistics {
		PEEK, 
		PEEKMINUSAVG, 
		MIDDLEHEIGHT,
		AREA
	}
	
	public enum InitType {
		TOP, 
		BUTTOM, 
		RANDOM
	}
}
