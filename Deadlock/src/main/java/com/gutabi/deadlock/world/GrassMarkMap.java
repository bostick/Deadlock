package com.gutabi.deadlock.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.deadlock.geom.Capsule;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class GrassMarkMap {
	
	List<Capsule> marks = new ArrayList<Capsule>();
	
	public GrassMarkMap() {
		
	}
	
	public void addGrassMark(Point p0, Point p1) {
		
		Capsule test = new Capsule(null, new Circle(null, p0, 0.0), new Circle(null, p1, 0.0), -1);
		
		for (Capsule cap : marks) {
			if (Capsule.contains(cap, test)) {
				return;
			}
		}
		
		if (marks.size() == 100) {
			marks.remove(0);
		} else {
			assert marks.size() < 100;
		}
		
		marks.add(new Capsule(null, new Circle(null, p0, 0.1), new Circle(null, p1, 0.1), -1));
	}
	
	public void postStop() {
		
		marks.clear();
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		ctxt.setColor(Color.brown);
		
		for (Capsule cap : marks) {
			cap.paint(ctxt);
		}
		
	}
	
}
