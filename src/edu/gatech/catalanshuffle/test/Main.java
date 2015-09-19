package edu.gatech.catalanshuffle.test;

import edu.gatech.catalanshuffle.model.*;

public class Main {

	public static void main(String[] args) {
		CatalanModel model1 = new DyckPath(8);
		System.out.println(model1.testRandomness(50));
	}

}
