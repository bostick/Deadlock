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
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class CarTool extends ToolBase {
	
	private InteractiveCar car;
	
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
		
		InteractiveCar pressed = (InteractiveCar)worldScreen.world.carMap.carHitTest(ev.p);
		if (pressed != null) {
			
			switch (pressed.state) {
			case IDLE:
				
				car = pressed;
				
				car.toolOrigP = car.center;
				car.toolOrigShape = car.shape;
				car.driver.toolOrigOverallPos = car.driver.overallPos;
				
				car.state = CarStateEnum.DRAGGING;
				
				break;
			case DRAGGING:
				assert false;
				break;
			case COASTING_FORWARD:
			case COASTING_BACKWARD:
				
				car = pressed;
				
				car.toolOrigP = car.center;
				car.toolOrigShape = car.shape;
				car.driver.toolOrigOverallPos = car.driver.overallPos;
				
				car.clearCoastingVel();
				car.state = CarStateEnum.DRAGGING;
				
				break;
			default:
				assert false : pressed.state;
				break;
			}
			
		}
		
	}
	
	public void released(InputEvent ev) {
		
		if (car != null && car.toolOrigShape.hitTest(worldScreen.world.lastPressedWorldPoint)) {
			
			switch (car.state) {
			case DRAGGING:
				
				if (car.driver.toolOrigExitingVertexPos != null && 
					
					car.driver.toolOrigExitingVertexPos.isEndOfPath()) {
						/*
						 * last vertex has been visited, so always go back to there
						 */
						
						car.driver.toolCoastingGoal = car.driver.toolOrigExitingVertexPos;
						car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
						car.state = CarStateEnum.COASTING_FORWARD;
					
					
				} else {
					
					GraphPosition gpos = car.driver.overallPos.getGraphPosition();
					
					if (gpos instanceof RoadPosition || gpos instanceof VertexPosition || (gpos instanceof RushHourBoardPosition && !floorAndCeilWithinGrid((RushHourBoardPosition)gpos))) {
						
						/*
						 * determine which direction to coast
						 */
						
						assert car.driver.toolOrigExitingVertexPos != null;
						
						int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
						boolean otherSideIsFree = true;
						
						if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
							
							/*
							 * overallPos is less than exiting vertex, so the other vertex must be less than overallPos
							 */
							
							int prevVertexIndex = car.driver.overallPath.prevVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
							
							Fixture prevVertex = (Fixture)car.driver.overallPath.get(prevVertexIndex).entity;
							
							if (prevVertex.getFacingSide().isRightOrBottom()) {
								/*
								 * vertex on top or left
								 */
								
								for (int i = 0; i < studCount; i++) {
									RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(prevVertexIndex-1-i);
									RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
									if (!stud.isFree()) {
										otherSideIsFree = false;
										break;
									}
								}
								
							} else {
								
								for (int i = 0; i < studCount; i++) {
									RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(prevVertexIndex-2-i);
									RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
									if (!stud.isFree()) {
										otherSideIsFree = false;
										break;
									}
								}
								
							}
							
							if (otherSideIsFree) {
								car.driver.toolCoastingGoal = new GraphPositionPathPosition(car.driver.overallPath, prevVertexIndex, 0.0).travelBackward(RushHourStud.SIZE + 0.5 * studCount);
								car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
								car.state = CarStateEnum.COASTING_BACKWARD;
							} else {
								car.driver.toolCoastingGoal = car.driver.toolOrigExitingVertexPos.travelForward(RushHourStud.SIZE + 0.5 * studCount);
								car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
								car.state = CarStateEnum.COASTING_FORWARD;
							}
							
						} else {
							
							int nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
							
							if (nextVertexIndex == car.driver.overallPath.size-1) {
								/*
								 * going through exit, so "other side is free"
								 */
								
							} else {
								
								Fixture nextVertex = (Fixture)car.driver.overallPath.get(nextVertexIndex).entity;
								
								if (nextVertex.getFacingSide().isRightOrBottom()) {
									/*
									 * vertex on top or left
									 */
									
									for (int i = 0; i < studCount; i++) {
										RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(nextVertexIndex+1+i);
										RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
										if (!stud.isFree()) {
											otherSideIsFree = false;
											break;
										}
									}
									
								} else {
									
									for (int i = 0; i < studCount; i++) {
										RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(nextVertexIndex+2+i);
										RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
										if (!stud.isFree()) {
											otherSideIsFree = false;
											break;
										}
									}
								}
							}
							
							if (otherSideIsFree) {
								if (!(nextVertexIndex == car.driver.overallPath.size-1)) {
									car.driver.toolCoastingGoal = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0).travelForward(RushHourStud.SIZE + 0.5 * studCount);
								} else {
									/*
									 * going through exit
									 */
									car.driver.toolCoastingGoal = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
								}
								car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
								car.state = CarStateEnum.COASTING_FORWARD;
							} else {
								car.driver.toolCoastingGoal = car.driver.toolOrigExitingVertexPos.travelBackward(RushHourStud.SIZE + 0.5 * studCount);
								car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
								car.state = CarStateEnum.COASTING_BACKWARD;
							}
							
						}
						
					} else {
						assert gpos instanceof RushHourBoardPosition;
						assert floorAndCeilWithinGrid((RushHourBoardPosition)gpos);
						
						RushHourBoard b = (RushHourBoard)((RushHourBoardPosition)gpos).entity;
						
						GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
						GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
						GraphPositionPathPosition tmpRoundPos = car.driver.overallPos.round(car.length/2);
						
						OBB testFloor = Geom.localToWorld(car.localAABB, car.angle, tmpFloorPos.p);
						OBB testCeil = Geom.localToWorld(car.localAABB, car.angle, tmpCeilPos.p);
						
						boolean floorWithin = testFloor.aabb.completelyWithin(b.gridAABB);
						boolean ceilWithin = testCeil.aabb.completelyWithin(b.gridAABB);
						
						if (!floorWithin && ceilWithin) {
							car.driver.toolCoastingGoal = tmpCeilPos;
						} else if (!ceilWithin && floorWithin) {
							car.driver.toolCoastingGoal = tmpFloorPos;
						} else if (ceilWithin && floorWithin) {
							/*
							 * both are valid, use rounded
							 */
							car.driver.toolCoastingGoal = tmpRoundPos;
						} else {
							assert !floorAndCeilWithinGrid((RushHourBoardPosition)gpos);
							assert false;
						}
						
						if (DMath.lessThanEquals(car.driver.toolCoastingGoal.combo, car.driver.overallPos.combo)) {
							
							car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
							car.state = CarStateEnum.COASTING_BACKWARD;
							
						} else {
							
							car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
							car.state = CarStateEnum.COASTING_FORWARD;
							
						}
						
					}
					
				}
				
//				car.toolOrigP = null;
//				car.toolOrigShape = null;
//				car.driver.toolOrigOverallPos = null;
				
				break;
			default:
				assert false;
				break;
			}
			
			
			car = null;
		}
		
	}
	
	boolean floorAndCeilWithinGrid(RushHourBoardPosition bpos) {
		
		RushHourBoard b = (RushHourBoard)bpos.entity;
		
		GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
		GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
		
		OBB testFloor = Geom.localToWorld(car.localAABB, car.angle, tmpFloorPos.p);
		OBB testCeil = Geom.localToWorld(car.localAABB, car.angle, tmpCeilPos.p);
		
		boolean floorWithin = testFloor.aabb.completelyWithin(b.gridAABB);
		boolean ceilWithin = testCeil.aabb.completelyWithin(b.gridAABB);
		
		return floorWithin && ceilWithin;
	}
	
	Point prevDragP;
	Point curDragP;
	Point dragVector;
	long prevDragMillis;
	long curDragMillis;
	long dragTimeStepMillis;
	
	/**
	 * setting dynamic properties is done here
	 */
	public void dragged(InputEvent ev) {
		
		prevDragP = curDragP;
		prevDragMillis = curDragMillis;
		curDragP = ev.p;
		curDragMillis = System.currentTimeMillis();
		if (prevDragP != null) {
			dragVector = curDragP.minus(prevDragP);
			dragTimeStepMillis = curDragMillis - prevDragMillis;
		}
				
		if (car != null) {
			
			Point diff = ev.p.minus(worldScreen.world.lastPressedWorldPoint);
			
			Point carPTmp = car.toolOrigP.plus(diff);
			
			switch (car.state) {
			case DRAGGING:
				
				GraphPositionPathPosition testPathPos = car.driver.overallPath.generalSearch(carPTmp, car.driver.overallPos, car.length);
				
				if (!collidesWithBoardOrOtherCars(car, testPathPos.p)) {
					
					car.setTransform(testPathPos.p, DMath.greaterThanEquals(testPathPos.combo, car.driver.overallPos.combo) ? testPathPos.angle() : /*2*Math.PI-*/testPathPos.angle());
					car.setPhysicsTransform();
					
					car.driver.setOverallPos(testPathPos);
					
					GraphPosition gpos = car.driver.overallPos.getGraphPosition();
					
					if (gpos instanceof RoadPosition) {
						
						if (car.driver.prevOverallPos.getGraphPosition() instanceof RushHourBoardPosition) {
							
							if (car.driver.toolOrigExitingVertexPos == null) {
								
								int nextVertexIndex;
								if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.prevOverallPos.index, car.driver.prevOverallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.prevOverallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.overallPos.combo;
								} else {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.overallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.prevOverallPos.combo;
								}
								
								car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//								System.out.println(car.driver.toolOrigExitingVertexPos.getGraphPosition());
							}
							
						} else if (car.driver.prevOverallPos.getGraphPosition() instanceof VertexPosition) {
							/*
							 * dragging from last vertex
							 */
							if (car.driver.toolOrigExitingVertexPos == null) {
								
								car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
//								System.out.println(car.driver.toolOrigExitingVertexPos.getGraphPosition());
							}
							
						}
						
//						double a = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
//						
//						double para;
//						if (a < 1.0/3.5) {
//							para = 1.0 + (3.5 * a) * (0.5 - 1.0);
//						} else if (a < (1-1.0/3.5)) {
//							para = 0.5;
//						} else {
//							para = 0.5 + (3.5 * (a - (1-1.0/3.5))) * (1.0 - 0.5);
//						}
//						
//						worldScreen.world.zoomAbsolute(para);
//						
//						worldScreen.world.render_worldPanel();
						
					} else if (gpos instanceof VertexPosition) {
						
						if (car.driver.toolOrigExitingVertexPos == null) {
							
							car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
//							System.out.println(car.driver.toolOrigExitingVertexPos.getGraphPosition());
						}
						
//						worldScreen.world.zoomAbsolute(1.0);
//						
//						worldScreen.world.render_worldPanel();
						
					} else if (gpos instanceof RushHourBoardPosition) {
						
						GraphPosition prevGPos = car.driver.prevOverallPos.getGraphPosition();
						
						if (prevGPos instanceof RoadPosition) {
							
							if (car.driver.toolOrigExitingVertexPos == null) {
								
								int nextVertexIndex;
								if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.prevOverallPos.index, car.driver.prevOverallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.prevOverallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.overallPos.combo;
								} else {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.overallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.prevOverallPos.combo;
								}
								
								car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//								System.out.println(car.driver.toolOrigExitingVertexPos.getGraphPosition());
							}
							
						} else if (prevGPos instanceof VertexPosition) {
							
//							assert car.driver.toolOrigExitingVertexPos == car.driver.prevOverallPos;
							
						} else if (prevGPos instanceof RushHourBoardPosition && !floorAndCeilWithinGrid((RushHourBoardPosition)gpos)) {
							
							if (car.driver.toolOrigExitingVertexPos == null) {
								
								int nextVertexIndex;
								if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.prevOverallPos.index, car.driver.prevOverallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.prevOverallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.overallPos.combo;
								} else {
									nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
									assert nextVertexIndex != -1;
									assert car.driver.overallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.prevOverallPos.combo;
								}
								
								car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//								System.out.println(car.driver.toolOrigExitingVertexPos.getGraphPosition());
							}
							
						}
						
//						worldScreen.world.zoomAbsolute(1.0);
//						
//						worldScreen.world.render_worldPanel();
						
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
