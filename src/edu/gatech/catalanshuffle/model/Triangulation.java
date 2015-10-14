package edu.gatech.catalanshuffle.model;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import edu.gatech.catalanshuffle.model.DyckPath.TestStatistics;

public class Triangulation extends CatalanModel {
	
	private TreeNode root;
	private TreeNode[] nodeMap;
	
	public Triangulation(int n) {
		super(n);
		reset();
	}
	
	public void reset() {
		this.nodeMap = new TreeNode[n];
		if (n >= 1) {
			this.root = new TreeNode(0, false);
			root.parent = new TreeNode(0, true);
			root.parent.left = root;
			TreeNode cur = root;
			nodeMap[0] = cur;
			for (int i = 1; i < n; i++) {
				cur.left = new TreeNode(i, false);
				cur.right = new TreeNode(i, true);
				cur.left.parent = cur;
				cur.right.parent = cur;
				cur = cur.left;
				nodeMap[i] = cur;
			}
			cur.left = new TreeNode(n, true);
			cur.right = new TreeNode(n+1, true);
			cur.left.parent = cur;
			cur.right.parent = cur;
		}
	}
	
	public void shuffleOnce() {
		TreeNode target = nodeMap[rand.nextInt(n)];
		boolean leftChild = rand.nextBoolean();
		if (leftChild) {
			if (!target.left.leaf) {
				TreeNode parent = target.parent;
				TreeNode child = target.left;
				TreeNode gchild = child.right;
				if (parent.leaf) {
					root = child;
					parent.parent = child;
				}
				else if (parent.left == target) {
					parent.left = child;
				}
				else {
					parent.right = child;
				}
				target.parent = child;
				target.left = gchild;
				child.parent = parent;
				child.right = target;
				gchild.parent = target;
			}
		}
		else {
			if (!target.right.leaf) {
				TreeNode parent = target.parent;
				TreeNode child = target.right;
				TreeNode gchild = child.left;
				if (parent.leaf) {
					root = child;
					parent.parent = child;
				}
				else if (parent.left == target) {
					parent.left = child;
				}
				else {
					parent.right = child;
				}
				target.parent = child;
				target.right = gchild;
				child.parent = parent;
				child.left = target;
				gchild.parent = target;
			}
		}
	}
	
	public boolean checkCatalanProperty() {
		return true;
	}
	
	public List<int[][]> distributionExperiment(int expectedNum, int shuffleItr) {
		long cNumber = catalanNumber();
		long trials = cNumber * expectedNum;
		List<int[][]> dis = new ArrayList<>();
		
		reset();
		Map<List<Integer>, Integer> freq = new HashMap<>();
		Map<Integer, Integer>[] tsFreq = new Map[TestStatistics.values().length];
		for (TestStatistics ts : TestStatistics.values()) {
			tsFreq[ts.ordinal()] = new HashMap<>();
		}
		for (long i = 0; i < trials; i++) {
			shuffle(shuffleItr);
			List<Integer> res = serialize();
			if (freq.containsKey(res)) {
				freq.put(res, freq.get(res) + 1);
			}
			else {
				freq.put(res, 1);
			}
			for (TestStatistics ts : TestStatistics.values()) {
				int value = testStatisticsValue(ts, nodeMap);
				if (tsFreq[ts.ordinal()].containsKey(value)) {
					tsFreq[ts.ordinal()].put(value, tsFreq[ts.ordinal()].get(value)+1);
				}
				else {
					tsFreq[ts.ordinal()].put(value, 1);
				}
			}
			reset();
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
			data = new int[2][n+2];
			for (int i = 0; i < n+2; i++) {
				data[0][i] = tsFreq[ts.ordinal()].containsKey(i) ? tsFreq[ts.ordinal()].get(i) : 0;
				data[1][i] = (int)cNumber / (n+2) * expectedNum;
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
	
	public List<Integer> serialize() {
		List<Integer> res = preorder(root);
		res.addAll(inorder(root));
		return res;
	}
	
	public List<Integer> preorder(TreeNode t) {
		List<Integer> res = new ArrayList<>();
		if (t != null) {
			res.add(t.val);
			res.addAll(preorder(t.left));
			res.addAll(preorder(t.right));
		}
		return res; 
	}
	
	public List<Integer> inorder(TreeNode t) {
		List<Integer> res = new ArrayList<>();
		if (t != null) {
			res.addAll(preorder(t.left));
			res.add(t.val);
			res.addAll(preorder(t.right));
		}
		return res; 
	}
	
	private static int testStatisticsValue(TestStatistics ts, TreeNode[] input) {
		switch (ts) {
			case LongestEdge: 
				double minValue = input.length;
				int minIndex = -1;
				double targetValue = ((double)input.length + 1) / 2;
				List<Integer> per = new ArrayList<>();
				for (int i = 0; i < input.length; i++) {
					per.add(i);
				}
				Collections.shuffle(per);
				for (int i : per) {
					int index = -1;
					int neighbors = 0;
					if (!input[i].parent.leaf) {
						neighbors++;
					}
					else {
						index = input[i].parent.val;
					}
					if (!input[i].left.leaf) {
						neighbors++;
					}
					else {
						index = input[i].left.val;
					}
					if (!input[i].right.leaf) {
						neighbors++;
					}
					else {
						index = input[i].right.val;
					}
					if (neighbors == 2) {
						double val = !input[i].left.leaf ? Math.abs(leafSize(input[i].left) - targetValue) : 
							Math.abs(leafSize(input[i].right) - targetValue);
						if (val < minValue) {
							minValue = val;
							minIndex = index;
						}
					}
				}
				return minIndex >= 0 ? minIndex : rand.nextInt(input.length+2);
			default: 
				return 0;
		}
	}
	
	private static int leafSize(TreeNode input) {
		return !input.leaf ? leafSize(input.left)+leafSize(input.right) : 1;
	}
	
	public enum TestStatistics {
		LongestEdge
	}
	
	private class TreeNode {
		boolean leaf;
		int val;
		TreeNode parent;
		TreeNode left;
		TreeNode right;
		
		public TreeNode(int val, boolean leaf) {
			this.val = val;
			this.leaf = leaf;
		}
	}

}
