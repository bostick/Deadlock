package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import javax.swing.JFrame;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Direction;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class RegularCursor extends CursorBase {
	
	enum RegularCursorMode {
		FREE,
		DRAFTING
	}
	
	RegularCursorMode mode;
	
	Circle shape;
	
	Stroke stroke;
	Stroke debugStroke;
	Stroke debugStroke2;
	
	public RegularCursor(WorldScreen screen) {
		super(screen);
		
		mode = RegularCursorMode.FREE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new Circle(null, p, Vertex.INIT_VERTEX_RADIUS);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	
	public void escKey(InputEvent ev) {
		
		VIEW.teardownCanvasAndControlPanel(VIEW.container);
		
		APP.screen = new MainMenu();
		APP.screen.init();
		
		VIEW.setupCanvas(VIEW.container);
		((JFrame)VIEW.container).setVisible(true);
		
		VIEW.canvas.canvasPostDisplay();
		
		APP.screen.render();
		APP.screen.repaint();
		
	}
	
	public void d1Key(InputEvent ev) {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				screen.render();
				screen.repaint();
				
			} else if (screen.hilited instanceof Fixture) {
				Fixture f = (Fixture)screen.hilited;
				
				Fixture g = f.match;
				
				if (f.getType() != null) {
					f.setType(f.getType().other());
				}
				if (g.getType() != null) {
					g.setType(g.getType().other());
				}
				
				f.setSide(f.getSide().other());
				g.setSide(g.getSide().other());
				
				screen.render();
				screen.repaint();
				
			}
			
		}
	}
	
	public void d2Key(InputEvent ev) {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				screen.render();
				screen.repaint();
				
			}
			
		}
	}
	
	public void d3Key(InputEvent ev) {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, null);
			
				screen.render();
				screen.repaint();
				
			}
			
		}
	}

	
	public void qKey(InputEvent ev) {
		StraightEdgeCursor c = new StraightEdgeCursor(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.cursor = c;
		screen.repaint();
	}
	
	public void wKey(InputEvent ev) {
		screen.cursor = new FixtureCursor(screen);
		screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.repaint();
	}
	
	public void aKey(InputEvent ev) {
		
		screen.hilited = null;
		
		screen.cursor = new CircleCursor(screen);
		
		screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.repaint();
	}
	
	public void sKey(InputEvent ev) {
		QuadCursor q = new QuadCursor(screen);
		q.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		q.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.cursor = q;
		screen.repaint();
	}
	
	public void dKey(InputEvent ev) {
		CubicCursor c = new CubicCursor(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.cursor = c;
		screen.repaint();
	}
	
	public void insertKey(InputEvent ev) {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof StopSign) {
				StopSign s = (StopSign)screen.hilited;
				
				s.setEnabled(true);
				
				screen.render();
				screen.repaint();
			}
			
		} else {
			
			screen.hilited = null;
			
			screen.cursor = new MergerCursor(screen);
			
			screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
			screen.repaint();
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = screen.world.hitTest(screen.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				screen.hilited = closest;
			}
			
			screen.cursor.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
			screen.repaint();
			break;
		case DRAFTING:
			assert false;
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		switch (mode) {
		case FREE:
			screen.cursor.setPoint(screen.lastDraggedWorldPoint);
			
			if (screen.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(screen.lastPressedWorldPoint);
				draftMove(screen.lastDraggedWorldPoint);
				
				screen.repaint();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			screen.cursor.setPoint(screen.lastDraggedWorldPoint);
			
			draftMove(screen.lastDraggedWorldPoint);
			
			screen.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			screen.render();
			screen.repaint();
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		screen.cursor.setPoint(null);
		screen.repaint();
	}
	
	public void draftStart(Point p) {
		
		mode = RegularCursorMode.DRAFTING;
		
		screen.hilited = null;
		
		stroke = new Stroke(screen.world);
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		
		stroke.finish();
		
		stroke.processNewStroke();
		
		assert screen.world.checkConsistency();
		
		debugStroke2 = debugStroke;
		debugStroke = stroke;
		stroke = null;
		
		mode = RegularCursorMode.FREE;
		
	}
	
	public void draw(RenderingContext ctxt) {
		
		if (p == null) {
			return;
		}
		
		if (stroke != null) {
			stroke.paint(ctxt);
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
		shape.draw(ctxt);
		
	}
	
}
