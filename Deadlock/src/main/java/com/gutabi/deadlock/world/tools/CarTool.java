package com.gutabi.deadlock.world.tools;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class CarTool extends ToolBase {
	
	private Car car;
	
	public CarTool(WorldScreen screen) {
		super(screen);
	}
	
	public void setPoint(Point p) {
		
	}
	
//	public void setCar(Car c) {
//		
//	}
	
	public Shape getShape() {
		return null;
	}
	
	
//	public void escKey() {
//		
//		screen.tool = new RegularTool(screen);
//		
//		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
//		
//		screen.contentPane.repaint();
//	}
	
	public void pressed(InputEvent ev) {
		
		Car pressed = screen.world.carMap.carHitTest(ev.p);
		if (pressed != null) {
			
			this.car = pressed;
			car.origState = pressed.state;
			
			car.toolP = car.p;
			car.toolAngle = car.angle;
			car.toolShape = car.shape;
			
			car.toolOrigP = car.p;
			car.toolOrigAngle = car.angle;
			car.toolOrigShape = car.shape;
			
			car.beginEditing = true;
			
		}
		
	}
	
	public void released(InputEvent ev) {
		
		car.endEditing = true;
		car = null;
	}
	
	public void dragged(InputEvent ev) {
		
		if (car.toolOrigShape.hitTest(screen.world.lastPressedWorldPoint)) {
			
			Point diff = ev.p.minus(screen.world.lastPressedWorldPoint);
			
			Point carP = car.toolOrigP.plus(diff);
			double carAngle = car.toolAngle;
			
			switch (car.origState) {
			case IDLE:
			case DRIVING:
			case BRAKING:
				
				GraphPositionPathPosition pathPos = car.driver.overallPath.findClosestGraphPositionPathPosition(car.driver.centerToGPPPPoint(carP), car.driver.overallPath.startingPos, false);
				GraphPosition gpos = pathPos.getGraphPosition();
				
				if (gpos instanceof RoadPosition) {
					
					carP = gpos.p;
					
					carAngle = ((RoadPosition)gpos).angle;
					
				} else if (gpos instanceof VertexPosition) {
					
					carP = gpos.p;
					
				} else if (gpos instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)gpos;
					
					RushHourBoardPosition rounded = new RushHourBoardPosition((RushHourBoard)rpos.entity, Math.round(rpos.rowCombo), Math.round(rpos.colCombo));
					
					carP = car.driver.gpppPointToCenter(rounded.p);
					
				} else {
					assert false;
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
			
			car.setToolTransform(carP, carAngle);
			
			screen.contentPane.repaint();
			
		}
		
	}
	
//	public void moved(InputEvent ev) {
//		
//	}
	
//	public void clicked(InputEvent ev) {
//		
//		if (car.toolShape.hitTest(screen.world.lastPressedWorldPoint)) {
//			
//			car.endEditing = true;
//			
//			screen.tool = new RegularTool(screen);
//			
//			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
//			
//			screen.contentPane.repaint();
//			
//		}
//		
//	}
	
	public void draw(RenderingContext ctxt) {
		
//		if (!car.destroyed) {
//			
//			ctxt.setColor(Color.BLUE);
//			ctxt.setStrokeWidth(0.0);
//			car.toolShape.draw(ctxt);
//			
//		}
	}
	
}
