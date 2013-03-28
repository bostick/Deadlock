package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.Line;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.geom.SweepUtils;
import com.gutabi.deadlock.geom.SweptOBB;
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
		if (pressed == null) {
			return;
		}
		
		if (car != null && pressed != car && car.state != CarStateEnum.IDLE) {
			return;
		}
		
		switch (pressed.state) {
		case IDLE:
			
			car = pressed;
			
			car.toolOrigP = car.center;
			car.toolOrigShape = car.shape;
			car.driver.toolOrigOverallPos = car.driver.overallPos;
			
			car.state = CarStateEnum.DRAGGING;
			
			break;
		case DRAGGING:
			/*
			 * double click?
			 */
			break;
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			
			if (pressed == car) {
				car.toolOrigP = car.center;
				car.toolOrigShape = car.shape;
				car.driver.toolOrigOverallPos = car.driver.overallPos;
				
				car.clearCoastingVel();
				car.state = CarStateEnum.DRAGGING;
			}
			
			break;
		default:
			assert false : pressed.state;
			break;
		}
		
	}
	
	public void released(InputEvent ev) {
		try {
			
			if (car == null) {
				return;
			}
			if (!car.toolOrigShape.hitTest(worldScreen.world.lastPressedWorldPoint)) {
				return;
			}
			if (dragVector == null) {
				return;
			}
			
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
						boolean roadOrVertex = (gpos instanceof RoadPosition || gpos instanceof VertexPosition);
						
						/*
						 * determine which direction to coast
						 */
						
						assert car.driver.toolOrigExitingVertexPos != null;
						
						int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
						boolean otherSideIsFree = true;
						
						if (roadOrVertex ? car.driver.overallPos.combo <= car.driver.toolOrigExitingVertexPos.combo : car.driver.overallPos.combo >= car.driver.toolOrigExitingVertexPos.combo) {
							
							/*
							 * overallPos is less than exiting vertex, so the other vertex must be less than overallPos
							 */
							
							int prevVertexIndex = roadOrVertex ? car.driver.overallPos.prevVertexIndex() : car.driver.toolOrigExitingVertexPos.prevVertexIndex();
							assert prevVertexIndex != -1;
							
							Fixture prevVertex = (Fixture)car.driver.overallPath.get(prevVertexIndex).entity;
							
							if (prevVertex.getFacingSide().isRightOrBottom()) {
								/*
								 * vertex on top or left
								 */
								
								for (int i = 0; i < studCount; i++) {
									RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(prevVertexIndex-1-i);
									RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
									if (!stud.isFree(car)) {
										otherSideIsFree = false;
										break;
									}
								}
								
							} else {
								
								for (int i = 0; i < studCount; i++) {
									RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(prevVertexIndex-2-i);
									RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
									if (!stud.isFree(car)) {
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
							
							int nextVertexIndex = roadOrVertex ? car.driver.overallPos.nextVertexIndex() : car.driver.toolOrigExitingVertexPos.nextVertexIndex();
							assert nextVertexIndex != -1;
							
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
										if (!stud.isFree(car)) {
											otherSideIsFree = false;
											break;
										}
									}
									
								} else {
									
									for (int i = 0; i < studCount; i++) {
										RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(nextVertexIndex+2+i);
										RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
										if (!stud.isFree(car)) {
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
						
//								RushHourBoard b = (RushHourBoard)((RushHourBoardPosition)gpos).entity;
						GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
						GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
//								GraphPositionPathPosition tmpRoundPos = car.driver.overallPos.round(car.length/2);
						
						if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
							car.driver.toolCoastingGoal = tmpCeilPos;
						} else {
							car.driver.toolCoastingGoal = tmpFloorPos;
						}
						
//								OBB testFloor = Geom.localToWorld(car.localAABB, car.angle, tmpFloorPos.p);
//								OBB testCeil = Geom.localToWorld(car.localAABB, car.angle, tmpCeilPos.p);
						
//								boolean floorWithin = testFloor.aabb.completelyWithin(b.gridAABB);
//								boolean ceilWithin = testCeil.aabb.completelyWithin(b.gridAABB);
						
//								if (!floorWithin && ceilWithin) {
//									car.driver.toolCoastingGoal = tmpCeilPos;
//								} else if (!ceilWithin && floorWithin) {
//									car.driver.toolCoastingGoal = tmpFloorPos;
//								} else if (ceilWithin && floorWithin) {
//									/*
//									 * both are valid, use rounded
//									 */
//									car.driver.toolCoastingGoal = tmpRoundPos;
//								} else {
//									assert !floorAndCeilWithinGrid((RushHourBoardPosition)gpos);
//									assert false;
//								}
						
						if (DMath.lessThanEquals(car.driver.toolCoastingGoal.combo, car.driver.overallPos.combo)) {
							
							car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
							car.state = CarStateEnum.COASTING_BACKWARD;
							
						} else {
							
							car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
							car.state = CarStateEnum.COASTING_FORWARD;
							
						}
						
					}
					
				}
				
				break;
			default:
				assert false;
				break;
			}	
			
		} finally {
			dragVector = null;
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
		
		if (car == null) {
			return;
		}
		if (!car.toolOrigShape.hitTest(worldScreen.world.lastPressedWorldPoint)) {
			return;
		}
		
		Point diff = ev.p.minus(worldScreen.world.lastPressedWorldPoint);
		
		Point carPTmp = car.toolOrigP.plus(diff);
		
		switch (car.state) {
		case DRAGGING:
			
			/*
			 * the 10.0 is a heuristic
			 * should be long enough to allow a flick to go from one side of the board to the other,
			 * but short enough to not have any path intersection problems, nor have any teleporting problems
			 * 
			 */
			GraphPositionPathPosition attemptedPos = car.driver.overallPath.generalSearch(carPTmp, car.driver.overallPos, 10.0);
			
			GraphPositionPathPosition actualPos = furthestAllowablePosition(car.driver.overallPos, attemptedPos);
			
			car.setTransform(actualPos.p, actualPos.angle());
			car.setPhysicsTransform();
			
			car.driver.setOverallPos(actualPos);
			
			GraphPosition gpos = car.driver.overallPos.getGraphPosition();
			
			if (gpos instanceof RoadPosition) {
				
				if (car.driver.prevOverallPos.getGraphPosition() instanceof RushHourBoardPosition) {
					/*
					 * crossed a vertex
					 */
					
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
					}
					
				} else if (car.driver.prevOverallPos.getGraphPosition() instanceof VertexPosition) {
					
					if (car.driver.toolOrigExitingVertexPos == null) {
						
						car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
					}
					
				}
				
			} else if (gpos instanceof VertexPosition) {
				
				if (car.driver.toolOrigExitingVertexPos == null) {
					
					car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
				}
				
			} else {
				assert gpos instanceof RushHourBoardPosition;
				
				GraphPosition prevGPos = car.driver.prevOverallPos.getGraphPosition();
				
				if (prevGPos instanceof RoadPosition) {
					/*
					 * crossed a vertex
					 */
					
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
					}
					
				} else if (prevGPos instanceof VertexPosition) {
					
					
				} else if (prevGPos instanceof RushHourBoardPosition && !floorAndCeilWithinGrid((RushHourBoardPosition)gpos)) {
					
					if (car.driver.toolOrigExitingVertexPos == null) {
						
						int nextVertexIndex;
						if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
							nextVertexIndex = car.driver.overallPos.nextVertexIndex();
							assert nextVertexIndex != -1;
						} else {
							nextVertexIndex = car.driver.prevOverallPos.prevVertexIndex();
							assert nextVertexIndex != -1;
						}
						
						car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
					}
					
				}
				
			}
			
			handleZooming();
			
			worldScreen.contentPane.repaint();
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
	/**
	 * from start, move along path until either a collision or reach end. return the position
	 */
	private GraphPositionPathPosition furthestAllowablePosition(GraphPositionPathPosition start, GraphPositionPathPosition end) {
		
		if (start.equals(end)) {
			return start;
		}
		
		return (start.combo < end.combo) ? furthestAllowablePositionForward(start, end) : furthestAllowablePositionBackward(start, end);		
	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	private GraphPositionPathPosition furthestAllowablePositionForward(GraphPositionPathPosition preStart, GraphPositionPathPosition end) {
		
		GraphPositionPathPosition start = findFirstRightAngleForwardOrEnd(preStart, end);
		
		if (start.equals(end)) {
			
			return end;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			OBB so = Geom.localToWorld(car.localAABB, start.angle(), start.p);
			OBB eo = Geom.localToWorld(car.localAABB, end.angle(), end.p);
			SweptOBB swept = new SweptOBB(so, eo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
			}
			
			return end;
		} else if (end.index == start.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			OBB so = Geom.localToWorld(car.localAABB, start.angle(), start.p);
			OBB eo = Geom.localToWorld(car.localAABB, end.angle(), end.p);
			SweptOBB swept = new SweptOBB(so, eo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, end.index, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
				}
			}
			
			return end;
			
		}
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition b;
		GraphPositionPathPosition startCeiling = start.ceil();
		GraphPositionPathPosition endFloor = end.floor();
		
		if (!startCeiling.equals(start)) {
			b = startCeiling;
			
			if (!DMath.isRightAngle(b.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(ao, bo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(a.param, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			if (!DMath.isRightAngle(b.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(ao, bo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				// identity
				param = DMath.lerp(0.0, 1.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			a = b;
		}
		if (!endFloor.equals(end)) {
			b = end;
			
			if (!DMath.isRightAngle(b.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(ao, bo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(0.0, b.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
			}
		}
		
		return end;
	}
	
	/**
	 * 
	 * returns end upon finding first non-right angle,
	 */
	private GraphPositionPathPosition furthestAllowablePositionBackward(GraphPositionPathPosition preStart, GraphPositionPathPosition end) {
		
		GraphPositionPathPosition start = findFirstRightAngleBackwardOrEnd(preStart, end);
		
		if (start.equals(end)) {
			
			return end;
			
		} else if (start.index == end.index) {
			
			if (!DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			OBB so = Geom.localToWorld(car.localAABB, start.angle(), start.p);
			OBB eo = Geom.localToWorld(car.localAABB, end.angle(), end.p);
			SweptOBB swept = new SweptOBB(so, eo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(start.param, end.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, start.index, param);
			}
			
			return end;
			
		} else if (end.index == start.index-1 && DMath.equals(start.param, 0.0)) {
			
			if (!DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			OBB so = Geom.localToWorld(car.localAABB, start.angle(), start.p);
			OBB eo = Geom.localToWorld(car.localAABB, end.angle(), end.p);
			SweptOBB swept = new SweptOBB(so, eo);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, end.param, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, start.index, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, end.index, param);
				}
			}
			
			return end;
		}
		
		GraphPositionPathPosition b = start;
		GraphPositionPathPosition a;
		GraphPositionPathPosition startFloor = start.floor();
		GraphPositionPathPosition endCeil = end.ceil();
		
		if (!startFloor.equals(start)) {
			a = startFloor;
			
			if (!DMath.isRightAngle(a.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(bo, ao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(b.param, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			b = a;
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a = b.prevBound();
			
			if (!DMath.isRightAngle(a.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(bo, ao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, 0.0, param);
				if (DMath.equals(param, 1.0)) {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index+1, 0.0);
				} else {
					return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
				}
			}
			
			b = a;
		}
		if (!endCeil.equals(end)) {
			a = end;
			
			if (!DMath.isRightAngle(a.angle())) {
				return end;
			}
			
			OBB ao = Geom.localToWorld(car.localAABB, a.angle(), a.p);
			OBB bo = Geom.localToWorld(car.localAABB, b.angle(), b.p);
			SweptOBB swept = new SweptOBB(bo, ao);
			
			double param = firstCollisionParam(car, swept);
			if (param != -1) {
				param = DMath.lerp(1.0, a.param, param);
				return new GraphPositionPathPosition(car.driver.overallPath, a.index, param);
			}
		}
		
		return end;
	}
	
	private GraphPositionPathPosition findFirstRightAngleForwardOrEnd(GraphPositionPathPosition start, GraphPositionPathPosition end) {
		
		if (DMath.isRightAngle(start.angle())) {
			
			return start;
			
		} else if (start.index == end.index) {
			
			if (DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			return end;
			
		} else if (end.index == start.index+1 && DMath.equals(end.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle())) {
				return end;
			}
			
			return end;
			
		}
		
		GraphPositionPathPosition a = start;
		GraphPositionPathPosition b;
		GraphPositionPathPosition startCeiling = start.ceil();
		GraphPositionPathPosition endFloor = end.floor();
		
		if (!startCeiling.equals(start)) {
			b = startCeiling;
			
			if (DMath.isRightAngle(b.angle())) {
				return b;
			}
			
			a = b;
		}
		while (true) {
			
			if (a.equals(endFloor)) {
				break;
			}
			
			b = a.nextBound();
			
			if (DMath.isRightAngle(b.angle())) {
				return b;
			}
			
			a = b;
		}
		if (!endFloor.equals(end)) {
			b = end;
			
			if (DMath.isRightAngle(b.angle())) {
				return b;
			}
			
		}
		
		return end;
	}
	
	private GraphPositionPathPosition findFirstRightAngleBackwardOrEnd(GraphPositionPathPosition start, GraphPositionPathPosition end) {
		
		if (DMath.isRightAngle(start.angle())) {
			
			return start;
			
		} else if (start.index == end.index) {
			
			if (DMath.isRightAngle(end.angle())) {
				return end;
			}
			
//			System.out.println("findFirstRightAngleBackwardOrEnd: reached end1: " + end);
			return end;
			
		} else if (end.index == start.index-1 && DMath.equals(start.param, 0.0)) {
			
			if (DMath.isRightAngle(end.angle())) {
				return end;
			}
			
//			System.out.println("findFirstRightAngleBackwardOrEnd: reached end2: " + end);
			return end;
		}
		
		GraphPositionPathPosition b = start;
		GraphPositionPathPosition a;
		GraphPositionPathPosition startFloor = start.floor();
		GraphPositionPathPosition endCeil = end.ceil();
		
		if (!startFloor.equals(start)) {
			a = startFloor;
			
			if (DMath.isRightAngle(a.angle())) {
				return a;
			}
			
			b = a;
		}
		while (true) {
			
			if (b.equals(endCeil)) {
				break;
			}
			
			a = b.prevBound();
			
			if (DMath.isRightAngle(a.angle())) {
				return a;
			}
			
			b = a;
		}
		if (!endCeil.equals(end)) {
			a = end;
			
			if (DMath.isRightAngle(a.angle())) {
				return a;
			}
			
		}
		
//		System.out.println("findFirstRightAngleBackwardOrEnd: reached end3: " + end);
		
		return end;
	}

	
	/**
	 * on the segment from start.center to end.center, what is the param of the first collision?
	 */
	double firstCollisionParam(Car car, SweptOBB swept) {
		
		double bestParam = -1.0;
		
		for (RushHourBoard b : worldScreen.world.graph.rushes) {
			for (Line l : b.perimeterSegments) {
				double param = SweepUtils.firstCollisionParam(l, swept);
				if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
					bestParam = param;
				}
			}
		}
		
		for (Car c : worldScreen.world.carMap.cars) {
			if (c == car) {
				continue;
			}
//			if (car.id == 0) {
//				String.class.getName();
//			}
			double param = SweepUtils.firstCollisionParam(c.shape, swept);
			if (param != -1 && (bestParam == -1 || DMath.lessThan(param, bestParam))) {
				bestParam = param;
			}
		}
		
		return bestParam;
	}
	
	public void handleZooming() {
		
		GraphPosition gpos = car.driver.overallPos.getGraphPosition();
		
		if (gpos instanceof RoadPosition) {
			
			RoadPosition rpos = (RoadPosition)gpos;
			
			double alpha = rpos.lengthToStartOfRoad / rpos.r.getTotalLength(rpos.r.start, rpos.r.end);
			double para;
			if (DMath.equals(alpha, 0.0)) {
				para = 1.0;
			} else if (DMath.equals(alpha, 1.0)) {
				para = 1.0;
			} else {
				double[] vals = new double[] {1.0, 0.75, 0.5, 0.5, 0.5, 0.5, 0.75, 1.0};
				double a = vals[(int)Math.floor(alpha * (vals.length-1))];
				double b = vals[(int)Math.floor(alpha * (vals.length-1))+1];
				para = DMath.lerp(a, b, (alpha * (vals.length-1) - Math.floor(alpha * (vals.length-1))));
			}
			
			worldScreen.world.zoomAbsolute(para);
			
//			worldScreen.world.render_worldPanel();
			
		} else if (gpos instanceof VertexPosition) {
			
			GraphPosition prevGPos = car.driver.prevOverallPos.getGraphPosition();
			
			if (prevGPos instanceof RoadPosition) {
				
				worldScreen.world.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
			
		} else {
			assert gpos instanceof RushHourBoardPosition;
			
			GraphPosition prevGPos = car.driver.prevOverallPos.getGraphPosition();
			
			if (prevGPos instanceof RoadPosition) {
				
				worldScreen.world.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
		}
		
	}
	
	public void draw(RenderingContext ctxt) {
		
	}
	
}
