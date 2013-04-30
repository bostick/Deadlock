package com.gutabi.capsloc.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.capsloc.geom.Capsule;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class RoadMarkMap {
	
	List<Capsule> marks = new ArrayList<Capsule>();
	
	public RoadMarkMap() {
		
	}
	
	public void addRoadMark(Point p0, Point p1) {
		
		return;
		
//		Capsule test = new Capsule(cam, null, new Circle(null, p0, 0.0), new Circle(null, p1, 0.0), -1);
//		
//		for (Capsule cap : marks) {
//			if (Capsule.contains(cap, test)) {
//				return;
//			}
//		}
//		
//		if (marks.size() == 100) {
//			marks.remove(0);
//		} else {
//			assert marks.size() < 100;
//		}
//		
//		marks.add(new Capsule(cam, null, new Circle(null, p0, 0.05), new Circle(null, p1, 0.05), -1));
	}
	
	public void postStop() {
		
		marks.clear();
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLACK);
		
		for (int i = 0; i < marks.size(); i++) {
			Capsule cap = marks.get(i);
			
			cap.paint(ctxt);
		}
		
	}
	
}
