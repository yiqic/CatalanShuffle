package edu.gatech.catalanshuffle.view;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import edu.gatech.catalanshuffle.model.DyckPath;

public class DyckPathCollectionCanvas extends CatalanModelCanvas {
	
	private DyckPath[] model;
	
	public DyckPathCollectionCanvas(int n, double width, double height, int size) {
		super(n, width, height);
		this.model = new DyckPath[size];
		for (int i = 0; i < size; i++) {
			model[i] = new DyckPath(n);
		}
		draw();
	}
	
	public void tick() {
		for (DyckPath p : model) {
			p.shuffleOnce();
		}
		draw();
	}
	
	private void draw() {
		double width = getWidth();
        double height = getHeight();
        int length = 2 * model[0].getN();
        double unitWidth = width / length;
        double unitHeight = height / model[0].getN();
        double unitOpacity = 1.0 / model.length;
        
        Map<List<Double>, Double> viewModel = new HashMap<>();
        for (DyckPath path : model) {
        	double curHeight = height;
            for (int i = 0; i < length; i++) {
            	double nextHeight = curHeight + (path.getModel()[i] ? -unitHeight : unitHeight);
            	List<Double> coor = new ArrayList<>();
            	coor.add(i*unitWidth);
            	coor.add(curHeight);
            	coor.add((i+1)*unitWidth);
            	coor.add(nextHeight);
            	if (viewModel.containsKey(coor)) {
            		viewModel.put(coor, viewModel.get(coor) + unitOpacity);
            	}
            	else {
            		viewModel.put(coor, unitOpacity);
            	}
            	curHeight = nextHeight;
            }
        }

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setLineWidth(5);
        
        for (List<Double> coor : viewModel.keySet()) {
        	gc.setStroke(new Color(0, 0, 1, Math.min(1.0, viewModel.get(coor))));
        	gc.strokeLine(coor.get(0), coor.get(1), coor.get(2), coor.get(3));
        }
        
	}
}
