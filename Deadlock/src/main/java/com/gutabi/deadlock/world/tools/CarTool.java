package com.gutabi.deadlock.world.tools;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Geom;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Edge;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;

public class CarTool extends ToolBase {
	
	private Car car;
	
	public CarTool(WorldScreen screen) {
		super(screen);
	}
	
	public void setPoint(Point p) {
		
	}
	
	public void setCar(Car c) {
		this.car = c;
		car.origState = c.state;
//		car.origP = car.p;
//		car.origAngle = car.angle;
//		car.origShape = c.shape;
		
		car.toolP = car.p;
		car.toolAngle = car.angle;
		car.toolShape = car.shape;
		
		car.toolOrigP = car.p;
		car.toolOrigAngle = car.angle;
		car.toolOrigShape = car.shape;
		
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
		
		if (car.toolShape.hitTest(screen.world.lastPressedWorldPoint)) {
			car.toolOrigP = car.toolP;
			car.toolOrigShape = car.toolShape;
		}
		
	}
	
	public void dragged(InputEvent ev) {
		
		if (car.toolOrigShape.hitTest(screen.world.lastPressedWorldPoint)) {
			
			Point diff = ev.p.minus(screen.world.lastPressedWorldPoint);
			
			Point carP = car.toolOrigP.plus(diff);
			double carAngle = car.toolOrigAngle;
			
			switch (car.origState) {
			case DRIVING:
			case BRAKING:
				
				RoadPosition closest = null;
				for (Edge e : screen.world.graph.edges) {
					if (e instanceof Road) {
						Road r = (Road)e;
						RoadPosition pos = r.findClosestRoadPosition(carP, Double.POSITIVE_INFINITY);
						if (pos != null && (closest == null || Point.distance(pos.p, carP) < Point.distance(closest.p, carP))) {
							closest = pos;
						}
					}
				}
				
				if (closest != null) {
					carP = closest.p;
					
					carAngle = closest.angle;
					
				}
				
				break;
			case CRASHED:
				break;
			case SINKED:
				break;
			case SKIDDED:
				break;
			case EDITING:
				assert false;
				break;
			}
			
			car.toolP = carP;
			car.toolAngle = carAngle;
			double[][] mat = Geom.rotationMatrix(car.toolAngle);
			car.toolShape = Geom.localToWorld(car.localQuad, mat, car.toolP);
			
			screen.contentPane.repaint();
			
		}
		
	}
	
//	public void moved(InputEvent ev) {
//		
//	}
	
	public void clicked(InputEvent ev) {
		
		if (car.toolShape.hitTest(screen.world.lastPressedWorldPoint)) {
			
			car.endEditing = true;
			
			screen.tool = new RegularTool(screen);
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
			
			screen.contentPane.repaint();
			
		}
		
	}
	
	public void draw(RenderingContext ctxt) {
		
//		if (closest != null) {
//			ctxt.setColor(Color.BLUE);
//			new Circle(null, closest.p, 0.3).paint(ctxt);
//			
//		}
		
		if (!car.destroyed) {
			
			ctxt.setColor(Color.BLUE);
			ctxt.setPixelStroke(2.0);
			car.toolShape.draw(ctxt);
			
		}
	}
	
}
