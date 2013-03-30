package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.Set;

import com.gutabi.deadlock.Entity;
import com.gutabi.deadlock.geom.Circle;
import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenuScreen;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreen;
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
	
	public RegularTool(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen, debuggerScreen);
		
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
		
		APP.platform.unshowDebuggerScreen();
		
		MainMenuScreen s = new MainMenuScreen();
		APP.setAppScreen(s);
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
	public void d1Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.STARTTOEND);
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.repaint();
				
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
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d2Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, Direction.ENDTOSTART);
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.repaint();
				
			}
			
		}
	}
	
	public void d3Key() {
		if (hilited != null) {
			
			if (hilited instanceof Road) {
				Road r = (Road)hilited;
				
				r.setDirection(null, null);
			
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.repaint();
				
			}
			
		}
	}

	
	public void qKey() {
		StraightEdgeTool c = new StraightEdgeTool(worldScreen, debuggerScreen);
		c.setStart(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		c.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		APP.tool = c;
		worldScreen.contentPane.repaint();
	}
	
	public void wKey() {
		APP.tool = new FixtureTool(worldScreen, debuggerScreen);
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		worldScreen.contentPane.repaint();
	}
	
	public void aKey() {
		
		hilited = null;
		
		APP.tool = new CircleTool(worldScreen, debuggerScreen);
		
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		
		worldScreen.contentPane.repaint();
	}
	
	public void sKey() {
		QuadTool q = new QuadTool(worldScreen, debuggerScreen);
		q.setStart(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		q.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		APP.tool = q;
		worldScreen.contentPane.repaint();
	}
	
	public void dKey() {
		CubicTool c = new CubicTool(worldScreen, debuggerScreen);
		c.setStart(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		c.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		APP.tool = c;
		worldScreen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (hilited != null) {
			
			if (hilited instanceof StopSign) {
				StopSign s = (StopSign)hilited;
				
				s.setEnabled(true);
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.repaint();
			}
			
		} else {
			
			hilited = null;
			
			APP.tool = new MergerTool(worldScreen, debuggerScreen);
			
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
			
			worldScreen.contentPane.repaint();
		}
	}
	
	public void deleteKey() {
		
		if (hilited != null) {
			
			if (hilited.isUserDeleteable()) {
				
				if (hilited instanceof Car) {
					Car c = (Car)hilited;
					
					worldScreen.contentPane.worldPanel.world.carMap.destroyCar(c);
					
				} else if (hilited instanceof Vertex) {
					Vertex v = (Vertex)hilited;
					
					Set<Vertex> affected = worldScreen.contentPane.worldPanel.world.graph.removeVertexTop(v);
					worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Road) {
					Road e = (Road)hilited;
					
					Set<Vertex> affected = worldScreen.contentPane.worldPanel.world.graph.removeRoadTop(e);
					worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof Merger) {
					Merger e = (Merger)hilited;
					
					Set<Vertex> affected = worldScreen.contentPane.worldPanel.world.graph.removeMergerTop(e);
					worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
					
				} else if (hilited instanceof StopSign) {
					StopSign s = (StopSign)hilited;
					
					s.r.removeStopSignTop(s);
					
				} else {
					throw new AssertionError();
				}
				
				hilited = null;
				
			}
			
		}
		
		worldScreen.contentPane.worldPanel.world.render_worldPanel();
		worldScreen.contentPane.worldPanel.world.render_preview();
		worldScreen.contentPane.repaint();
	}
	
	public void moved(InputEvent ev) {
		switch (mode) {
		case FREE:
			Entity closest = worldScreen.contentPane.worldPanel.world.hitTest(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint);
			
			synchronized (APP) {
				hilited = closest;
			}
			
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
			
			worldScreen.contentPane.repaint();
			break;
		case DRAFTING:
			assert false;
			break;
		}
	}
	
	public void dragged(InputEvent ev) {
		switch (mode) {
		case FREE:
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint);
			
			if (worldScreen.contentPane.worldPanel.world.lastDraggedWorldPointWasNull) {
				// first drag
				draftStart(worldScreen.contentPane.worldPanel.world.lastPressedWorldPoint);
				draftMove(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint);
				
				worldScreen.contentPane.repaint();
				
			} else {
				assert false;
			}
			break;
		case DRAFTING:
			APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint);
			
			draftMove(worldScreen.contentPane.worldPanel.world.lastDraggedWorldPoint);
			
			worldScreen.contentPane.repaint();
			break;
		}
	}
	
	public void released(InputEvent ev) {
		switch (mode) {
		case FREE:
			break;
		case DRAFTING:
			draftEnd();
			
			worldScreen.contentPane.worldPanel.world.render_worldPanel();
			worldScreen.contentPane.worldPanel.world.render_preview();
			worldScreen.contentPane.repaint();
			debuggerScreen.contentPane.repaint();
			break;
		}
	}
	
	public void draftStart(Point p) {
		
		mode = RegularToolMode.DRAFTING;
		
		hilited = null;
		
		stroke = new Stroke(worldScreen.contentPane.worldPanel.world);
		stroke.add(p);
			
	}
	
	public void draftMove(Point p) {

		stroke.add(p);
	}
	
	public void draftEnd() {
		
		stroke.finish();
		
		Set<Vertex> affected = stroke.processNewStroke();
		worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
		
		assert worldScreen.contentPane.worldPanel.world.checkConsistency();
		
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
