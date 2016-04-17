package edu.gatech.catalanshuffle.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import edu.gatech.catalanshuffle.model.DyckPath;
import edu.gatech.catalanshuffle.model.DyckPath.InitType;

public class DyckPathCouplingCanvas extends CatalanModelCanvas {
	
	private DyckPath top;
	private DyckPath bottom;
	
	private int difference;
	private int tickCounter;
	//number of shuffles it takes for the two Dyck paths to merge. 
	private List<Integer> mergeTime;
	
	private boolean independentShuffling;
	
	public static final Random rand = new Random();
	
	public DyckPathCouplingCanvas(int n, double width, double height, double weightedLambda) {
		this(n, width, height, weightedLambda, false, true);
	}
	
	public DyckPathCouplingCanvas(int n, double width, double height, double weightedLambda, 
			boolean independentShuffling, boolean lazyChain) {
		super(n, width, height);
		this.top = new DyckPath(n, InitType.TOP, lazyChain, false, weightedLambda);
		this.bottom = new DyckPath(n, InitType.BUTTOM, lazyChain, false, weightedLambda);
		this.independentShuffling = independentShuffling;
		this.difference = n / 2;
		this.mergeTime =  new ArrayList<Integer>();
		this.tickCounter = 0;
		draw();
	}
	
	public void tick() {
		int index1 = rand.nextInt(2 * top.getN());
		int index2 = rand.nextInt(2 * top.getN());
		boolean firstIndexValue = rand.nextBoolean();
		
		top.shuffleOnce(index1, index2, firstIndexValue);
		
		if (independentShuffling) {
			index1 = rand.nextInt(2 * bottom.getN());
			index2 = rand.nextInt(2 * bottom.getN());
			firstIndexValue = rand.nextBoolean();
		}
		bottom.shuffleOnce(index1, index2, firstIndexValue);
		
		tickCounter++;
		draw();
	}
	
	public void setWeightedLambda(double weightedLambda) {
		top.setWeightedLambda(weightedLambda);
		bottom.setWeightedLambda(weightedLambda);
	}
	
	public void reset() {
		top.reset();
		bottom.reset();
		this.difference = top.getN() / 2;
		this.tickCounter = 0;
		draw();
	}
	
	private void draw() {
		double width = getWidth();
        double height = getHeight();
        int length = 2 * top.getN();
        double unitWidth = width / length;
        double unitHeight = height / top.getN();
        int curDifference = 0;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        
        double curTopHeight = height;
        double curBottomHeight = height;
        for (int i = 0; i < length; i++) {
        	// only counts case that top is true and bottom is false to avoid double counting. 
        	curDifference += (top.getModel()[i] && !bottom.getModel()[i] ? 1 : 0);
        	double nextTopHeight = curTopHeight + (top.getModel()[i] ? -unitHeight : unitHeight);
        	double nextBottomHeight = curBottomHeight + (bottom.getModel()[i] ? -unitHeight : unitHeight);
        	if (curTopHeight == curBottomHeight && nextTopHeight == nextBottomHeight) {
        		gc.setStroke(Color.PURPLE);
        		gc.strokeLine(i*unitWidth, curTopHeight, (i+1)*unitWidth, nextTopHeight);
        	}
        	else {
        		gc.setStroke(Color.RED);
        		gc.strokeLine(i*unitWidth, curTopHeight, (i+1)*unitWidth, nextTopHeight);
        		gc.setStroke(Color.BLUE);
        		gc.strokeLine(i*unitWidth, curBottomHeight, (i+1)*unitWidth, nextBottomHeight);
        	}
        	curTopHeight = nextTopHeight;
        	curBottomHeight = nextBottomHeight;
        }
        if (difference != 0 && curDifference == 0) {
        	mergeTime.add(tickCounter);
        }
        difference = curDifference;
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeText("Time elapsed: " + tickCounter, 10, 20);
        gc.strokeText("Distance: " + difference, 10, 45);
        gc.strokeText("Coupling time: " + mergeTime, 10, 70);
	}
}
