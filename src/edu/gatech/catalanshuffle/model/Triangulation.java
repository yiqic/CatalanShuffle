package edu.gatech.catalanshuffle.model;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import edu.gatech.catalanshuffle.model.CatalanModel.DistanceMetric;

public class Triangulation extends CatalanModel {
	
	private TreeNode root;
	private TreeNode[] nodeMap;
	
	private Map<Integer, Integer>[] dist;
	
	public Triangulation(int n) {
		this(n, true, 1);
	}
	
	public Triangulation(int n, boolean initDist, double weightedLambda) {
		super(n, weightedLambda);
		reset();
		if (initDist) {
			loadTestStatisticsDist();
		}
	}
	
	public void loadTestStatisticsDist() {
		List<TreeNode> all = generateAllCatalanStructures(n);
		System.out.println(all.size());
		dist = new Map[TestStatistics.values().length];
		TestStatistics ts = TestStatistics.LongestEdge;
		dist[ts.ordinal()] = new HashMap<>();
		int uniform = (int)catalanNumber() / (n+2);
		for (int i = 0; i < n+2; i++) {
			dist[ts.ordinal()].put(i, uniform);
		}
		ts = TestStatistics.LongestDiagonal;
		dist[ts.ordinal()] = new HashMap<>();
		for (TreeNode b : all) {
			int value = testStatisticsValue(ts, b, null);
			if (dist[ts.ordinal()].containsKey(value)) {
				dist[ts.ordinal()].put(value, dist[ts.ordinal()].get(value)+1);
			}
			else {
				dist[ts.ordinal()].put(value, 1);
			}
		}
	}
	
	public TreeNode getRoot() {
		return root;
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
	
	private void zig(TreeNode target) {
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
	
	private void zag(TreeNode target) {
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
	
	public void shuffleOnce() {
		TreeNode target = nodeMap[rand.nextInt(n)];
		boolean leftChild = rand.nextBoolean();
		double diagonal = Math.pow(weightedLambda, testStatisticsValue(TestStatistics.LongestDiagonal, root, null));
		if (leftChild) {
			if (!target.left.leaf) {
				zig(target);
				double newDiagonal = Math.pow(weightedLambda, testStatisticsValue(TestStatistics.LongestDiagonal, root, null));
				double acceptProb = Math.min(1, newDiagonal/diagonal);
				if (rand.nextDouble() > acceptProb) {
					zag(target.parent);
				}
			}
			else {
				shuffleOnce();
			}
		}
		else {
			if (!target.right.leaf) {
				zag(target);
				double newDiagonal = Math.pow(weightedLambda, testStatisticsValue(TestStatistics.LongestDiagonal, root, null));
				double acceptProb = Math.min(1, newDiagonal/diagonal);
				if (rand.nextDouble() > acceptProb) {
					zig(target.parent);
				}
			}
			else {
				shuffleOnce();
			}
		}
	}
	
	public boolean checkCatalanProperty() {
		return true;
	}
	
	public List<double[][]> distributionExperiment(double expectedNum, int shuffleItr) {
		long cNumber = catalanNumber();
		long trials = (long) (cNumber * expectedNum);
		List<double[][]> dis = new ArrayList<>();
		
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
				int value = testStatisticsValue(ts, root, nodeMap);
				if (tsFreq[ts.ordinal()].containsKey(value)) {
					tsFreq[ts.ordinal()].put(value, tsFreq[ts.ordinal()].get(value)+1);
				}
				else {
					tsFreq[ts.ordinal()].put(value, 1);
				}
			}
			reset();
		}
		
		double[][] data = new double[2][(int)cNumber];
		int idx = 0;
		for (Integer l : freq.values()) {
			data[0][idx] = l;
			idx++;
		}
		
		Arrays.fill(data[1], expectedNum);
		dis.add(data);
		
		for (TestStatistics ts : TestStatistics.values()) {
			data = new double[2][dist[ts.ordinal()].size()];
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
			res[i] = expected.length <= 1 ? 1 : 
				(metric == DistanceMetric.CHISQUARE ? new ChiSquareTest().chiSquareTest(expected, observed) : 
					averageDistance(expected, observed));
		}
		return res;
	}
	
	public double[] testUniformDistribution(int expectedNum, int shuffleItr, boolean report, DistanceMetric metric) {
		List<double[][]> dis = distributionExperiment(expectedNum, shuffleItr);
		return testUniformDistribution(dis, report, metric);
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
	
	public static List<TreeNode> generateAllCatalanStructures(int n) {
		List<TreeNode>[] dp = new List[n+1];
		dp[0] = new ArrayList<>();
		dp[0].add(null);
		for (int i = 1; i <= n; i++) {
			dp[i] = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				for (TreeNode l : dp[j]) {
					for (TreeNode r : dp[i-1-j]) {
						TreeNode ro = new TreeNode(0, false);
						ro.left = copyTree(l);
						ro.right = copyTree(r);
						if (ro.left != null) {
							ro.left.parent = ro;
						}
						if (ro.right != null) {
							ro.right.parent = ro;
						}
						dp[i].add(ro);
					}
				}
			}
		}
		return dp[n];
	}
	
	public static TreeNode copyTree(TreeNode ori) {
		if (ori == null) {
			return null;
		}
		TreeNode res = new TreeNode(ori.val, ori.leaf);
		res.left = copyTree(ori.left);
		res.right = copyTree(ori.right);
		if (res.left != null) {
			res.left.parent = res;
		}
		if (res.right != null) {
			res.right.parent = res;
		}
		return res;
	}
	
	public static int testStatisticsValue(TestStatistics ts, TreeNode root, TreeNode[] input) {
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
			case LongestDiagonal: 
				int n = numNodes(root);
				return calculateDiagonal(root, n, (double)n / 2);
			default: 
				return 0;
		}
	}
	
	private static int calculateDiagonal(TreeNode input, int n, double threshold) {
		int maxValue = 0;
		if (input == null) {
			return 0;
		}
		int left = numNodes(input.left);
		if (maxValue < left && left <= threshold) {
			maxValue = left;
		}
		else if (maxValue < (n-left) && (n-left) <= threshold) {
			maxValue = n-left;
		}
		int right = numNodes(input.right);
		if (maxValue < right && right <= threshold) {
			maxValue = right;
		}
		else if (maxValue < (n-right) && (n-right) <= threshold) {
			maxValue = n-right;
		}
		int parent = n-1-left-right;
		if (maxValue < parent && parent <= threshold) {
			maxValue = parent;
		}
		else if (maxValue < (n-parent) && (n-parent) <= threshold) {
			maxValue = n-parent;
		}
		return Math.max(Math.max(maxValue+1, calculateDiagonal(input.left, n, threshold)),calculateDiagonal(input.right, n, threshold));
	}
	
	public static int leafSize(TreeNode input) {
		return !input.leaf ? leafSize(input.left)+leafSize(input.right) : 1;
	}
	
	private static int numNodes(TreeNode input) {
		return input != null && !input.leaf ? numNodes(input.left)+numNodes(input.right)+1 : 0;
	}
	
	public enum TestStatistics {
		LongestEdge, 
		LongestDiagonal
	}
	
	public static class TreeNode {
		boolean leaf;
		int val;
		TreeNode parent;
		TreeNode left;
		TreeNode right;
		
		public TreeNode(int val, boolean leaf) {
			this.val = val;
			this.leaf = leaf;
		}
		
		public boolean isLeaf() {
			return leaf;
		}
		
		public TreeNode getLeft() {
			return left;
		}
		
		public TreeNode getRight() {
			return right;
		}
	}

}
