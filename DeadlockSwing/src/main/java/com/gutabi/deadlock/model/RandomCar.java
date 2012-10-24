package com.gutabi.deadlock.model;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Source;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.path.GraphPositionPath;

@SuppressWarnings("static-access")
public class RandomCar extends Car {
	
	private final double speed = 2.0;
	
	static Logger logger = Logger.getLogger(RandomCar.class);
	
	public RandomCar(Source s) {
		super(s);
		
		List<Vertex> poss = new ArrayList<Vertex>();
		poss.add(s);
		poss.add(s.matchingSink);
		overallPath = GraphPositionPath.createRandomPathFromSkeleton(poss, graph);
		
//		logger.debug(overallPath);
		
		computeStarting();
	}
	
	@Override
	BufferedImage image() {
		return MODEL.world.randomCar;
	}
	
	public double getMetersPerSecond() {
		return speed;
	}

}
