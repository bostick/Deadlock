package com.gutabi.bypass.level;

import static com.gutabi.capsloc.CapslocApplication.APP;
import com.gutabi.capsloc.geom.Shape;
import com.gutabi.capsloc.math.DMath;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.InputEvent;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.cars.CarStateEnum;
import com.gutabi.capsloc.world.graph.BypassBoard;
import com.gutabi.capsloc.world.graph.BypassBoardPosition;
import com.gutabi.capsloc.world.graph.BypassStud;
import com.gutabi.capsloc.world.graph.GraphPosition;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;
import com.gutabi.capsloc.world.graph.RoadPosition;
import com.gutabi.capsloc.world.graph.VertexPosition;
import com.gutabi.capsloc.world.sprites.CarSheet.CarType;
import com.gutabi.capsloc.world.tools.WorldToolBase;

public class BypassCarTool extends WorldToolBase {
	
	public BypassCar car;
	
	public BypassCarTool() {
		
	}
	
	public void setPoint(Point p) {
		
	}
	
	public Shape getShape() {
		return null;
	}
	
	public void escKey() {
		
		APP.platform.finishAction();
	}
	
	public void pressed(InputEvent ignore) {
		super.pressed(ignore);
		
		World world = (World)APP.model;
		
		BypassCar pressed = (BypassCar)world.carMap.carHitTest(world.lastPressedWorldPoint);
		if (pressed == null) {
			return;
		}
		
		if (car != null && pressed != car && car.state != CarStateEnum.IDLE) {
			return;
		}
		
		switch (pressed.state) {
		case IDLE:
			
			car = pressed;
			
			car.toolOrigCenter = car.center;
			car.shape.copy(car.toolOrigShape);
			car.toolOrigPixelOffset = Point.worldToPanel(world.worldCamera.worldViewport.x, world.worldCamera.worldViewport.y, world.worldCamera).minus(Point.worldToPanel(car.center, world.worldCamera));
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
				car.toolOrigCenter = car.center;
				car.shape.copy(car.toolOrigShape);
				car.toolOrigPixelOffset = Point.worldToPanel(world.worldCamera.worldViewport.x, world.worldCamera.worldViewport.y, world.worldCamera).minus(Point.worldToPanel(car.center, world.worldCamera));
				
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
				
				if (gpos instanceof RoadPosition) {
					assert car.driver.toolLastExitingVertexPos != null;
					
					if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
						determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
					} else {
						determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
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
					BypassBoard b = (BypassBoard)bpos.entity;
					
					if (!b.floorAndCeilWithinGrid(car)) {
						assert car.driver.toolLastExitingVertexPos != null;
						
						if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.nextVertexPosition(), true);
						} else {
							determineCoasting(car.driver.toolLastExitingVertexPos, car.driver.toolLastExitingVertexPos.prevVertexPosition(), true);
						}
						
					} else {
						
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
			
			int moves = car.driver.toolOrigOverallPos.movesDistance(car.driver.toolCoastingGoal, car);
			world.curLevel.userMoves += moves;
			
		} finally {
			dragVector = null;
		}
	}
	
	void determineCoasting(GraphPositionPathPosition origVertexPos, GraphPositionPathPosition otherVertexPos, boolean tryOtherSideFirst) {
		assert !origVertexPos.equals(otherVertexPos);
		assert origVertexPos.gp instanceof VertexPosition;
		assert otherVertexPos.gp instanceof VertexPosition;
		
		boolean otherIsForward = otherVertexPos.combo > origVertexPos.combo;
		
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
	
	/**
	 * setting dynamic properties is done here
	 */
	public void dragged(InputEvent ignore) {
		super.dragged(ignore);
		
		BypassWorld world = (BypassWorld)APP.model;
		
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
		
		Point carPTmp = car.toolOrigCenter.plus(diff);
		
		if (car.state != CarStateEnum.DRAGGING) {
			return;
		}
		
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
				
				BypassBoardPosition rpos = (BypassBoardPosition)prevGPos;
				BypassBoard b = (BypassBoard)rpos.entity;
				
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
				
				if (vertexPos.gp.entity == b.exitVertex && !world.curLevel.isWon && car.type == CarType.RED) {
					
					released(null);
					world.winner();
					return;
					
				} else {
					
				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				car.driver.toolLastExitingVertexPos = car.driver.prevOverallPos;
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = car.driver.prevOverallPos;
				}
				
			}
			
		} else if (gpos instanceof VertexPosition) {
			
			if (prevGPos instanceof BypassBoardPosition) {
				
				BypassBoardPosition rpos = (BypassBoardPosition)prevGPos;
				BypassBoard b = (BypassBoard)rpos.entity;
				
				car.driver.toolLastExitingVertexPos = car.driver.overallPos;
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
				}
				
				if (car.driver.overallPos.gp.entity == b.exitVertex && !world.curLevel.isWon && car.type == CarType.RED) {
					
					released(null);
					world.winner();
					return;
					
				} else {
					
				}
			} else {
				
				car.driver.toolLastExitingVertexPos = car.driver.overallPos;
				if (car.driver.toolOrigExitingVertexPos == null) {
					car.driver.toolOrigExitingVertexPos = car.driver.overallPos;
				}
			}
			
		} else {
			assert gpos instanceof BypassBoardPosition;
			
			BypassBoardPosition bpos = (BypassBoardPosition)gpos;
			BypassBoard b = (BypassBoard)bpos.entity;
			
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
					
					if (vertexPos.gp.entity == b.exitVertex && !world.curLevel.isWon && car.type == CarType.RED) {
						
						released(null);
						world.winner();
						return;
						
					} else {
						
					}
				}
				
			}
			
		}
		
		world.handleZooming(car);
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
