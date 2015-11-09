package edu.gatech.catalanshuffle.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class DyckPath extends CatalanModel {
	
	private Boolean[] cur;
	private Map<Integer, Integer>[] dist;
	
	public static final InitType DEFAULT_INIT_TYPE = InitType.TOP;
	
	public DyckPath(int n) {
		this(n, DEFAULT_INIT_TYPE);
	}
	
	public DyckPath(int n, InitType initType) {
		super(n);
		reset(initType);
		loadTestStatisticsDist();
	}
	
	public void reset() {
		reset(DEFAULT_INIT_TYPE);
	}
	
	public void reset(InitType initType) {
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
	
	public void loadTestStatisticsDist() {
		List<Boolean[]> all = generateAllCatalanStructures(n);
		dist = new Map[TestStatistics.values().length];
		for (TestStatistics ts : TestStatistics.values()) {
			dist[ts.ordinal()] = new HashMap<>();
			for (Boolean[] b : all) {
				int value = testStatisticsValue(ts, b);
				if (dist[ts.ordinal()].containsKey(value)) {
					dist[ts.ordinal()].put(value, dist[ts.ordinal()].get(value)+1);
				}
				else {
					dist[ts.ordinal()].put(value, 1);
				}
			}
		}
	}
	
	public Boolean[] getModel() {
		return cur;
	}
	
	public void shuffleOnce() {
		int index1, index2;
		boolean satisfy = false;
		while (!satisfy) {
			index1 = rand.nextInt(2 * n);
			index2 = rand.nextInt(2 * n);
			if (index1 > index2) {
				int tmp = index1;
				index1 = index2;
				index2 = tmp;
			}
			swap(index1, index2);
			if (!(satisfy = (cur[index1] || (!cur[index2]) || checkCatalanProperty()))) {
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
	
	public List<int[][]> distributionExperiment(int expectedNum, int shuffleItr) {
		long cNumber = catalanNumber();
		long trials = cNumber * expectedNum;
		List<int[][]> dis = new ArrayList<>();
		
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
		
		int[][] data = new int[2][(int)cNumber];
		int idx = 0;
		for (Integer l : freq.values()) {
			data[0][idx] = l;
			idx++;
		}
		
		Arrays.fill(data[1], expectedNum);
		dis.add(data);
		
		for (TestStatistics ts : TestStatistics.values()) {
			data = new int[2][dist[ts.ordinal()].size()];
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
	
	public double[] testUniformDistribution(List<int[][]> dis, boolean report) {
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
				observed[j] = dis.get(i)[0][j];
				expected[j] = dis.get(i)[1][j];
			}
			res[i] = new ChiSquareTest().chiSquareTest(expected, observed);
		}
		return res;
	}
	
	public double[] testUniformDistribution(int expectedNum, int shuffleItr, boolean report) {
		List<int[][]> dis = distributionExperiment(expectedNum, shuffleItr);
		return testUniformDistribution(dis, report);
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
	
	public int testStatisticsValue(TestStatistics ts) {
		return testStatisticsValue(ts, cur);
	}
	
	private static int testStatisticsValue(TestStatistics ts, Boolean[] input) {
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
			default: 
				return 0;
		}
	}
	
	private void swap(int index1, int index2) {
		boolean temp = cur[index1];
		cur[index1] = cur[index2];
		cur[index2] = temp;
	}
	
	public enum TestStatistics {
		PEEK, 
		PEEKMINUSAVG
	}
	
	public enum InitType {
		TOP, 
		BUTTOM, 
		RANDOM
	}
}
