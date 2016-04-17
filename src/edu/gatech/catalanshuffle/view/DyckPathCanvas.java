package edu.gatech.catalanshuffle.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import edu.gatech.catalanshuffle.model.DyckPath;

public class DyckPathCanvas extends CatalanModelCanvas {
	
	private DyckPath model;
	
	public DyckPathCanvas(int n, double width, double height, double weightedLambda) {
		super(n, width, height);
		this.model = new DyckPath(n);
		draw();
	}
	
	public void tick() {
		model.shuffleOnce();
		draw();
	}
	
	public void setWeightedLambda(double weightedLambda) {
		model.setWeightedLambda(weightedLambda);
	}
	
	public void reset() {
		model.reset();
		draw();
	}
	
	private void draw() {
		double width = getWidth();
        double height = getHeight();
        int length = 2 * model.getN();
        double unitWidth = width / length;
        double unitHeight = height / model.getN();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        
        double curHeight = height;
        for (int i = 0; i < length; i++) {
        	double nextHeight = curHeight + (model.getModel()[i] ? -unitHeight : unitHeight);
        	gc.strokeLine(i*unitWidth, curHeight, (i+1)*unitWidth, nextHeight);
        	curHeight = nextHeight;
        }
	}
}
