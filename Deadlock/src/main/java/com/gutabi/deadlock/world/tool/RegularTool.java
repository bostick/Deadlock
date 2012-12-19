package com.gutabi.deadlock.world.tool;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.util.Set;

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
public class RegularTool extends ToolBase {
	
	enum RegularToolMode {
		FREE,
		DRAFTING
	}
	
	RegularToolMode mode;
	
	Circle shape;
	
	Stroke stroke;
	Stroke debugStroke;
	Stroke debugStroke2;
	
	public RegularTool(WorldScreen screen) {
		super(screen);
		
		mode = RegularToolMode.FREE;
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
		
		VIEW.setupCanvas(VIEW.container);
		((JFrame)VIEW.container).setVisible(true);
		
		APP.screen.postDisplay();
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
		StraightEdgeTool c = new StraightEdgeTool(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
		screen.repaint();
	}
	
	public void wKey(InputEvent ev) {
		screen.tool = new FixtureTool(screen);
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.repaint();
	}
	
	public void aKey(InputEvent ev) {
		
		screen.hilited = null;
		
		screen.tool = new CircleTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.repaint();
	}
	
	public void sKey(InputEvent ev) {
		QuadTool q = new QuadTool(screen);
		q.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		q.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = q;
		screen.repaint();
	}
	
	public void dKey(InputEvent ev) {
		CubicTools c = new CubicTools(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
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
			
			screen.tool = new MergerTool(screen);
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
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
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
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
			screen.tool.setPoint(screen.lastDraggedWorldPoint);
			
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
			screen.tool.setPoint(screen.lastDraggedWorldPoint);
			
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
		screen.tool.setPoint(null);
		screen.repaint();
	}
	
	public void draftStart(Point p) {
		
		mode = RegularToolMode.DRAFTING;
		
		screen.hilited = null;
		
		stroke = new Stroke(screen.cam, screen.world, screen.world.graph);
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		
		stroke.finish();
		
		Set<Vertex> affected = stroke.processNewStroke();
		screen.world.graph.computeVertexRadii(affected);
		
		assert screen.world.checkConsistency();
		
		debugStroke2 = debugStroke;
		debugStroke = stroke;
		stroke = null;
		
		mode = RegularToolMode.FREE;
		
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
		ctxt.setPixelStroke(screen.cam.pixelsPerMeter, 1);
		
		shape.draw(ctxt);
		
	}
	
}
