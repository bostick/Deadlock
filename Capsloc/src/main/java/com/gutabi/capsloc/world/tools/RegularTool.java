package com.gutabi.capsloc.world.tools;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.util.Set;

import com.gutabi.capsloc.Entity;
import com.gutabi.capsloc.geom.Circle;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.InputEvent;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.Stroke;
import com.gutabi.capsloc.world.World;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.graph.Direction;
import com.gutabi.capsloc.world.graph.Fixture;
import com.gutabi.capsloc.world.graph.Merger;
import com.gutabi.capsloc.world.graph.Road;
import com.gutabi.capsloc.world.graph.StopSign;
import com.gutabi.capsloc.world.graph.Vertex;

public class RegularTool extends WorldToolBase {
	
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
	
	public RegularTool() {
		
		mode = RegularToolMode.FREE;
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new Circle(p, Vertex.INIT_VERTEX_RADIUS);
		} else {
			shape = null;
		}
		
	}
	
	public Object getShape() {
		return shape;
	}
	
	
	public void escKey() {
		
	}
	
	public void d1Key() {
		World world = (World)APP.model;
		
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				world.render_worldPanel();
				
			} else if (hilited instanceof Fixture) {
				Fixture f = (Fixture)hilited;
				
				Fixture g = f.match;
				
				if (f.getType() != null) {
					f.setType(f.getType().other());
				}
				if (g.getType() != null) {
					g.setType(g.getType().other());
				}
				
				f.setFacingSide(f.getFacingSide().other());
				g.setFacingSide(g.getFacingSide().other());
				
				world.render_worldPanel();
				
			}
			
		}
	}
	
	public void d2Key() {
		World world = (World)APP.model;
		
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				world.render_worldPanel();
				
			}
			
		}
	}
	
	public void d3Key() {
		World world = (World)APP.model;
		
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, null);
			
				world.render_worldPanel();
				
			}
			
		}
	}

	
	public void qKey() {
		World world = (World)APP.model;
		
		StraightEdgeTool c = new StraightEdgeTool();
		c.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		c.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		APP.tool = c;
	}
	
	public void wKey() {
		World world = (World)APP.model;
		
		APP.tool = new FixtureTool();
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
	}
	
	public void aKey() {
		World world = (World)APP.model;
		
		hilited = null;
		
		APP.tool = new CircleTool();
		
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
	}
	
	public void sKey() {
		World world = (World)APP.model;
		
		QuadTool q = new QuadTool();
		q.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		q.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		APP.tool = q;
	}
	
	public void dKey() {
		World world = (World)APP.model;
		
		CubicTool c = new CubicTool();
		c.setStart(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		c.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		APP.tool = c;
	}
	
	public void insertKey() {
		World world = (World)APP.model;
		
		if (hilited != null) {
			
			if (hilited instanceof StopSign) {
				StopSign s = (StopSign)hilited;
				
				s.setEnabled(true);
				
				world.render_worldPanel();
			}
			
		} else {
			
			hilited = null;
			
			APP.tool = new MergerTool();
			
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			
		}
	}
	
	public void deleteKey() {
		World world = (World)APP.model;
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					world.carMap.destroyCar(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					Set<Vertex> affected = world.graph.removeVertexTop(v);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					Set<Vertex> affected = world.graph.removeRoadTop(e);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					Set<Vertex> affected = world.graph.removeMergerTop(e);
					world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					s.r.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		world.render_worldPanel();
		
//		if (world.previewImage != null) {
//			world.render_preview();
//		}
	}
	
	public void moved(InputEvent ignore) {
		super.moved(ignore);
		
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			Entity closest = world.hitTest(world.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				hilited = closest;
			}
			
			APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
			
			break;
		case DRAFTING:
			assert false;
			break;
		}
	}
	
	public void dragged(InputEvent ignore) {
		super.dragged(ignore);
		
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			APP.tool.setPoint(world.lastDraggedWorldPoint);
			
			if (world.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(world.lastPressedWorldPoint);
				draftMove(world.lastDraggedWorldPoint);
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			APP.tool.setPoint(world.lastDraggedWorldPoint);
			
			draftMove(world.lastDraggedWorldPoint);
			
			break;
		}
	}
	
	public void released(InputEvent ignore) {
		super.released(ignore);
		
		World world = (World)APP.model;
		
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			world.render_worldPanel();
//			if (world.previewImage != null) {
//				world.render_preview();
//			}
			break;
		}
	}
	
	public void draftStart(Point p) {
		World world = (World)APP.model;
		
		mode = RegularToolMode.DRAFTING;
		
		hilited = null;
		
		stroke = new Stroke(world);
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		World world = (World)APP.model;
		
		stroke.finish();
		
		Set<Vertex> affected = stroke.processNewStroke(true);
		world.graph.computeVertexRadii(affected);
		
//		assert world.checkConsistency();
		
		debugStroke2 = debugStroke;
		debugStroke = stroke;
		stroke = null;
		
		mode = RegularToolMode.FREE;
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		if (p == null) {
			return;
		}
		
		ctxt.pushTransform();
		
		ctxt.scale(world.worldCamera.pixelsPerMeter);
		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
		
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
		
		ctxt.popTransform();
		
		ctxt.clearXORMode();
	}
	
}
