package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.util.Set;

import javax.swing.JFrame;

import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Circle;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Direction;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

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
	
	
	public void escKey() {
		
		screen.teardown(APP.container);
		APP.container.getContentPane().repaint();
		
		MainMenu s = new MainMenu();
		
		s.setup(APP.container);
		((JFrame)APP.container).setVisible(true);
		
		s.postDisplay();
		s.canvas.render();
		s.contentPane.repaint();
	}
	
	public void d1Key() {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
				
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
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d2Key() {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d3Key() {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof Road) {
				Road r = (Road)screen.hilited;
				
				r.setDirection(null, null);
			
				screen.world.render_canvas();
				screen.contentPane.repaint();
				
			}
			
		}
	}

	
	public void qKey() {
		StraightEdgeTool c = new StraightEdgeTool(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
		screen.contentPane.repaint();
	}
	
	public void wKey() {
		screen.tool = new FixtureTool(screen);
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.contentPane.repaint();
	}
	
	public void aKey() {
		
		screen.hilited = null;
		
		screen.tool = new CircleTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.contentPane.repaint();
	}
	
	public void sKey() {
		QuadTool q = new QuadTool(screen);
		q.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		q.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = q;
		screen.contentPane.repaint();
	}
	
	public void dKey() {
		CubicTool c = new CubicTool(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
		screen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (screen.hilited != null) {
			
			if (screen.hilited instanceof StopSign) {
				StopSign s = (StopSign)screen.hilited;
				
				s.setEnabled(true);
				
				screen.world.render_canvas();
				screen.contentPane.repaint();
			}
			
		} else {
			
			screen.hilited = null;
			
			screen.tool = new MergerTool(screen);
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
			
			screen.contentPane.repaint();
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
			
			screen.contentPane.repaint();
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
				
				screen.contentPane.repaint();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			screen.tool.setPoint(screen.lastDraggedWorldPoint);
			
			draftMove(screen.lastDraggedWorldPoint);
			
			screen.contentPane.repaint();
//			screen.controlPanel.repaint();
//			screen.controlPanel.previewPanel.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			screen.world.render_canvas();
			screen.world.render_preview();
			screen.contentPane.repaint();
//			screen.controlPanel.repaint();
			break;
		}
	}
	
	public void exited(InputEvent ev) {
		screen.tool.setPoint(null);
		screen.contentPane.repaint();
	}
	
	public void draftStart(Point p) {
		
		mode = RegularToolMode.DRAFTING;
		
		screen.hilited = null;
		
		stroke = new Stroke(screen);
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
		ctxt.setPixelStroke(1.0);
		
		shape.draw(ctxt);
		
		ctxt.setPaintMode();
	}
	
}
