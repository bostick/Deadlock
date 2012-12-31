package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("serial")
public class WorldCanvas {
	
	WorldScreen screen;
	
//	private BufferStrategy bs;
	
//	private java.awt.Canvas c;
	
	AABB aabb = new AABB(0, 0, 0, 0);
	
//	private JavaListener jl;
	
	static Logger logger = Logger.getLogger(WorldCanvas.class);
	
	public WorldCanvas(final WorldScreen screen) {
		this.screen = screen;
		
//		c = new java.awt.Canvas() {
//			public void paint(Graphics g) {
//				super.paint(g);
//				
//				RenderingContext ctxt = new RenderingContext((Graphics2D)g);
//				
//				WorldCanvas.this.paint(ctxt);
//			}
//		};
		
//		c.setSize(new Dimension(1384, 822));
//		c.setPreferredSize(new Dimension(1384, 822));
//		c.setMaximumSize(new Dimension(1384, 822));
		
		aabb = new AABB(aabb.x, aabb.y, 1384, 822);
		
//		c.setFocusable(false);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
//	public int getWidth() {
//		return c.getWidth();
//	}
//	
//	public int getHeight() {
//		return c.getHeight();
//	}
//	
//	public java.awt.Canvas java() {
//		return c;
//	}
	
	public Dim postDisplay() {
		
//		c.requestFocusInWindow();
		
//		c.createBufferStrategy(2);
//		bs = c.getBufferStrategy();
		
		return new Dim(aabb.width, aabb.height);
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	Point lastClickedCanvasPoint;
	
	public Point lastPressedWorldPoint;
	
	public Point lastDraggedWorldPoint;
	public boolean lastDraggedWorldPointWasNull;
	
	
	
	public Point lastMovedWorldPoint;
	public Point lastMovedOrDraggedWorldPoint;
	
	public void pressed_canvas(InputEvent ev) {
		
		Point p = ev.p;
		
		lastPressedWorldPoint = screen.canvasToWorld(p);
		lastDraggedWorldPoint = null;
		
	}
	
	public void dragged_canvas(InputEvent ev) {
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastDraggedWorldPointWasNull = (lastDraggedWorldPoint == null);
			lastDraggedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastDraggedWorldPoint;
			screen.tool.dragged(ev);
			break;
		}
		}
		
	}
	
	public void released_canvas(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.released(ev);
			break;
		}
		
	}
	
	public void moved_canvas(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED: {
			Point p = ev.p;
			
			lastMovedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			break;
		}
		case DIALOG:
			break;
		case EDITING: {
			Point p = ev.p;
			
			lastMovedWorldPoint = screen.canvasToWorld(p);
			lastMovedOrDraggedWorldPoint = lastMovedWorldPoint;
			screen.tool.moved(ev);
			break;
		}
		}
	}
	
	public void exited_canvas(InputEvent ev) {
		
		switch (screen.mode) {
		case RUNNING:
		case PAUSED:
		case DIALOG:
			break;
		case EDITING:
			screen.tool.exited(ev);
			break;
		}
		
	}
	
//	public void pressed_canvas(InputEvent ev) {
//		screen.pressed_canvas(ev);
//	}
//	
//	public void dragged_canvas(InputEvent ev) {
//		screen.dragged_canvas(ev);
//	}
//	
//	public void released_canvas(InputEvent ev) {
//		
//		screen.released_canvas(ev);
//	}
//	
//	public void moved_canvas(InputEvent ev) {
//		screen.moved_canvas(ev);
//	}
//	
//	public void exited_canvas(InputEvent ev) {
//		screen.exited_canvas(ev);
//	}
	
//	public void repaint() {
//		c.repaint();
//	}
	
//	public void repaint() {
//		
//		if (SwingUtilities.isEventDispatchThread()) {
//			if (screen.mode == WorldScreenMode.RUNNING) {
//				return;
//			}
//		}
//		
//		do {
//			
//			do {
//				
//				RenderingContext ctxt = new RenderingContext((Graphics2D)bs.getDrawGraphics());
//				ctxt.cam = screen.cam;
//				ctxt.FPS_DRAW = screen.FPS_DRAW;
//				
//				//synchronized (VIEW) {
//				paint_canvas(ctxt);
//				//}
//				
//				ctxt.dispose();
//				
//			} while (bs.contentsRestored());
//			
//			bs.show();
//			
//		} while (bs.contentsLost());
//		
//	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.cam = screen.cam;
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		screen.world.paint_canvas(ctxt);
		
		ctxt.scale(ctxt.cam.pixelsPerMeter);
		ctxt.translate(-ctxt.cam.worldViewport.x, -ctxt.cam.worldViewport.y);
		
		Entity hilitedCopy;
		synchronized (APP) {
			hilitedCopy = screen.hilited;
		}
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		screen.tool.draw(ctxt);
		
		if (APP.FPS_DRAW) {
			
			ctxt.translate(ctxt.cam.worldViewport.x, ctxt.cam.worldViewport.y);
			
			screen.stats.paint(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
