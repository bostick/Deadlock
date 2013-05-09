package com.gutabi.bypass.level;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.InputEvent;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.cars.CarStateEnum;
import com.gutabi.capsloc.world.graph.BypassBoard;
import com.gutabi.capsloc.world.graph.BypassBoardPosition;
import com.gutabi.capsloc.world.graph.BypassStud;
import com.gutabi.capsloc.world.graph.GraphPosition;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.RoadPosition;
import com.gutabi.capsloc.world.graph.VertexPosition;
import com.gutabi.capsloc.world.tools.WorldToolBase;

public class BypassCarTool extends WorldToolBase {
	
	public BypassCar car;
	
	public BypassCarTool() {
		
	}
	
	public void setPoint(Point p) {
		
	}
	
	public Object getShape() {
		return null;
	}
	
	public void escKey() {
		
		APP.platform.finishAction();
	}
	
	
	
	Point lastPressP;
	Point lastPanelPressP;
	
	public void pressed(InputEvent ignore) {
		super.pressed(ignore);
		
		BypassWorld world = (BypassWorld)APP.model;
		
		lastPressP = world.lastPressedWorldPoint;
		lastPanelPressP = ignore.p;
		
		BypassCar pressed = (BypassCar)world.carMap.carHitTest(world.lastPressedWorldPoint);
		if (pressed == null) {
			return;
		}
		
		if (car != null && pressed != car && car.state != CarStateEnum.IDLE) {
			return;
		}
		
		switch (pressed.state) {
		case IDLE: {
			
			car = pressed;
			
			car.toolOrigCenter = car.center;
			car.shape.copy(car.toolOrigShape);
			
			car.driver.toolOrigOverallPos = car.driver.overallPos;
			
			car.state = CarStateEnum.DRAGGING;
			
			break;
		}
		case DRAGGING:
			/*
			 * double click?
			 */
			break;
		case COASTING_FORWARD:
		case COASTING_BACKWARD:
			
			if (pressed == car) {
				car.toolOrigCenter = car.center;
				car.shape.copy(car.toolOrigShape);
				
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
		
		BypassWorld world = (BypassWorld)APP.model;
		BypassBoard b = world.bypassBoard;
		
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
			
			if (world.curLevel.isWon) {
				
				car.driver.toolCoastingGoal = car.driver.overallPath.endPos;
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
			
			} else {
				
				GraphPosition gpos = car.driver.overallPos.gp;
				GraphPosition prevGPos = car.driver.prevOverallPos.gp;
				
				if (gpos instanceof RoadPosition) {
					assert car.driver.toolLastExitingVertexPos != null;
					
					if (car.driver.overallPos.combo < car.driver.prevOverallPos.combo) {
						determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.overallPos.prevVertexPosition(), true);
					} else {
						determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.overallPos.nextVertexPosition(), true);
					}
					
				} else if (gpos instanceof VertexPosition) {
					assert car.driver.toolLastExitingVertexPos != null;
					
					if (car.driver.overallPos.combo == car.driver.toolLastExitingVertexPos.combo) {
						
						if (car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RoadPosition) {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
						} else {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
						}
						
					} else {
						assert false;
					}
					
				} else {
					assert gpos instanceof BypassBoardPosition;
					
					BypassBoardPosition bpos = (BypassBoardPosition)gpos;
					
					if (prevGPos instanceof RoadPosition) {
						
						if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
						} else {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
						}
						
					} else if (prevGPos instanceof VertexPosition) {
						
						if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
						} else {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
						}
						
					} else if (prevGPos instanceof BypassBoardPosition) {
						
						if (!b.floorAndCeilWithinGrid(car)) {
							assert car.driver.toolLastExitingVertexPos != null;
							
							BypassBoardPosition prevBpos = (BypassBoardPosition)prevGPos;
							
							if (bpos.equals(prevBpos)) {
								// arbitrarily choose leaving
								
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
								} else {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
								}
								
							} else if (b.enteringBoard(prevBpos, bpos)) {
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), false);
								} else {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), false);
								}
							} else {
								// leaving
								
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
								} else {
									determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
								}
							}
							
						} else {
							
							GraphPositionPathPosition tmpFloorPos = car.driver.overallPos.floor(car.length/2);
							GraphPositionPathPosition tmpCeilPos = car.driver.overallPos.ceil(car.length/2);
							GraphPositionPathPosition tmpRoundPos = car.driver.overallPos.round(car.length/2);
							
							if (car.driver.overallPos.combo - tmpFloorPos.combo < 0.15) {
								car.driver.toolCoastingGoal = tmpFloorPos;
							} else if (tmpCeilPos.combo - car.driver.overallPos.combo < 0.15) {
								car.driver.toolCoastingGoal = tmpCeilPos;
							} else {
								if (car.driver.prevOverallPos == null) {
									car.driver.toolCoastingGoal = tmpRoundPos;
								} else if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
									car.driver.toolCoastingGoal = tmpCeilPos;
								} else {
									car.driver.toolCoastingGoal = tmpFloorPos;
								}
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
				
			}
			
			int moves = car.driver.toolOrigOverallPos.movesDistance(car.driver.toolCoastingGoal, car);
			world.curLevel.userMoves += moves;
			
		} finally {
			dragVector = null;
			
			lastPanelDragP = null;
			penPanelDragP = null;
			
		}
	}
	
	void determineCoasting(GraphPositionPathPosition origVertexPos, GraphPositionPathPosition otherVertexPos, boolean tryOtherSideFirst) {
		assert origVertexPos.gp instanceof VertexPosition;
		assert otherVertexPos.gp instanceof VertexPosition;
		
		boolean otherIsForward = otherVertexPos.combo > car.driver.overallPos.combo;
		
		int studCount = (int)(car.length * Car.METERS_PER_CARLENGTH / BypassStud.SIZE);
		
		if (tryOtherSideFirst) {
			/*
			 * go to other vertex
			 */
			
			if (otherIsForward) {
				if (!otherVertexPos.isEndOfPath()) {
					car.driver.toolCoastingGoal = otherVertexPos.travelForward(BypassStud.SIZE + 0.5 * studCount);
				} else {
					/*
					 * going through exit
					 */
					car.driver.toolCoastingGoal = otherVertexPos;
				}
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
				
			} else {
				car.driver.toolCoastingGoal = otherVertexPos.travelBackward(BypassStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
				
			}
			
		} else {
			/*
			 * go to orig vertex
			 */
			
			if (otherIsForward) {
				car.driver.toolCoastingGoal = origVertexPos.travelBackward(BypassStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
				
			} else {
				car.driver.toolCoastingGoal = origVertexPos.travelForward(BypassStud.SIZE + 0.5 * studCount);
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
	
	Point lastPanelDragP;
	Point penPanelDragP;
	
	/**
	 * setting dynamic properties is done here
	 */
	public void dragged(InputEvent ignore) {
		
		BypassWorld world = (BypassWorld)APP.model;
		BypassBoard b = world.bypassBoard;
		
		super.dragged(ignore);
		
		penPanelDragP = lastPanelDragP;
		lastPanelDragP = ignore.p;
		
		prevDragP = curDragP;
		prevDragMillis = curDragMillis;
		curDragP = world.lastDraggedWorldPoint;
		curDragMillis = APP.platform.monotonicClockMillis();
		
		if (prevDragP != null) {
			dragVector = curDragP.minus(prevDragP);
			dragTimeStepMillis = curDragMillis - prevDragMillis;
		}
		
		if (curDragP.equals(prevDragP)) {
			return;
		}
		
		if (curDragMillis == prevDragMillis) {
			return;
		}
		
		if (car == null) {
			return;
		}
		if (!car.toolOrigShape.hitTest(world.lastPressedWorldPoint)) {
			return;
		}
		
		if (car.state != CarStateEnum.DRAGGING) {
			return;
		}
		
		Point diff;
		if (penPanelDragP != null) {
			diff = lastPanelDragP.minus(penPanelDragP).multiply(1/world.worldCamera.pixelsPerMeter);
		} else {
			diff = lastPanelDragP.minus(lastPanelPressP).multiply(1/world.worldCamera.pixelsPerMeter);
		}
		
		Point carPTmp = car.center.plus(diff);
		
		/*
		 * the 10.0 is a heuristic
		 * should be long enough to allow a flick to go from one side of the board to the other,
		 * but short enough to not have any path intersection problems, nor have any teleporting problems
		 * 
		 */
		GraphPositionPathPosition attemptedPos = car.driver.overallPos.generalSearch(carPTmp, 1.0);
		
		GraphPositionPathPosition actualPos = car.driver.overallPos.furthestAllowablePosition(car, attemptedPos);
		
		if (actualPos.equals(car.driver.overallPos)) {
			return;
		}
		
		car.setTransform(actualPos.p, actualPos.angle);
		car.setPhysicsTransform();
		
		car.driver.setOverallPos(actualPos);
		
		GraphPosition gpos = car.driver.overallPos.gp;
		GraphPosition prevGPos = car.driver.prevOverallPos.gp;
		
		if (gpos instanceof RoadPosition) {	
			
			if (prevGPos instanceof BypassBoardPosition) {
				
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
				
				car.driver.toolLastExitingVertexPos = vertexPos;
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = vertexPos;
				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				car.driver.toolLastExitingVertexPos = car.driver.prevOverallPos;
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
				}
				
			}
			
		} else if (gpos instanceof VertexPosition) {
			
			car.driver.toolLastExitingVertexPos = car.driver.overallPos;
			if (car.driver.toolOrigExitingVertexPos == null) {
				car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
			}
			
		} else {
			assert gpos instanceof BypassBoardPosition;
			
			BypassBoardPosition bpos = (BypassBoardPosition)gpos;
			
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
				
				car.driver.toolLastExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				
				
			} else if (prevGPos instanceof BypassBoardPosition && !b.floorAndCeilWithinGrid(car)) {
				
				BypassBoardPosition prevBpos = (BypassBoardPosition)prevGPos;
				
				if (bpos.equals(prevBpos)) {
					
				} else if (b.enteringBoard(prevBpos, bpos)) {
					
					int nextVertexIndex;
					if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
						nextVertexIndex = car.driver.overallPos.prevVertexIndex();
						assert nextVertexIndex != -1;
					} else {
						nextVertexIndex = car.driver.prevOverallPos.nextVertexIndex();
						assert nextVertexIndex != -1;
					}
					GraphPositionPathPosition vertexPos = new GraphPositionPathPosition(car.driver.overallPath, nextVertexIndex, 0.0);
					
					car.driver.toolLastExitingVertexPos = vertexPos;
					if (car.driver.toolOrigExitingVertexPos == null) {
						car.driver.toolOrigExitingVertexPos = vertexPos;
					}
					
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
					
					car.driver.toolLastExitingVertexPos = vertexPos;
					if (car.driver.toolOrigExitingVertexPos == null) {
						car.driver.toolOrigExitingVertexPos = vertexPos;
					}
					
				}
				
			}
			
		}

		world.handlePanning(car, car.center);
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
