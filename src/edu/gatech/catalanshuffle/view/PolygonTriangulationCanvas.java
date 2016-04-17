package edu.gatech.catalanshuffle.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import edu.gatech.catalanshuffle.model.DyckPath;
import edu.gatech.catalanshuffle.model.Triangulation;
import edu.gatech.catalanshuffle.model.Triangulation.TestStatistics;
import edu.gatech.catalanshuffle.model.Triangulation.TreeNode;

public class PolygonTriangulationCanvas extends CatalanModelCanvas {
	
	private Triangulation model;
	private double[] x;
	private double[] y;
	
	public PolygonTriangulationCanvas(int n, double width, double height, double weightedLambda) {
		super(n, width, height);
		this.model = new Triangulation(n, false, weightedLambda);
		// initialize vertices
		this.x = new double[n+2];
		this.y = new double[n+2];
		setVertices(30);
		draw();
	}
	
	public void setVertices(double padding) {
		double wOffset = getWidth() / 2;
        double hOffset = getHeight() / 2;
		double radius = Math.min(wOffset, hOffset) - padding;
		
		double theta = 2 * Math.PI / x.length;
		for (int i = 0; i < x.length; i++) {
		    x[i] = radius * Math.cos(theta * i) + wOffset;
		    y[i] = radius * Math.sin(theta * i) + hOffset;
		}
	}
	
	public void tick() {
		model.shuffleOnce();
		draw();
	}
	
	public void reset() {
		model.reset();
		draw();
	}

	private void draw() {
		int centralDiagonal = Triangulation.testStatisticsValue
				(TestStatistics.LongestDiagonal, model.getRoot(), null);
		
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
//        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.strokeLine(x[0], y[0], x[x.length-1], y[y.length-1]);
        drawLine(gc, model.getRoot(), 0, x.length-1, centralDiagonal);
	}
	
	private void drawLine(GraphicsContext gc, TreeNode node, int start, int end, int centralDiagonal) {
		gc.strokeLine(x[start], y[start],x[end], y[end]);
		if (end - start == centralDiagonal || end - start == x.length - centralDiagonal) {
	        gc.setStroke(Color.RED);
	        gc.setLineWidth(5);
	        gc.strokeLine(x[start], y[start],x[end], y[end]);
	        gc.setStroke(Color.BLACK);
	        gc.setLineWidth(2);
		}
		if (!node.isLeaf()) {
			int splitPoint = start + Triangulation.leafSize(node.getLeft());
			drawLine(gc, node.getLeft(), start, splitPoint, centralDiagonal);
			drawLine(gc, node.getRight(), splitPoint, end, centralDiagonal);
		}
	}
}
