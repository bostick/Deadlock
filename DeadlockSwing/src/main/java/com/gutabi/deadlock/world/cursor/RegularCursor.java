package com.gutabi.deadlock.world.cursor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;

import javax.swing.JFrame;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.World;
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
	
	public RegularCursor(World world) {
		super(world);
		
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
	
	
	public void escKey() {
		
		try {
			
			VIEW.teardownCanvasAndControlPanel(VIEW.container);
			
			APP.PIXELS_PER_METER = 1.0;
			APP.screen = new MainMenu();
			APP.screen.init();
			
			VIEW.setupCanvas(VIEW.container);
			((JFrame)VIEW.container).setVisible(true);
			VIEW.canvas.requestFocusInWindow();
			
			VIEW.canvas.canvasPostDisplay();
			
			APP.screen.render();
			APP.screen.repaint();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void d1Key() {
		if (world.hilited != null) {
			
			if (world.hilited instanceof Road) {
				Road r = (Road)world.hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				world.render();
				world.repaint();
				
			} else if (world.hilited instanceof Fixture) {
				Fixture f = (Fixture)world.hilited;
				
				Fixture g = f.match;
				
				if (f.getType() != null) {
					f.setType(f.getType().other());
				}
				if (g.getType() != null) {
					g.setType(g.getType().other());
				}
				
				f.setSide(f.getSide().other());
				g.setSide(g.getSide().other());
				
				world.render();
				world.repaint();
				
			}
			
		}
	}
	
	public void d2Key() {
		if (world.hilited != null) {
			
			if (world.hilited instanceof Road) {
				Road r = (Road)world.hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				world.render();
				world.repaint();
				
			}
			
		}
	}
	
	public void d3Key() {
		if (world.hilited != null) {
			
			if (world.hilited instanceof Road) {
				Road r = (Road)world.hilited;
				
				r.setDirection(null, null);
			
				world.render();
				world.repaint();
				
			}
			
		}
	}

	
	public void qKey() {
		StraightEdgeCursor c = new StraightEdgeCursor(world);
		c.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		c.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		world.cursor = c;
		world.repaint();
	}
	
	public void wKey() {
		world.cursor = new FixtureCursor(world);
		world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		world.repaint();
	}
	
	public void aKey() {
		
		world.hilited = null;
		
		world.cursor = new CircleCursor(world);
		
		world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
		world.repaint();
	}
	
	public void sKey() {
		QuadCursor q = new QuadCursor(world);
		q.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		q.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		world.cursor = q;
		world.repaint();
	}
	
	public void dKey() {
		CubicCursor c = new CubicCursor(world);
		c.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		c.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		world.cursor = c;
		world.repaint();
	}
	
	public void insertKey() {
		if (world.hilited != null) {
			
			if (world.hilited instanceof StopSign) {
				StopSign s = (StopSign)world.hilited;
				
				s.setEnabled(true);
				
				world.render();
				world.repaint();
			}
			
		} else {
			
			world.hilited = null;
			
			world.cursor = new MergerCursor(world);
			
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			
			world.repaint();
		}
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = world.hitTest(world.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				world.hilited = closest;
			}
			
			world.cursor.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			
			world.repaint();
			break;
		case DRAFTING:
			assert false;
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		switch (mode) {
		case FREE:
			world.cursor.setPoint(world.lastDraggedWorldPoint);
			
			if (world.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(world.lastPressedWorldPoint);
				draftMove(world.lastDraggedWorldPoint);
				
				world.repaint();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			world.cursor.setPoint(world.lastDraggedWorldPoint);
			
			draftMove(world.lastDraggedWorldPoint);
			
			world.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			world.render();
			world.repaint();
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		world.cursor.setPoint(null);
		world.repaint();
	}
	
	public void draftStart(Point p) {
		
		mode = RegularCursorMode.DRAFTING;
		
		world.hilited = null;
		
		stroke = new Stroke(world);
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		
		stroke.finish();
		
		stroke.processNewStroke();
		
		assert world.checkConsistency();
		
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
