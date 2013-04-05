package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.menu.MenuTool;
import com.gutabi.deadlock.menu.WinnerMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.cars.CarStateEnum;
import com.gutabi.deadlock.world.cars.InteractiveCar;
import com.gutabi.deadlock.world.examples.RushHourWorld;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.GraphPosition;
import com.gutabi.deadlock.world.graph.GraphPositionPathPosition;
import com.gutabi.deadlock.world.graph.RoadPosition;
import com.gutabi.deadlock.world.graph.RushHourBoard;
import com.gutabi.deadlock.world.graph.RushHourBoardPosition;
import com.gutabi.deadlock.world.graph.RushHourStud;
import com.gutabi.deadlock.world.graph.VertexPosition;

public class InteractiveCarTool extends WorldToolBase {
	
	public InteractiveCar car;
	
	public InteractiveCarTool() {
		
	}
	
	public void setPoint(Point p) {
		
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void escKey() {
		
		APP.platform.unshowDebuggerScreen();
		
		MainMenu.action();
	}
	
	public void pressed(InputEvent ignore) {
		super.pressed(ignore);
		
		World world = (World)APP.model;
		
		InteractiveCar pressed = (InteractiveCar)world.carMap.carHitTest(world.lastPressedWorldPoint);
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
	
	public void released(InputEvent ignore) {
		super.released(ignore);
		
		RushHourWorld world = (RushHourWorld)APP.model;
		
		try {
			
			if (car == null) {
				return;
			}
			if (!car.toolOrigShape.hitTest(world.lastPressedWorldPoint)) {
				return;
			}
			if (dragVector == null) {
				return;
			}
			
			switch (car.state) {
			case DRAGGING:
				
				if (world.isWon) {
						
						car.driver.toolCoastingGoal = car.driver.overallPath.endPos;
						car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
						car.state = CarStateEnum.COASTING_FORWARD;
					
					
				} else {
					
					GraphPosition gpos = car.driver.overallPos.gp;
					
					if (gpos instanceof RoadPosition || gpos instanceof VertexPosition || (gpos instanceof RushHourBoardPosition && !floorAndCeilWithinGrid((RushHourBoardPosition)gpos))) {
						boolean road = gpos instanceof RoadPosition;
						boolean vertex = gpos instanceof VertexPosition;
						
						/*
						 * determine which direction to coast
						 */
						
						assert car.driver.toolOrigExitingVertexPos != null;
						
						int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
						boolean otherSideIsFree = true;
						
						if ((road||vertex) ? car.driver.overallPos.combo <= car.driver.toolOrigExitingVertexPos.combo : car.driver.overallPos.combo >= car.driver.toolOrigExitingVertexPos.combo) {
							
							/*
							 * overallPos is less than exiting vertex, so the other vertex must be less than overallPos
							 */
							
							int prevVertexIndex = road ? car.driver.overallPos.prevVertexIndex() : (vertex ? car.driver.overallPos.index : car.driver.toolOrigExitingVertexPos.prevVertexIndex());
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
							
							int nextVertexIndex = road ? car.driver.overallPos.nextVertexIndex() : (vertex ? car.driver.overallPos.index : car.driver.toolOrigExitingVertexPos.nextVertexIndex());
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
						
						GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
						GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
						
						if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
							car.driver.toolCoastingGoal = tmpCeilPos;
						} else {
							car.driver.toolCoastingGoal = tmpFloorPos;
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
				
				break;
			default:
				assert false;
				break;
			}	
			
		} finally {
			dragVector = null;
		}
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
	public void dragged(InputEvent ignore) {
		super.dragged(ignore);
		
		RushHourWorld world = (RushHourWorld)APP.model;
		
		prevDragP = curDragP;
		prevDragMillis = curDragMillis;
		curDragP = world.lastDraggedWorldPoint;
		curDragMillis = System.currentTimeMillis();
		if (prevDragP != null) {
			dragVector = curDragP.minus(prevDragP);
			dragTimeStepMillis = curDragMillis - prevDragMillis;
		}
		
		if (car == null) {
			return;
		}
		if (!car.toolOrigShape.hitTest(world.lastPressedWorldPoint)) {
			return;
		}
		
		Point diff = world.lastDraggedWorldPoint.minus(world.lastPressedWorldPoint);
		
		Point carPTmp = car.toolOrigP.plus(diff);
		
		switch (car.state) {
		case DRAGGING:
			
			/*
			 * the 10.0 is a heuristic
			 * should be long enough to allow a flick to go from one side of the board to the other,
			 * but short enough to not have any path intersection problems, nor have any teleporting problems
			 * 
			 */
			GraphPositionPathPosition attemptedPos = car.driver.overallPos.generalSearch(carPTmp, 10.0);
			
			GraphPositionPathPosition actualPos = car.driver.overallPos.furthestAllowablePosition(car, attemptedPos);
			
			car.setTransform(actualPos.p, actualPos.angle());
			car.setPhysicsTransform();
			
			car.driver.setOverallPos(actualPos);
			
			GraphPosition gpos = car.driver.overallPos.gp;
			
			if (gpos instanceof RoadPosition) {
				
				if (car.driver.prevOverallPos.gp instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)car.driver.prevOverallPos.gp;
					RushHourBoard b = (RushHourBoard)rpos.entity;
					
					/*
					 * crossed a vertex
					 */
					
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
					GraphPositionPathPosition vertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
					
					if (vertexPos.gp.entity == b.exitVertex && !world.isWon) {
						
						winner();
						
					} else if (car.driver.toolOrigExitingVertexPos == null) {
						
						car.driver.toolOrigExitingVertexPos = vertexPos;
					}
					
				} else if (car.driver.prevOverallPos.gp instanceof VertexPosition) {
					
					if (car.driver.toolOrigExitingVertexPos == null) {
						
						car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
					}
					
				}
				
			} else if (gpos instanceof VertexPosition) {
				
				if (car.driver.prevOverallPos.gp instanceof RushHourBoardPosition) {
					
					RushHourBoardPosition rpos = (RushHourBoardPosition)car.driver.prevOverallPos.gp;
					RushHourBoard b = (RushHourBoard)rpos.entity;
					
					if (car.driver.overallPos.gp.entity == b.exitVertex && !world.isWon) {
						
						winner();
						
					} else if (car.driver.toolOrigExitingVertexPos == null) {
						
						car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
					}
				}
				
			} else {
				assert gpos instanceof RushHourBoardPosition;
				
				GraphPosition prevGPos = car.driver.prevOverallPos.gp;
				
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
					
					RushHourBoardPosition bpos = (RushHourBoardPosition)gpos;
					RushHourBoardPosition prevBpos = (RushHourBoardPosition)car.driver.prevOverallPos.gp;
					RushHourBoard b = (RushHourBoard)bpos.entity;
					
					if (b.enteringBoard(prevBpos, bpos)) {
						
					} else {
						//leaving board
						
						int nextVertexIndex;
						if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
							nextVertexIndex = car.driver.overallPos.nextVertexIndex();
							assert nextVertexIndex != -1;
						} else {
							nextVertexIndex = car.driver.prevOverallPos.prevVertexIndex();
							assert nextVertexIndex != -1;
						}
						GraphPositionPathPosition vertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
						
						if (vertexPos.gp.entity == b.exitVertex && !world.isWon) {
							
							winner();
							
						} else if (car.driver.toolOrigExitingVertexPos == null) {
							
							car.driver.toolOrigExitingVertexPos = vertexPos;
						}
					}
					
				}
				
			}
			
			handleZooming();
			
			APP.appScreen.contentPane.repaint();
			
			break;
		default:
			assert false;
			break;
		}
		
	}
	
//	public void moved(InputEvent ev) {
//		
//		if (winnerMenu != null) {
//			
//			Point p = ev.p;
//			
//			p = Point.worldToPanel(p, worldCamera);
//			p = Point.panelToMenu(p, winnerMenu);
//			
//			winnerMenu.moved(p);
//			
//		}
//		
//	}
//	
//	public void clicked(InputEvent ev) {
//		
//		if (winnerMenu != null) {
//			
//			Point p = ev.p;
//			
//			p = Point.worldToPanel(p, worldCamera);
//			p = Point.panelToMenu(p, winnerMenu);
//			
//			winnerMenu.clicked(p);
//			
//		}
//		
//	}
	
	boolean floorAndCeilWithinGrid(RushHourBoardPosition bpos) {
		
		/*
		 * for uses of floorAndCeilWithinGridX, figure out if car is entering or leaving the board, behavior may depend on that
		 */
		
		RushHourBoard b = (RushHourBoard)bpos.entity;
		
		GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
		GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
		
		OBB testFloor = Geom.localToWorld(car.localAABB, car.angle, tmpFloorPos.p);
		OBB testCeil = Geom.localToWorld(car.localAABB, car.angle, tmpCeilPos.p);
		
		boolean floorWithin = testFloor.aabb.completelyWithin(b.gridAABB);
		boolean ceilWithin = testCeil.aabb.completelyWithin(b.gridAABB);
		
		return floorWithin && ceilWithin;
	}
	
	public void handleZooming() {
		World world = (World)APP.model;
		
		GraphPosition gpos = car.driver.overallPos.gp;
		
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
			
			world.worldCamera.zoomAbsolute(para);
			
//			worldScreen.world.render_worldPanel();
			
		} else if (gpos instanceof VertexPosition) {
			
			GraphPosition prevGPos = car.driver.prevOverallPos.gp;
			
			if (prevGPos instanceof RoadPosition) {
				
				world.worldCamera.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
			
		} else {
			assert gpos instanceof RushHourBoardPosition;
			
			GraphPosition prevGPos = car.driver.prevOverallPos.gp;
			
			if (prevGPos instanceof RoadPosition) {
				
				world.worldCamera.zoomAbsolute(1.0);
				
//				worldScreen.world.render_worldPanel();
			}
		}
		
	}
	
	void winner() {
		RushHourWorld world = (RushHourWorld)APP.model;
		
		world.isWon = true;
		
		world.winnerMenu = new WinnerMenu();
		
		world.winnerMenu.render();
		
		APP.tool = new MenuTool();
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
