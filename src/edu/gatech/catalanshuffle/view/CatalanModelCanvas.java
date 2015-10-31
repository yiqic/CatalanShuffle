package edu.gatech.catalanshuffle.view;

import javafx.scene.canvas.Canvas;

public abstract class CatalanModelCanvas extends Canvas {
	
	public CatalanModelCanvas(double width, double height) {
		super(width, height);
	}

	public abstract void tick();
	
}
