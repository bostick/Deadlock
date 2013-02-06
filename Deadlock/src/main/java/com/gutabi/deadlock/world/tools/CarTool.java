package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class CarTool extends ToolBase {
	
	private Car car;
	
	public CarTool(WorldScreen screen) {
		super(screen);
	}
	
	public void setPoint(Point p) {
		
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		APP.platform.setupScreen(s.contentPane.cp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public void pressed(InputEvent ev) {
		
		Car pressed = screen.world.carMap.carHitTest(ev.p);
		if (pressed != null) {
			
			this.car = pressed;
			
			car.toolOrigP = car.p;
			car.toolOrigAngle = car.angle;
			car.toolOrigShape = car.shape;
			
		}
		
	}
	
	public void released(InputEvent ev) {
		
		if (car.toolOrigShape.hitTest(screen.world.lastPressedWorldPoint)) {
			
			switch (car.state) {
			case IDLE:
			case DRIVING:
			case BRAKING:
				
				GraphPosition gpos = car.driver.overallPos.getGraphPosition();
				
				if (gpos instanceof RoadPosition) {
					
				} else if (gpos instanceof VertexPosition) {
					
				} else if (gpos instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)gpos;
					
					RushHourBoard b = (RushHourBoard)gpos.entity;
					
					RushHourBoardPosition rounded = null;
					switch (Side.angleToSide(car.angle)) {
					case TOP:
					case BOTTOM:
						rounded = new RushHourBoardPosition(b,
								Math.round(rpos.rowCombo - car.CAR_LENGTH/2) + car.CAR_LENGTH/2,
								Math.round(rpos.colCombo - car.CAR_WIDTH/2) + car.CAR_WIDTH/2);
						break;
					case LEFT:
					case RIGHT:
						rounded = new RushHourBoardPosition(b,
								Math.round(rpos.rowCombo - car.CAR_WIDTH/2) + car.CAR_WIDTH/2,
								Math.round(rpos.colCombo - car.CAR_LENGTH/2) + car.CAR_LENGTH/2);
						break;
					}
					
					car.setTransform(rounded.p, car.angle);
					car.driver.overallPos = car.driver.overallPath.findClosestGraphPositionPathPosition(car.p, car.driver.overallPath.startingPos, false);
					
					screen.contentPane.repaint();
					
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
			}
		}


	}
	
	/*
	 * calls car.setTransform(), setting car.p and car.angle
	 * 
	 */
	public void dragged(InputEvent ev) {
		
		if (car.toolOrigShape.hitTest(screen.world.lastPressedWorldPoint)) {
			
			Point diff = ev.p.minus(screen.world.lastPressedWorldPoint);
			
			
			Point carPTmp = car.toolOrigP.plus(diff);
			Point carP = null;
			double carAngle = car.angle;
			
			switch (car.state) {
			case IDLE:
			case DRIVING:
			case BRAKING:
				
				GraphPositionPathPosition pathPos = car.driver.overallPath.findClosestGraphPositionPathPosition(carPTmp, car.driver.overallPath.startingPos, false);
				GraphPosition gpos = pathPos.getGraphPosition();
				
				if (gpos instanceof RoadPosition) {
					
					carP = gpos.p;
					
					carAngle = ((RoadPosition)gpos).angle;
					
				} else if (gpos instanceof VertexPosition) {
					
					carP = gpos.p;
					
				} else if (gpos instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)gpos;
					
					if (!collidesWithBoardOrOtherCars(car, rpos)) {
						carP = rpos.p;
					} else {
						carP = car.p;
					}
					
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
			}
			
			car.setTransform(carP, carAngle);
			
			car.driver.overallPos = car.driver.overallPath.findClosestGraphPositionPathPosition(car.p, car.driver.overallPath.startingPos, false);
			
			screen.contentPane.repaint();
		}
		
	}
	
	private boolean collidesWithBoardOrOtherCars(Car car, RushHourBoardPosition rpos) {
		
		RushHourBoard b = (RushHourBoard)rpos.entity;
		Point test = rpos.p;
		
		OBB testOBB = Geom.localToWorld(car.localAABB, car.angle, test);
		
		if (b.overlapsPerimeter(testOBB)) {
			return true;
		} else {
			for (Car c : screen.world.carMap.cars) {
				if (c == car) {
					continue;
				}
				if (ShapeUtils.intersectAreaOO(testOBB, c.shape)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void draw(RenderingContext ctxt) {
		
	}
	
}
