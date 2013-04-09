package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.Geom;
import com.gutabi.deadlock.geom.OBB;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.DMath;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.rushhour.RushHourWorld;
import com.gutabi.deadlock.rushhour.WinnerMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
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
			
			if (car.state != CarStateEnum.DRAGGING) {
				return;
			}
			
			if (world.isWon) {
				
				car.driver.toolCoastingGoal = car.driver.overallPath.endPos;
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
			
			} else {
				
				GraphPosition gpos = car.driver.overallPos.gp;
//				GraphPositionPathPosition prevPos = car.driver.prevOverallPos;
				
				if (gpos instanceof RoadPosition) {
					assert car.driver.toolOrigExitingVertexPos != null;
					
					if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
						determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition(), true);
					} else {
						determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition(), true);
					}
					
				} else if (gpos instanceof VertexPosition) {
					assert car.driver.toolOrigExitingVertexPos != null;
					
					if (car.driver.overallPos.combo == car.driver.toolOrigExitingVertexPos.combo) {
						
//						lookAtOrigExitingVertex(car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RushHourBoardPosition ? true : false);
						
						if (car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RoadPosition) {
							determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition(), true);
						} else {
							determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition(), true);
						}
						
					} else {
						assert false;
					}
					
//					if (prevPos == null) {
//						
//						if (car.driver.overallPos.combo == car.driver.toolOrigExitingVertexPos.combo) {
//							
////							lookAtOrigExitingVertex(car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RushHourBoardPosition ? true : false);
//							
//							if (car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RoadPosition) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition(), true);
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition(), true);
//							}
//							
//						} else {
//							assert false;
//						}
//						
//					} else if (prevPos.gp instanceof RushHourBoardPosition) {
//						
//						if (car.driver.overallPos.combo == car.driver.toolOrigExitingVertexPos.combo) {
//							// leaving board
//							
//							if (car.driver.prevOverallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							}
//							
//						} else {
//							assert false;
//						}
//						
//					} else {
//						assert prevPos.gp instanceof RoadPosition;
//						
//						if (car.driver.overallPos.combo == car.driver.toolOrigExitingVertexPos.combo) {
//							// entering board
//							
//							if (car.driver.prevOverallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							}
//							
//						} else {
//							// entering board
//							
//							if (car.driver.prevOverallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							}
//							
//						}
//					}
					
				} else {
					assert gpos instanceof RushHourBoardPosition;
					
					if (!floorAndCeilWithinGrid((RushHourBoardPosition)gpos)) {
						assert car.driver.toolOrigExitingVertexPos != null;
						
						if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
							determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition(), false);
						} else {
							determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition(), false);
						}
						
//						if (prevPos == null) {
//							
//							if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							}
//							
//						} else if (prevPos.gp instanceof RoadPosition) {
//							/*
//							 * crossed a vertex
//							 */
//							
//							if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							}
//							
//						} else if (prevPos.gp instanceof VertexPosition) {
//							
//							if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							}
//							
//						} else if (prevPos.gp instanceof RushHourBoardPosition) {
//							
//							RushHourBoardPosition bpos = (RushHourBoardPosition)gpos;
//							RushHourBoardPosition prevBpos = (RushHourBoardPosition)prevPos.gp;
//							RushHourBoard b = (RushHourBoard)bpos.entity;
//							
//	//						if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//	//							lookAtNextVertex(car.driver.overallPos.gp);
//	//						} else {
//	//							lookAtPreviousVertex(car.driver.overallPos.gp);
//	//						}
//							
//							if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.prevVertexPosition());
//							} else {
//								determineCoasting(car.driver.toolOrigExitingVertexPos, car.driver.toolOrigExitingVertexPos.nextVertexPosition());
//							}
//							
////							if (b.enteringBoard(prevBpos, bpos)) {
////								
//////								if (overallPos.combo < toolOrigExitingVertexPos) {
//////									next
//////								} else {
//////									prev
//////								}
//////								lookAtOrigExitingVertex(car.driver.prevOverallPos.combo < car.driver.overallPos.combo ? true : false);
////								
////								if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
////									lookAtPreviousVertex();
////								} else {
////									lookAtNextVertex();
////								}
////								
////							} else {
////								//leaving board
////								
////								if (car.driver.overallPos.combo < car.driver.toolOrigExitingVertexPos.combo) {
////									lookAtNextVertex();
////								} else {
////									lookAtPreviousVertex();
////								}
////								
////							}
//							
//						}
						
					} else {
						assert gpos instanceof RushHourBoardPosition;
						assert floorAndCeilWithinGrid((RushHourBoardPosition)gpos);
						
						GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
						GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
						GraphPositionPathPosition tmpRoundPos = car.driver.overallPos.round(car.length/2);
						
						if (car.driver.prevOverallPos == null) {
							car.driver.toolCoastingGoal = tmpRoundPos;
						} else if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
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
				
			}
			
			int moves = car.driver.overallPos.movesDistance(car.driver.toolCoastingGoal);
			world.curLevel.userMoves += moves;
			
			APP.debuggerScreen.contentPane.repaint();
			
		} finally {
			dragVector = null;
		}
	}
	
