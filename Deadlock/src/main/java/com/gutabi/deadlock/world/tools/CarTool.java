package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.ShapeUtils;
import com.gutabi.deadlock.math.DMath;
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
			
			car.toolOrigP = car.center;
			car.toolOrigAngle = car.angle;
			car.toolOrigShape = car.shape;
			car.driver.toolOrigOverallPos = car.driver.overallPos;
			
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
					
					/*
					 * reset
					 */
					car.setTransform(car.toolOrigP, car.toolOrigAngle);
					car.driver.overallPos = car.driver.toolOrigOverallPos;
					
					screen.contentPane.repaint();
					
				} else if (gpos instanceof VertexPosition) {
					
					/*
					 * reset
					 */
					car.setTransform(car.toolOrigP, car.toolOrigAngle);
					car.driver.overallPos = car.driver.toolOrigOverallPos;
					
					screen.contentPane.repaint();
					
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
					
					if (b.allowablePosition(car)) {
						
						car.setTransform(rounded.p, car.angle);
						car.driver.overallPos = car.driver.overallPath.generalSearch(car.center, car.driver.overallPos, car.CAR_LENGTH);
						
						screen.contentPane.repaint();
						
					} else {
						
						/*
						 * reset
						 */
						car.setTransform(car.toolOrigP, car.toolOrigAngle);
						car.driver.overallPos = car.driver.toolOrigOverallPos;
						
						screen.contentPane.repaint();
						
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
			
			switch (car.state) {
			case IDLE:
			case DRIVING:
			case BRAKING:
				
				GraphPositionPathPosition testPathPos = car.driver.overallPath.generalSearch(carPTmp, car.driver.overallPos, car.CAR_LENGTH);
				
				if (!collidesWithBoardOrOtherCars(car, testPathPos.p)) {
					
					car.setTransform(testPathPos.p, newAngle(car, testPathPos));
					
					car.driver.overallPos = testPathPos;
					
					GraphPosition gpos = car.driver.overallPos.getGraphPosition();
					
					if (gpos instanceof RoadPosition) {
						
						RoadPosition rpos = (RoadPosition)gpos;
						
						double a = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
						
						double para = 1.8 * a * a - 1.8 * a + 1;
						
						screen.world.zoomAbsolute(para);
						
						screen.world.render_worldPanel();
						
					} else if (gpos instanceof VertexPosition) {
						
						screen.world.zoomAbsolute(1.0);
						
						screen.world.render_worldPanel();
						
					} else if (gpos instanceof RushHourBoardPosition) {
						
						screen.world.zoomAbsolute(1.0);
						
						screen.world.render_worldPanel();
						
					} else {
						assert false;
					}
					
					screen.contentPane.repaint();
					
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
	
	private double newAngle(Car car, GraphPositionPathPosition testPathPos) {
		
		Point worldFront = Geom.localToWorld(car.localFront, car.angle, car.center);
		GraphPositionPathPosition centerPathPos = car.driver.overallPath.generalSearch(car.center, car.driver.overallPos, car.CAR_LENGTH);
		GraphPositionPathPosition frontPathPos = car.driver.overallPath.generalSearch(worldFront, car.driver.overallPos, car.CAR_LENGTH);
		int directionInTrack;
		if (DMath.lessThan(centerPathPos.combo, frontPathPos.combo)) {
			directionInTrack = 1;
		} else {
			directionInTrack = -1;
		}
		
		
		GraphPosition testGpos = testPathPos.getGraphPosition();
		
		double a;
		
		if (testPathPos.equals(car.driver.overallPos)) {
			a = car.angle;
		} else if (DMath.lessThan(car.driver.overallPos.combo, testPathPos.combo)) {
			if (directionInTrack == 1) {
				a = Math.atan2(testGpos.p.y - car.center.y, testGpos.p.x - car.center.x);
			} else {
				a = Math.atan2(car.center.y - testGpos.p.y, car.center.x - testGpos.p.x);
			}
		} else {
			if (directionInTrack == 1) {
				a = Math.atan2(car.center.y - testGpos.p.y, car.center.x - testGpos.p.x);
			} else {
				a = Math.atan2(testGpos.p.y - car.center.y, testGpos.p.x - car.center.x);
			}
		}
		
		if (DMath.lessThan(a, 0.0)) {
			a = a + 2 * Math.PI;
		}
		
		return a;
	}
	
	private boolean collidesWithBoardOrOtherCars(Car car, Point test) {
		
		OBB testOBB = Geom.localToWorld(car.localAABB, car.angle, test);
		
		for (RushHourBoard b : screen.world.graph.rushes) {
			if (b.overlapsPerimeter(testOBB)) {
				return true;
			}
		}
		
		for (Car c : screen.world.carMap.cars) {
			if (c == car) {
				continue;
			}
			if (ShapeUtils.intersectAreaOO(testOBB, c.shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void draw(RenderingContext ctxt) {
		
	}
	
}
