package com.gutabi.deadlock.world.tools;

import java.awt.Color;

import org.jbox2d.common.Mat22;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.RoadPosition;

public class CarTool extends ToolBase {
	
	private Car car;
	Point origCarP;
	
//	Line line;
	RoadPosition closest;
	
	public CarTool(WorldScreen screen) {
		super(screen);
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
//		closest = null;
//		for (Edge e : screen.world.graph.edges) {
//			if (e instanceof Road) {
//				Road r = (Road)e;
//				RoadPosition pos = r.findClosestRoadPosition(p, Double.POSITIVE_INFINITY);
//				if (pos != null && (closest == null || Point.distance(pos.p, p) < Point.distance(closest.p, p))) {
//					closest = pos;
//				}
//			}
//		}
		
//		if (closest != null) {
//			line = new Line(p, closest.p);
//		}
	}
	
	public void setCar(Car c) {
		this.car = c;
		this.origCarP = car.p;
		
		car.beginEditing = true;
	}
	
	public Shape getShape() {
		return null;
	}
	
	
	public void escKey() {
		
		screen.tool = new RegularTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		
		screen.contentPane.repaint();
	}
	
	public void pressed(InputEvent ev) {
		
		origCarP = car.p;
		
	}
	
	public void dragged(InputEvent ev) {
		
		Point diff = ev.p.minus(screen.world.lastPressedWorldPoint);
		
		Point carP = origCarP.plus(diff);
		
//		closest = null;
//		for (Edge e : screen.world.graph.edges) {
//			if (e instanceof Road) {
//				Road r = (Road)e;
//				RoadPosition pos = r.findClosestRoadPosition(ev.p, Double.POSITIVE_INFINITY);
//				if (pos != null && (closest == null || Point.distance(pos.p, ev.p) < Point.distance(closest.p, ev.p))) {
//					closest = pos;
//				}
//			}
//		}
		
		car.p = carP;
//		Mat22 r = car.b2dBody.getTransform().R;
//		car.carTransArr[0][0] = r.col1.x;
//		car.carTransArr[0][1] = r.col2.x;
//		car.carTransArr[1][0] = r.col1.y;
//		car.carTransArr[1][1] = r.col2.y;
		car.shape = Geom.localToWorld(car.localQuad, car.carTransArr, car.p);
		
		screen.contentPane.repaint();
		
	}
	
//	public void moved(InputEvent ev) {
//		
//	}
	
	public void draw(RenderingContext ctxt) {
		
//		if (closest != null) {
//			ctxt.setColor(Color.BLUE);
//			new Circle(null, closest.p, 0.3).paint(ctxt);
//			
//		}
		
		if (!car.destroyed) {
			
			ctxt.setColor(Color.BLUE);
			ctxt.setPixelStroke(2.0);
			car.shape.draw(ctxt);
			
		}
	}
	
}