//	void lookAtPreviousVertex() {
//		
//		GraphPosition gpos = car.driver.overallPos.gp;
//		
//		boolean road = gpos instanceof RoadPosition;
//		boolean vertex = gpos instanceof VertexPosition;
//		
////		int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
////		boolean otherSideIsFree = true;
//		
//		/*
//		 * overallPos is less than exiting vertex, so the other vertex must be less than overallPos
//		 */
//		
//		int prevVertexIndex = road ? car.driver.overallPos.prevVertexIndex() : (vertex ? car.driver.overallPos.index : car.driver.toolOrigExitingVertexPos.prevVertexIndex());
//		GraphPositionPathPosition vPos = new GraphPositionPathPosition(car.driver.overallPath, prevVertexIndex, 0.0);
//		
//		determineCoasting(car.driver.toolOrigExitingVertexPos, vPos, false);
//	}
	
//	void lookAtNextVertex() {
//		
//		GraphPosition gpos = car.driver.overallPos.gp;
//		
//		boolean road = gpos instanceof RoadPosition;
//		boolean vertex = gpos instanceof VertexPosition;
//		
////		int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
////		boolean otherSideIsFree = true;
//		
//		/*
//		 * overallPos is greater than exiting vertex, so the other vertex must be greater than overallPos
//		 */
//				
//		int nextVertexIndex = road ? car.driver.overallPos.nextVertexIndex() : (vertex ? car.driver.overallPos.index : car.driver.toolOrigExitingVertexPos.nextVertexIndex());
//		GraphPositionPathPosition vPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//		
//		determineCoasting(car.driver.toolOrigExitingVertexPos, vPos, true);
//	}
	
//	void lookAtOrigExitingVertex(boolean forward) {
//		
//		determineCoasting(other, car.driver.toolOrigExitingVertexPos, forward);
//	}
	
	void determineCoasting(GraphPositionPathPosition origVertexPos, GraphPositionPathPosition otherVertexPos, boolean tryOtherSideFirst) {
		assert !origVertexPos.equals(otherVertexPos);
		assert origVertexPos.gp instanceof VertexPosition;
		assert otherVertexPos.gp instanceof VertexPosition;
		
//		boolean road = gpos instanceof RoadPosition;
//		boolean vertex = gpos instanceof VertexPosition;
		
		boolean otherIsForward = otherVertexPos.combo > origVertexPos.combo;
		
		int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / RushHourStud.SIZE);
		boolean otherSideIsFree = true;
		boolean origSideIsFree = true;
		
		/*
		 * overallPos is greater than exiting vertex, so the other vertex must be greater than overallPos
		 */
				
