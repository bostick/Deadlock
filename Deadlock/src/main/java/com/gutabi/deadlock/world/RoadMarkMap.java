package com.gutabi.deadlock.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Capsule;
import com.gutabi.deadlock.core.geom.Circle;

public class RoadMarkMap {
	
	WorldCamera cam;
	List<Capsule> marks = new ArrayList<Capsule>();
	
	public RoadMarkMap(WorldCamera cam) {
		this.cam = cam;
	}
	
	public void addRoadMark(Point p0, Point p1) {
		
		Capsule test = new Capsule(cam, null, new Circle(null, p0, 0.0), new Circle(null, p1, 0.0), -1);
		
		for (Capsule cap : marks) {
			if (cap.) {
				
			}
		}
		
	}
	
}
