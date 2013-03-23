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
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.Side;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class CarTool extends ToolBase {
	
	private Car car;
	
	public CarTool(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen, debuggerScreen);
	}
	
	public void setPoint(Point p) {
		
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void escKey() {
		
		APP.platform.unshowDebuggerScreen();
		
		APP.appScreen = null;
		
		MainMenu s = new MainMenu();
		
		APP.platform.setupAppScreen(s.contentPane.cp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public void pressed(InputEvent ev) {
		
		Car pressed = worldScreen.world.carMap.carHitTest(ev.p);
		if (pressed != null) {
			
			switch (pressed.state) {
			case IDLE:
				
				this.car = pressed;
				
				car.toolOrigP = car.center;
				car.toolOrigAngle = car.angle;
				car.toolOrigShape = car.shape;
				car.driver.toolOrigOverallPos = car.driver.overallPos;
				
				car.state = CarStateEnum.DRAGGING;
				
				break;
			default:
				assert false;
				break;
			}
			
		}
		
	}
	
	public void released(InputEvent ev) {
		
		if (car != null && car.toolOrigShape.hitTest(worldScreen.world.lastPressedWorldPoint)) {
			
			switch (car.state) {
			case DRAGGING:
				
				GraphPosition gpos = car.driver.overallPos.getGraphPosition();
				
				if (gpos instanceof RoadPosition) {
					
					/*
					 * reset
					 */
//					car.setTransform(car.toolOrigP, car.toolOrigAngle);
//					car.driver.overallPos = car.driver.toolOrigOverallPos;
//					
//					APP.appScreen.world.zoomAbsolute(1.0);
//					
//					APP.appScreen.world.render_worldPanel();
//					
//					APP.appScreen.contentPane.repaint();
					
//					Point worldFront = Geom.localToWorld(car.localFront, car.angle, car.center);
//					GraphPositionPathPosition centerPathPos = car.driver.overallPath.generalSearch(car.center, car.driver.overallPos, car.length);
//					GraphPositionPathPosition frontPathPos = car.driver.overallPath.generalSearch(worldFront, car.driver.overallPos, car.length);
//					int directionInTrack;
//					if (DMath.lessThan(centerPathPos.combo, frontPathPos.combo)) {
//						directionInTrack = 1;
//					} else {
//						directionInTrack = -1;
//					}
					
					int nextVertexIndex = car.driver.overallPos.nextVertexIndex();
					int prevVertexIndex = car.driver.overallPos.prevVertexIndex();
					
					GraphPosition nextVertex = car.driver.overallPath.get(nextVertexIndex);
					GraphPosition prevVertex = car.driver.overallPath.get(prevVertexIndex);
					
					
					double distToNext;
					double distToPrev;
					Road r = (Road)gpos.entity;
					if (nextVertex.entity == r.end) {
						assert prevVertex.entity == r.start;
						distToNext = ((RoadPosition)gpos).lengthToEndOfRoad;
						distToPrev = ((RoadPosition)gpos).lengthToStartOfRoad;
					} else {
						assert nextVertex.entity == r.start;
						assert prevVertex.entity == r.end;
						distToNext = ((RoadPosition)gpos).lengthToStartOfRoad;
						distToPrev = ((RoadPosition)gpos).lengthToEndOfRoad;
					}
					
					if (distToNext < distToPrev) {
						car.state = CarStateEnum.COASTING_FORWARD;
					} else {
						
//						System.out.println("overall: " + car.driver.overallPos);
						
						car.state = CarStateEnum.COASTING_BACKWARD;
					}
					
				} else if (gpos instanceof VertexPosition) {
					
					/*
					 * reset
					 */
					car.setTransform(car.toolOrigP, car.toolOrigAngle);
					car.setPhysicsTransform();
					car.driver.overallPos = car.driver.toolOrigOverallPos;
					
					worldScreen.world.zoomAbsolute(1.0);
					
					worldScreen.world.render_worldPanel();
					
					worldScreen.contentPane.repaint();
					
				} else if (gpos instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)gpos;
					
					RushHourBoard b = (RushHourBoard)gpos.entity;
					
					RushHourBoardPosition rounded = null;
					switch (Side.angleToSide(car.angle)) {
					case TOP:
					case BOTTOM:
						rounded = new RushHourBoardPosition(b,
								Math.round(rpos.rowCombo - car.length/2) + car.length/2,
								Math.round(rpos.colCombo - car.width/2) + car.width/2);
						break;
					case LEFT:
					case RIGHT:
						rounded = new RushHourBoardPosition(b,
								Math.round(rpos.rowCombo - car.width/2) + car.width/2,
								Math.round(rpos.colCombo - car.length/2) + car.length/2);
						break;
					}
					
					if (b.allowablePosition(car)) {
						
						car.state = CarStateEnum.IDLE;
						
						car.setTransform(rounded.p, car.angle);
						car.setPhysicsTransform();
						car.driver.overallPos = car.driver.overallPath.generalSearch(car.center, car.driver.overallPos, car.length);
						
						worldScreen.world.zoomAbsolute(1.0);
						
						worldScreen.world.render_worldPanel();
						
						worldScreen.contentPane.repaint();
						
					} else {
						
						car.state = CarStateEnum.IDLE;
						
						/*
						 * reset
						 */
						car.setTransform(car.toolOrigP, car.toolOrigAngle);
						car.setPhysicsTransform();
						car.driver.overallPos = car.driver.toolOrigOverallPos;
						
						worldScreen.world.zoomAbsolute(1.0);
						
						worldScreen.world.render_worldPanel();
						
						worldScreen.contentPane.repaint();
						
					}
					
				} else {
					assert false;
				}
				
				break;
			default:
				assert false;
				break;
			}
		}


	}
	
	/**
	 * setting dynamic properties is done here
	 */
	public void dragged(InputEvent ev) {
		
		if (car.toolOrigShape.hitTest(worldScreen.world.lastPressedWorldPoint)) {
			
			Point diff = ev.p.minus(worldScreen.world.lastPressedWorldPoint);
			
			Point carPTmp = car.toolOrigP.plus(diff);
			
			switch (car.state) {
			case DRAGGING:
				
				GraphPositionPathPosition testPathPos = car.driver.overallPath.generalSearch(carPTmp, car.driver.overallPos, car.length);
				
				if (!collidesWithBoardOrOtherCars(car, testPathPos.p)) {
					
					car.setTransform(testPathPos.p, car.newAngle(testPathPos));
					car.setPhysicsTransform();
					
					car.driver.overallPos = testPathPos;
					
					GraphPosition gpos = car.driver.overallPos.getGraphPosition();
					
					if (gpos instanceof RoadPosition) {
						
						RoadPosition rpos = (RoadPosition)gpos;
						
						double a = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
						
						double para;
						if (a < 1.0/3.5) {
							para = 1.0 + (3.5 * a) * (0.5 - 1.0);
						} else if (a < (1-1.0/3.5)) {
							para = 0.5;
						} else {
							para = 0.5 + (3.5 * (a - (1-1.0/3.5))) * (1.0 - 0.5);
						}
						
						worldScreen.world.zoomAbsolute(para);
						
						worldScreen.world.render_worldPanel();
						
					} else if (gpos instanceof VertexPosition) {
						
						worldScreen.world.zoomAbsolute(1.0);
						
						worldScreen.world.render_worldPanel();
						
					} else if (gpos instanceof RushHourBoardPosition) {
						
						worldScreen.world.zoomAbsolute(1.0);
						
						worldScreen.world.render_worldPanel();
						
					} else {
						assert false;
					}
					
					worldScreen.contentPane.repaint();
					
				}
				
				break;
			default:
				assert false;
				break;
			}
			
		}
		
	}
	
	private boolean collidesWithBoardOrOtherCars(Car car, Point test) {
		
		OBB testOBB = Geom.localToWorld(car.localAABB, car.angle, test);
		
		for (RushHourBoard b : worldScreen.world.graph.rushes) {
			if (b.overlapsPerimeter(testOBB)) {
				return true;
			}
		}
		
		for (Car c : worldScreen.world.carMap.cars) {
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