//		int nextVertexIndex = road ? car.driver.overallPos.nextVertexIndex() : (vertex ? car.driver.overallPos.index : car.driver.toolOrigExitingVertexPos.nextVertexIndex());
//		assert vertexIndex != -1;
		
		/*
		 * find thisSideIsFree
		 */
		Fixture origVertex = (Fixture)origVertexPos.gp.entity;
		if (origVertex.getFacingSide().isRightOrBottom()) {
			/*
			 * vertex on top or left
			 */
			for (int i = 0; i < studCount; i++) {
				RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(otherIsForward ? origVertexPos.index-1-i : origVertexPos.index+1+i);
				RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
				if (!stud.isFree(car)) {
					origSideIsFree = false;
					break;
				}
			}
			
		} else {
			
			for (int i = 0; i < studCount; i++) {
				RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(otherIsForward ? origVertexPos.index-2-i : origVertexPos.index+2+i);
				RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
				if (!stud.isFree(car)) {
					origSideIsFree = false;
					break;
				}
			}
		}
		
		/*
		 * find otherSideIsFree
		 */
		if (otherVertexPos.isEndOfPath()) {
			/*
			 * going through exit, so "other side is free"
			 */
			
		} else {
			
			Fixture otherVertex = (Fixture)otherVertexPos.gp.entity;
			if (otherVertex.getFacingSide().isRightOrBottom()) {
				/*
				 * vertex on top or left
				 */
				for (int i = 0; i < studCount; i++) {
					RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(otherIsForward ? otherVertexPos.index+1+i : otherVertexPos.index-1-i);
					RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
					if (!stud.isFree(car)) {
						otherSideIsFree = false;
						break;
					}
				}
				
			} else {
				
				for (int i = 0; i < studCount; i++) {
					RushHourBoardPosition bpos = (RushHourBoardPosition)car.driver.overallPath.get(otherIsForward ? otherVertexPos.index+2+i : otherVertexPos.index-2-i);
					RushHourStud stud = ((RushHourBoard)bpos.entity).stud(bpos);
					if (!stud.isFree(car)) {
						otherSideIsFree = false;
						break;
					}
				}
			}
		}
		
		if (tryOtherSideFirst ? otherSideIsFree : !origSideIsFree) {
			assert otherSideIsFree;
			/*
			 * go to other vertex
			 */
			
			if (otherIsForward) {
				if (!otherVertexPos.isEndOfPath()) {
					car.driver.toolCoastingGoal = otherVertexPos.travelForward(RushHourStud.SIZE + 0.5 * studCount);
				} else {
					/*
					 * going through exit
					 */
					car.driver.toolCoastingGoal = otherVertexPos;
				}
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
			} else {
				car.driver.toolCoastingGoal = otherVertexPos.travelBackward(RushHourStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
			}
			
		} else {
			assert origSideIsFree;
			/*
			 * go to orig vertex
			 */
			
			if (otherIsForward) {
				car.driver.toolCoastingGoal = origVertexPos.travelBackward(RushHourStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
			} else {
				car.driver.toolCoastingGoal = origVertexPos.travelForward(RushHourStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
			}
			
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
		if (curDragP.equals(prevDragP)) {
			return;
		}
		
		curDragMillis = System.currentTimeMillis();
		if (curDragMillis == prevDragMillis) {
			return;
		}
		
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
		
		if (car.state != CarStateEnum.DRAGGING) {
			return;
		}
		
		/*
		 * the 10.0 is a heuristic
		 * should be long enough to allow a flick to go from one side of the board to the other,
		 * but short enough to not have any path intersection problems, nor have any teleporting problems
		 * 
		 */
		GraphPositionPathPosition attemptedPos = car.driver.overallPos.generalSearch(carPTmp, 10.0);
		
		GraphPositionPathPosition actualPos = car.driver.overallPos.furthestAllowablePosition(car, attemptedPos);
		
		if (actualPos.equals(car.driver.overallPos)) {
			return;
		}
		
		car.setTransform(actualPos.p, actualPos.angle());
		car.setPhysicsTransform();
		
		car.driver.setOverallPos(actualPos);
		
		GraphPosition gpos = car.driver.overallPos.gp;
		GraphPosition prevGPos = car.driver.prevOverallPos.gp;
		
		if (gpos instanceof RoadPosition) {	
			
			if (prevGPos instanceof RushHourBoardPosition) {
				
				RushHourBoardPosition rpos = (RushHourBoardPosition)prevGPos;
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
					
				} else {
					
					car.driver.toolOrigExitingVertexPos = vertexPos;
//					System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//					if (car.driver.toolOrigExitingVertexPos == null) {
//						
//						car.driver.toolOrigExitingVertexPos = vertexPos;
//						System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//					}
					
				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
//				System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//				if (car.driver.toolOrigExitingVertexPos == null) {
//					
//					car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
//					System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//				}
				
			}
			
		} else if (gpos instanceof VertexPosition) {
			
			if (prevGPos instanceof RushHourBoardPosition) {
				
				RushHourBoardPosition rpos = (RushHourBoardPosition)prevGPos;
				RushHourBoard b = (RushHourBoard)rpos.entity;
				
				if (car.driver.overallPos.gp.entity == b.exitVertex && !world.isWon) {
					
					winner();
					
				} else {
					
					car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
//					System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//					if (car.driver.toolOrigExitingVertexPos == null) {
//						
//						car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
//						System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//					}
					
				}
			}
			
		} else {
			assert gpos instanceof RushHourBoardPosition;
			
			if (prevGPos instanceof RoadPosition) {
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
				
				car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//				System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//				if (car.driver.toolOrigExitingVertexPos == null) {
//					
//					int nextVertexIndex;
//					if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
//						nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.prevOverallPos.index, car.driver.prevOverallPos.param);
//						assert nextVertexIndex != -1;
//						assert car.driver.prevOverallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.overallPos.combo;
//					} else {
//						nextVertexIndex = car.driver.overallPath.nextVertexIndex(car.driver.overallPos.index, car.driver.overallPos.param);
//						assert nextVertexIndex != -1;
//						assert car.driver.overallPos.combo < nextVertexIndex && nextVertexIndex < car.driver.prevOverallPos.combo;
//					}
//					
//					car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
//					System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				
			} else if (prevGPos instanceof RushHourBoardPosition && !floorAndCeilWithinGrid((RushHourBoardPosition)gpos)) {
				
				RushHourBoardPosition bpos = (RushHourBoardPosition)gpos;
				RushHourBoardPosition prevBpos = (RushHourBoardPosition)prevGPos;
				RushHourBoard b = (RushHourBoard)bpos.entity;
				
				if (bpos.equals(prevBpos)) {
					
				} else if (b.enteringBoard(prevBpos, bpos)) {
					
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
						
					} else {
						
						car.driver.toolOrigExitingVertexPos = vertexPos;
//						System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//						if (car.driver.toolOrigExitingVertexPos == null) {
//							
//							car.driver.toolOrigExitingVertexPos = vertexPos;
//							System.out.println("exiting vertex: " + car.driver.toolOrigExitingVertexPos.gp);
//						}
						
					}
				}
				
			}
			
		}
		
		world.handleZooming(car);
		
		APP.appScreen.contentPane.repaint();
		
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
	
	void winner() {
		
		RushHourWorld world = (RushHourWorld)APP.model;
		
		world.isWon = true;
		
		released(null);
		
		WinnerMenu.action();
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
