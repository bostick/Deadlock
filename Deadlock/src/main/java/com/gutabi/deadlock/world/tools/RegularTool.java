package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Stroke;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.cars.Car;
import com.gutabi.deadlock.world.graph.Direction;
import com.gutabi.deadlock.world.graph.Fixture;
import com.gutabi.deadlock.world.graph.Merger;
import com.gutabi.deadlock.world.graph.Road;
import com.gutabi.deadlock.world.graph.StopSign;
import com.gutabi.deadlock.world.graph.Vertex;

public class RegularTool extends ToolBase {
	
	enum RegularToolMode {
		FREE,
		DRAFTING
	}
	
	RegularToolMode mode;
	
	public Entity hilited;
	
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
			shape = APP.platform.createShapeEngine().createCircle(p, Vertex.INIT_VERTEX_RADIUS);
		} else {
			shape = null;
		}
		
	}
	
	public Shape getShape() {
		return shape;
	}
	
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		APP.platform.setupScreen(s.contentPane.cp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public void d1Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
				
			} else if (hilited instanceof Fixture) {
				Fixture f = (Fixture)hilited;
				
				Fixture g = f.match;
				
				if (f.getType() != null) {
					f.setType(f.getType().other());
				}
				if (g.getType() != null) {
					g.setType(g.getType().other());
				}
				
				f.setSide(f.getSide().other());
				g.setSide(g.getSide().other());
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d2Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d3Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, null);
			
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
				
			}
			
		}
	}

	
	public void qKey() {
		StraightEdgeTool c = new StraightEdgeTool(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
		screen.contentPane.repaint();
	}
	
	public void wKey() {
		screen.tool = new FixtureTool(screen);
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		screen.contentPane.repaint();
	}
	
	public void aKey() {
		
		hilited = null;
		
		screen.tool = new CircleTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		
		screen.contentPane.repaint();
	}
	
	public void sKey() {
		QuadTool q = new QuadTool(screen);
		q.setStart(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		q.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		screen.tool = q;
		screen.contentPane.repaint();
	}
	
	public void dKey() {
		CubicTool c = new CubicTool(screen);
		c.setStart(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		c.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		screen.tool = c;
		screen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (hilited != null) {
			
			if (hilited instanceof StopSign) {
				StopSign s = (StopSign)hilited;
				
				s.setEnabled(true);
				
				screen.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
		} else {
			
			hilited = null;
			
			screen.tool = new MergerTool(screen);
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
			
			screen.contentPane.repaint();
		}
	}
	
	public void deleteKey() {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					screen.world.carMap.destroyCar(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					Set<Vertex> affected = screen.world.graph.removeVertexTop(v);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					Set<Vertex> affected = screen.world.graph.removeRoadTop(e);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					Set<Vertex> affected = screen.world.graph.removeMergerTop(e);
					screen.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					s.r.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		screen.world.render_worldPanel();
		screen.world.render_preview();
		screen.contentPane.repaint();
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = screen.world.hitTest(screen.world.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				hilited = closest;
			}
			
			screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
			
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
			screen.tool.setPoint(screen.world.lastDraggedWorldPoint);
			
			if (screen.world.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(screen.world.lastPressedWorldPoint);
				draftMove(screen.world.lastDraggedWorldPoint);
				
				screen.contentPane.repaint();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			screen.tool.setPoint(screen.world.lastDraggedWorldPoint);
			
			draftMove(screen.world.lastDraggedWorldPoint);
			
			screen.contentPane.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			screen.world.render_worldPanel();
			screen.world.render_preview();
			screen.contentPane.repaint();
			break;
		}
	}
	
	public void draftStart(Point p) {
		
		mode = RegularToolMode.DRAFTING;
		
		hilited = null;
		
		stroke = new Stroke(screen.world);
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
		
		Entity hilitedCopy;
		hilitedCopy = hilited;
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		if (stroke != null) {
			stroke.paint(ctxt);
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		shape.draw(ctxt);
		
		ctxt.setPaintMode();
	}
	
}
