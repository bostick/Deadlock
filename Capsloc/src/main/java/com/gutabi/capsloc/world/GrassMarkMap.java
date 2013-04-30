package com.gutabi.capsloc.world;

import java.util.ArrayList;
import java.util.List;

import com.gutabi.capsloc.geom.Capsule;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class GrassMarkMap {
	
	List<Capsule> marks = new ArrayList<Capsule>();
	
	public GrassMarkMap() {
		
	}
	
	public void addGrassMark(Point p0, Point p1) {
		
		Capsule test = new Capsule(
						new Circle(p0, 0.0),
						new Circle(p1, 0.0));
		
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
		
		marks.add(new Capsule(
					new Circle(p0, 0.1),
					new Circle(p1, 0.1)));
	}
	
	public void postStop() {
		
		marks.clear();
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		
		ctxt.setColor(Color.brown);
		
		for (int i = 0; i < marks.size(); i++) {
			Capsule cap = marks.get(i);
			
			cap.paint(ctxt);
		}
		
	}
	
}
