package com.brentonbostick.bypass.level;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.math.DMath;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.InputEvent;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.cars.CarStateEnum;
import com.brentonbostick.capsloc.world.graph.BypassBoard;
import com.brentonbostick.capsloc.world.graph.BypassBoardPosition;
import com.brentonbostick.capsloc.world.graph.BypassStud;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.RoadPosition;
import com.brentonbostick.capsloc.world.graph.VertexPosition;
import com.brentonbostick.capsloc.world.graph.gpp.MutableGPPP;
import com.brentonbostick.capsloc.world.tools.WorldToolBase;

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
			
			car.driver.toolOrigOverallPos.set(car.driver.overallPos);
			
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
	
	static final MutableGPPP nextVertexPos = new MutableGPPP();
	static final MutableGPPP prevVertexPos = new MutableGPPP();
	static final MutableGPPP tmpFloorPos = new MutableGPPP();
	static final MutableGPPP tmpCeilPos = new MutableGPPP();
	static final MutableGPPP tmpRoundPos = new MutableGPPP();
	static final MutableGPPP attemptedPos = new MutableGPPP();
	static final MutableGPPP actualPos = new MutableGPPP();
	static final MutableGPPP newFrontPos = new MutableGPPP();
	
	private void releasedOrCanceled() {
		
		BypassWorld world = (BypassWorld)APP.model;
		BypassBoard b = world.bypassBoard;
		
//		for (int i = 0; i < world.quadrantMap.grassMap.grass.size(); i++) {
//			AnimatedGrass g = world.quadrantMap.grassMap.grass.get(i);
//			if (g.aabb.hitTest(world.lastPressedWorldPoint)) {
//				
//				if (g.aabb.hitTest(world.lastReleasedWorldPoint)) {
//					
//					g.xor = !g.xor;
//					
//				}
//				
//				return;
//			}
//		}
		
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
			if (car.driver.prevOverallPos.isCleared()) {
				/*
				 * nothing dragged, so this is only press, release
				 */
				
				car.state = CarStateEnum.IDLE;
				
				return;
			}
			
			
			if (world.curLevel.isWon) {
				
				car.driver.toolCoastingGoal.set(car.driver.overallPath.endPos);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
			
			} else {
				
				GraphPosition gpos = car.driver.overallPos.gp;
				GraphPosition prevGPos = car.driver.prevOverallPos.gp;
				
				if (gpos instanceof RoadPosition) {
					assert car.driver.toolLastExitingVertexPos != null;
					
					if (car.driver.overallPos.combo < car.driver.prevOverallPos.combo) {
						prevVertexPos.set(car.driver.overallPos);
						prevVertexPos.prevVertexPosition();
						determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
					} else {
						nextVertexPos.set(car.driver.overallPos);
						nextVertexPos.nextVertexPosition();
						determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
					}
					
				} else if (gpos instanceof VertexPosition) {
					assert car.driver.toolLastExitingVertexPos != null;
					
					if (car.driver.overallPos.combo == car.driver.toolLastExitingVertexPos.combo) {
						
						if (car.driver.overallPath.get(car.driver.overallPos.index+1) instanceof RoadPosition) {
							nextVertexPos.set(car.driver.toolLastExitingVertexPos);
							nextVertexPos.nextVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
						} else {
							prevVertexPos.set(car.driver.toolLastExitingVertexPos);
							prevVertexPos.prevVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
						}
						
					} else {
						throw new AssertionError();
					}
					
				} else {
					assert gpos instanceof BypassBoardPosition;
					
					BypassBoardPosition bpos = (BypassBoardPosition)gpos;
					
					if (prevGPos instanceof RoadPosition) {
						
						if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
							nextVertexPos.set(car.driver.toolLastExitingVertexPos);
							nextVertexPos.nextVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
						} else {
							prevVertexPos.set(car.driver.toolLastExitingVertexPos);
							prevVertexPos.prevVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
						}
						
					} else if (prevGPos instanceof VertexPosition) {
						
						if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
							nextVertexPos.set(car.driver.toolLastExitingVertexPos);
							nextVertexPos.nextVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
						} else {
							prevVertexPos.set(car.driver.toolLastExitingVertexPos);
							prevVertexPos.prevVertexPosition();
							determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
						}
						
					} else if (prevGPos instanceof BypassBoardPosition) {
						
						if (!b.floorAndCeilWithinGrid(car)) {
							assert car.driver.toolLastExitingVertexPos != null;
							
							BypassBoardPosition prevBpos = (BypassBoardPosition)prevGPos;
							
							if (bpos.equals(prevBpos)) {
								// arbitrarily choose leaving
								
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									nextVertexPos.set(car.driver.toolLastExitingVertexPos);
									nextVertexPos.nextVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
								} else {
									prevVertexPos.set(car.driver.toolLastExitingVertexPos);
									prevVertexPos.prevVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
								}
								
							} else if (b.enteringBoard(prevBpos, bpos)) {
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									nextVertexPos.set(car.driver.toolLastExitingVertexPos);
									nextVertexPos.nextVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, false);
								} else {
									prevVertexPos.set(car.driver.toolLastExitingVertexPos);
									prevVertexPos.prevVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, false);
								}
							} else {
								// leaving
								
								if (car.driver.overallPos.combo < car.driver.toolLastExitingVertexPos.combo) {
									nextVertexPos.set(car.driver.toolLastExitingVertexPos);
									nextVertexPos.nextVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, nextVertexPos, true);
								} else {
									prevVertexPos.set(car.driver.toolLastExitingVertexPos);
									prevVertexPos.prevVertexPosition();
									determineCoasting(car.driver.toolLastExitingVertexPos, prevVertexPos, true);
								}
							}
							
						} else {
							
							tmpFloorPos.set(car.driver.overallPos);
							tmpFloorPos.floor(car.length/2);
							
							tmpCeilPos.set(car.driver.overallPos);
							tmpCeilPos.ceil(car.length/2);
							
							tmpRoundPos.set(car.driver.overallPos);
							tmpRoundPos.round(car.length/2);
							
							if (car.driver.overallPos.combo - tmpFloorPos.combo < 0.15) {
								car.driver.toolCoastingGoal.set(tmpFloorPos);
							} else if (tmpCeilPos.combo - car.driver.overallPos.combo < 0.15) {
								car.driver.toolCoastingGoal.set(tmpCeilPos);
							} else {
								if (car.driver.prevOverallPos.isCleared()) {
									car.driver.toolCoastingGoal.set(tmpRoundPos);
								} else if (car.driver.prevOverallPos.combo < car.driver.overallPos.combo) {
									car.driver.toolCoastingGoal.set(tmpCeilPos);
								} else {
									car.driver.toolCoastingGoal.set(tmpFloorPos);
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
	
	public void released(InputEvent ignore) {
		super.released(ignore);
		
		releasedOrCanceled();
	}
	
	public void canceled(InputEvent ev) {
		super.canceled(ev);
		
		releasedOrCanceled();
	}
	
	void determineCoasting(MutableGPPP origVertexPos, MutableGPPP otherVertexPos, boolean tryOtherSideFirst) {
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
					car.driver.toolCoastingGoal.set(otherVertexPos);
					car.driver.toolCoastingGoal.travelForward(BypassStud.SIZE + 0.5 * studCount);
				
				} else {
					/*
					 * going through exit
					 */
					car.driver.toolCoastingGoal.set(otherVertexPos);
				}
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, true);
				car.state = CarStateEnum.COASTING_FORWARD;
				
			} else {
				car.driver.toolCoastingGoal.set(otherVertexPos);
				car.driver.toolCoastingGoal.travelBackward(BypassStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
				
			}
			
		} else {
			/*
			 * go to orig vertex
			 */
			
			if (otherIsForward) {
				car.driver.toolCoastingGoal.set(origVertexPos);
				car.driver.toolCoastingGoal.travelBackward(BypassStud.SIZE + 0.5 * studCount);
				car.setCoastingVelFromDrag(dragVector, dragTimeStepMillis, false);
				car.state = CarStateEnum.COASTING_BACKWARD;
				
			} else {
				car.driver.toolCoastingGoal.set(origVertexPos);
				car.driver.toolCoastingGoal.travelForward(BypassStud.SIZE + 0.5 * studCount);
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
		attemptedPos.set(car.driver.overallPos);
		attemptedPos.generalSearch(carPTmp, 1.0, car.driver.acc);
		
		actualPos.set(car.driver.overallPos);
		actualPos.furthestAllowablePosition(car, attemptedPos);
		
		if (actualPos.equals(car.driver.overallPos)) {
			return;
		}
		
		newFrontPos.set(actualPos);
		if (actualPos.combo > car.driver.overallPos.combo) {
			newFrontPos.travelForward(car.length / 2);
		} else {
			newFrontPos.travelBackward(car.length / 2);
		}
		double a = Math.atan2(newFrontPos.p.y - actualPos.p.y, newFrontPos.p.x - actualPos.p.x);
		
		car.setTransform(actualPos.p, a);
		car.setPhysicsTransform();
		
		car.driver.prevOverallPos.set(car.driver.overallPos);
		car.driver.overallPos.set(actualPos);
		
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
				
				car.driver.toolLastExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
				if (car.driver.toolOrigExitingVertexPos.isCleared()) {
					car.driver.toolOrigExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
				}
				
			} else if (prevGPos instanceof VertexPosition) {
				
				car.driver.toolLastExitingVertexPos.set(car.driver.prevOverallPos);
				if (car.driver.toolOrigExitingVertexPos.isCleared()) {
					car.driver.toolOrigExitingVertexPos.set(car.driver.prevOverallPos);
				}
				
			}
			
		} else if (gpos instanceof VertexPosition) {
			
			car.driver.toolLastExitingVertexPos.set(car.driver.overallPos);
			if (car.driver.toolOrigExitingVertexPos.isCleared()) {
				car.driver.toolOrigExitingVertexPos.set(car.driver.overallPos);
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
				
				car.driver.toolLastExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
				if (car.driver.toolOrigExitingVertexPos.isCleared()) {
					car.driver.toolOrigExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
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
					
					car.driver.toolLastExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
					if (car.driver.toolOrigExitingVertexPos.isCleared()) {
						car.driver.toolOrigExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
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
					
					car.driver.toolLastExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
					if (car.driver.toolOrigExitingVertexPos.isCleared()) {
						car.driver.toolOrigExitingVertexPos.set(car.driver.overallPath, nextVertexIndex, 0.0);
					}
					
				}
				
			}
			
		}

		world.handlePanning(car, car.center);
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}
	
}
