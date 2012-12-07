package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.ShapeUtils;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.core.graph.Direction;
import com.gutabi.deadlock.core.graph.EdgePosition;
import com.gutabi.deadlock.core.graph.GraphPosition;
import com.gutabi.deadlock.core.graph.Intersection;
import com.gutabi.deadlock.core.graph.Merger;
import com.gutabi.deadlock.core.graph.Road;
import com.gutabi.deadlock.core.graph.RoadPosition;
import com.gutabi.deadlock.core.graph.Side;
import com.gutabi.deadlock.core.graph.Vertex;
import com.gutabi.deadlock.core.graph.VertexPosition;
import com.gutabi.deadlock.model.Car;
import com.gutabi.deadlock.model.Fixture;
import com.gutabi.deadlock.model.FixtureType;
import com.gutabi.deadlock.model.StopSign;
import com.gutabi.deadlock.model.Stroke;
import com.gutabi.deadlock.model.cursor.FixtureCursor;
import com.gutabi.deadlock.model.cursor.MergerCursor;
import com.gutabi.deadlock.model.cursor.RegularCursor;
import com.gutabi.deadlock.model.cursor.StraightEdgeCursor;

//@SuppressWarnings("serial")
public class KeyboardController implements KeyListener {
	
	public void keyPressed(KeyEvent arg0) {
		
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_INSERT) {
			insertKey();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteKey();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			escKey();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			qKey();
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			wKey();
		} else if (e.getKeyCode() == KeyEvent.VK_G) {
			gKey();
		} else if (e.getKeyCode() == KeyEvent.VK_1) {
			d1Key();
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			d2Key();
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			d3Key();
		} else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS) {
			plusKey();
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			minusKey();
		} else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
			slashKey();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			downKey();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			upKey();
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			enterKey();
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void qKey() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			Entity hit = MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape());
			if (hit == null) {
				
				MODEL.world.addVertexTop(new Intersection(MODEL.cursor.getPoint()));
				
				VIEW.renderWorldBackground();
				VIEW.repaintControlPanel();
				VIEW.repaintCanvas();
				
			} else if (hit instanceof Road || hit instanceof Vertex) {
				
				if (hit instanceof Road) {
					GraphPosition pos = MODEL.world.findClosestRoadPosition(MODEL.cursor.getPoint(), ((Circle)MODEL.cursor.getShape()).radius);
					
					Entity hit2;
					if (pos instanceof EdgePosition) {
						hit2 = MODEL.world.pureGraphBestHitTestCircle(new Circle(null, pos.p, ((Circle)MODEL.cursor.getShape()).radius));
					} else {
						hit2 = ((VertexPosition)pos).v;
					}
					
					if (hit2 instanceof Road) {
						hit = MODEL.world.splitRoadTop((RoadPosition)pos);
						
						assert ShapeUtils.intersectCC((Circle)MODEL.cursor.getShape(), (Circle)hit.getShape());
						
					} else {
						hit = hit2;
					}
				}
				
				CONTROLLER.mode = ControlMode.STRAIGHTEDGECURSOR;
				
				MODEL.cursor = new StraightEdgeCursor((Vertex)hit);
				
				MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
				
				VIEW.repaintCanvas();
				
			}
			
			break;
			
		case STRAIGHTEDGECURSOR:
			
			Point first = ((StraightEdgeCursor)MODEL.cursor).first.p;
			Point second = MODEL.cursor.getPoint();
			
			Stroke s = new Stroke();
			s.add(first);
			s.add(second);
			
			CONTROLLER.processNewStroke(s);
			
			assert MODEL.world.checkConsistency();
			
			VIEW.renderWorldBackground();
			VIEW.repaintControlPanel();
			
			CONTROLLER.mode = ControlMode.IDLE;
			
			MODEL.cursor = new RegularCursor();
			
			MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
			
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void wKey() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			CONTROLLER.mode = ControlMode.FIXTURECURSOR;
			
			MODEL.cursor = new FixtureCursor();
			
			MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
		case FIXTURECURSOR:
			FixtureCursor fc = (FixtureCursor)MODEL.cursor;
			Axis axis = fc.getAxis();
			
			if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
				
				Fixture source = new Fixture(fc.getSourcePoint(), axis);
				Fixture sink = new Fixture(fc.getSinkPoint(), axis);
				
				source.setType(FixtureType.SOURCE);
				sink.setType(FixtureType.SINK);
				
				source.match = sink;
				sink.match = source;
				
				switch (axis) {
				case TOPBOTTOM:
					source.setSide(Side.BOTTOM);
					sink.setSide(Side.BOTTOM);
					break;
				case LEFTRIGHT:
					source.setSide(Side.RIGHT);
					sink.setSide(Side.RIGHT);
					break;
				}
				
				MODEL.world.addVertexTop(source);
				
				MODEL.world.addVertexTop(sink);
				
				CONTROLLER.mode = ControlMode.IDLE;
				
				MODEL.cursor = new RegularCursor();
				
				MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
				
				VIEW.renderWorldBackground();
				VIEW.repaintCanvas();
				VIEW.repaintControlPanel();
				
			}
			
			break;
		case RUNNING:
		case PAUSED:
		case MERGERCURSOR:
		case STRAIGHTEDGECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void gKey() {
		
		MODEL.grid = !MODEL.grid;
		
		VIEW.renderWorldBackground();
		VIEW.repaintCanvas();
		
	}
	
	public void deleteKey() {
		
		if (MODEL.hilited != null) {
			
			if (MODEL.hilited.isUserDeleteable()) {
				
				if (MODEL.hilited instanceof Car) {
					Car c = (Car)MODEL.hilited;
					
					CONTROLLER.removeCarTop(c);
					
				} else if (MODEL.hilited instanceof Vertex) {
					Vertex v = (Vertex)MODEL.hilited;
					
					CONTROLLER.removeVertexTop(v);
					
				} else if (MODEL.hilited instanceof Road) {
					Road e = (Road)MODEL.hilited;
					
					CONTROLLER.removeRoadTop(e);
					
				} else if (MODEL.hilited instanceof Merger) {
					Merger e = (Merger)MODEL.hilited;
					
					CONTROLLER.removeMergerTop(e);
					
				} else if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					CONTROLLER.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				MODEL.hilited = null;
				
			}
			
		}
		
		VIEW.renderWorldBackground();
		VIEW.repaintCanvas();
		VIEW.repaintControlPanel();
		
	}
	
	public void insertKey() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof StopSign) {
					StopSign s = (StopSign)MODEL.hilited;
					
					s.setEnabled(true);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
				}
				
			} else {
				
				CONTROLLER.mode = ControlMode.MERGERCURSOR;
				
				MODEL.cursor = new MergerCursor();
				
				MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
				
				VIEW.repaintCanvas();
				
			}
			
			break;
		case MERGERCURSOR:
			
			if (MODEL.world.completelyContains(MODEL.cursor.getShape())) {
				
				if (MODEL.world.pureGraphBestHitTest(MODEL.cursor.getShape()) == null) {
					
					MODEL.world.insertMergerTop(MODEL.cursor.getPoint());
					
					CONTROLLER.mode = ControlMode.IDLE;
					
					MODEL.cursor = new RegularCursor();
					
					MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
				}
				
			}
			
			break;
		case RUNNING:
		case PAUSED:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
		case DRAFTING:
		case MENU:
			break;
		}
		
	}
	
	public void escKey() {
		
		switch (CONTROLLER.mode) {
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			CONTROLLER.mode = ControlMode.IDLE;
			
			MODEL.cursor = new RegularCursor();
			
			MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
			
			VIEW.repaintCanvas();
			
			break;
		case RUNNING:
		case PAUSED:
		case DRAFTING:
		case IDLE:
		case MENU:
			break;
		}
		
	}
	
	public void d1Key() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(null, Direction.STARTTOEND);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				} else if (MODEL.hilited instanceof Fixture) {
					Fixture f = (Fixture)MODEL.hilited;
					
					Fixture g = f.match;
					
					if (f.getType() != null) {
						f.setType(f.getType().other());
					}
					if (g.getType() != null) {
						g.setType(g.getType().other());
					}
					
					f.setSide(f.getSide().other());
					g.setSide(g.getSide().other());
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void d2Key() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(null, Direction.ENDTOSTART);
					
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}

	public void d3Key() {
		
		switch (CONTROLLER.mode) {
		case IDLE:
			
			if (MODEL.hilited != null) {
				
				if (MODEL.hilited instanceof Road) {
					Road r = (Road)MODEL.hilited;
					
					r.setDirection(null, null);
				
					VIEW.renderWorldBackground();
					VIEW.repaintCanvas();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
	
	public void plusKey() {
		
		VIEW.zoom(1.1);
		
		VIEW.canvas.lastMovedWorldPoint = VIEW.canvasToWorld(VIEW.canvas.lastMovedCanvasPoint);
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
			assert false;
			break;
		case PAUSED:
		case IDLE:
		case RUNNING:
			
			Entity closest = MODEL.world.hitTest(VIEW.canvas.lastMovedWorldPoint);
			
			synchronized (MODEL) {
				MODEL.hilited = closest;
			}
			
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * VIEW.canvas.lastMovedWorldPoint.x), 2 * Math.round(0.5 * VIEW.canvas.lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
				}
			}
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case MENU:
			break;
		}
		
	}
	
	public void minusKey() {
		
		VIEW.zoom(0.9);
		
		VIEW.canvas.lastMovedWorldPoint = VIEW.canvasToWorld(VIEW.canvas.lastMovedCanvasPoint);
		
		switch (CONTROLLER.mode) {
		case DRAFTING:
			assert false;
			break;
		case IDLE:
		case PAUSED:
		case RUNNING:
			
			Entity closest = MODEL.world.hitTest(VIEW.canvas.lastMovedWorldPoint);
			
			synchronized (MODEL) {
				MODEL.hilited = closest;
			}
			
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			
			if (MODEL.cursor != null) {
				if (MODEL.grid) {
					
					Point closestGridPoint = new Point(2 * Math.round(0.5 * VIEW.canvas.lastMovedWorldPoint.x), 2 * Math.round(0.5 * VIEW.canvas.lastMovedWorldPoint.y));
					MODEL.cursor.setPoint(closestGridPoint);
					
				} else {
					MODEL.cursor.setPoint(VIEW.canvas.lastMovedWorldPoint);
				}
			}
			
			VIEW.renderWorldBackground();
			VIEW.repaintCanvas();
			VIEW.repaintControlPanel();
			
			break;
		case MENU:
			break;
		}
		
	}
	
	public void slashKey() {
		
	}
	
	
	public void downKey() {
		
		switch (CONTROLLER.mode) {
		case MENU:
			
			MODEL.menu.downKey();
			
			break;
		case DRAFTING:
		case IDLE:
		case PAUSED:
		case RUNNING:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			break;
		}
		
	}

	public void upKey() {
		
		switch (CONTROLLER.mode) {
		case MENU:
			
			MODEL.menu.upKey();
			
			break;
		case DRAFTING:
		case IDLE:
		case PAUSED:
		case RUNNING:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			break;
		}
		
	}
	
	public void enterKey() {
		
		switch (CONTROLLER.mode) {
		case MENU:
			
			if (MODEL.menu.hilited == null) {
				
			} else {
				
				MODEL.menu.hilited.action();
				
			}
			
			VIEW.repaintCanvas();
			
			break;
		case DRAFTING:
		case IDLE:
		case PAUSED:
		case RUNNING:
		case MERGERCURSOR:
		case FIXTURECURSOR:
		case STRAIGHTEDGECURSOR:
			break;
		}
		
	}
	
}
