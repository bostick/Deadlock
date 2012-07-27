package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.view.DeadlockView.VIEW;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.Point;

public class MouseController implements MouseListener, MouseMotionListener {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	public void init() {
		VIEW.panel.addMouseListener(this);
		VIEW.panel.addMouseMotionListener(this);
	}
	
	public void pressed(Point p) {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		
		MODEL.lastPointRaw = p;
		MODEL.curStrokeRaw.add(p);
		
		MODEL.allStrokes.add(new ArrayList<Point>());
		
		MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
		
		VIEW.repaint();
	}
	
	public void dragged(Point p) {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		if (!p.equals(MODEL.lastPointRaw)) {
			MODEL.curStrokeRaw.add(p);
			MODEL.allStrokes.get(MODEL.allStrokes.size()-1).add(p);
			MODEL.lastPointRaw = p;
			VIEW.repaint();
		}
	}
	
	public void released() {
		assert Thread.currentThread().getName().startsWith("AWT-EventQueue-");
		
		//List<Point> curStroke1 = massage(MODEL.curStrokeRaw);
		List<Point> curStroke1 = MODEL.curStrokeRaw;
		
		for (int i = 0; i < curStroke1.size()-1; i++) {
			MODEL.addStroke(curStroke1.get(i), curStroke1.get(i+1));
		}
		
		MODEL.checkConsistency();
		
		MODEL.lastPointRaw = null;
		MODEL.curStrokeRaw.clear();
	}
	
	public void pressed_M(final Point p) throws InterruptedException, InvocationTargetException {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				pressed(p);
			}
		});
	}
	
	public void dragged_M(final Point p) throws InterruptedException, InvocationTargetException {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				dragged(p);
				//VIEW.repaint();
			}
		});
	}
	
	public void released_M() throws InterruptedException, InvocationTargetException {
		released_M(false);
	}
	
	public void released_M(final boolean massage) throws InterruptedException, InvocationTargetException {
		assert Thread.currentThread().getName().startsWith("main");
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				if (massage) {
					MODEL.curStrokeRaw = massage(MODEL.curStrokeRaw);
				}
				released();
				//VIEW.repaint();
			}
		});
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		pressed(new Point(ev.getX(), ev.getY()));
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		//dragged(new Point(ev.getX(), ev.getY()));
		Point p = new Point(ev.getX(), ev.getY());
		dragged(p);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {
		
		MODEL.curStrokeRaw = massage(MODEL.curStrokeRaw);
		
		released();
	}
	
	private List<Point> massage(List<Point> raw) {
		
		/*
		 * maybe 1. cut-off rest of points if angle is too sharp
		 * maybe 2. cut-off rest of points if too close to 
		 * 
		 * 1. adjust for first point to be on a vertex, if close enough
		 * 2. adjust for second point, etc.
		 * 
		 * 3. if first point is not on vertex, adjust for first vertex to be on edge, if close enough
		 * 4. if second point is not on vertex, adjust for second vertex to be on edge, if close enough
		 * 
		 * 
		 * scan endpoints of raw first
		 * scan over vertices first since the user will probably be trying to hit vertices more than just edges
		 * 
		 */
		
		//List<Point> diff = new ArrayList<Point>(raw.size());
		List<Point> adj = new ArrayList<Point>(raw);
		
		if (adj.size() == 1) {
			return adj;
		}
		
		int s = raw.size();
		Point first = raw.get(0);
		Point last = raw.get(s-1);
		
		/*
		 * first and last have to be very close
		 */
		if ((!last.equals(first)) && Point.dist(last, first) <= 20.0) {
			/*
			 * maintain invariant that there are no contiguous, equal points
			 */
			if (!raw.get(s-2).equals(first)) {
				adj.set(s-1, first);
				last = first;
			} else {
				adj.remove(s-1);
				s = s-1;
				last = raw.get(s-1);
			}
		}
		
		Vertex best = null;
		
		for (Vertex v : MODEL.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!first.equals(vp)) && Point.dist(first, vp) <= 40.0) {
				if (best == null) {
					best = v;
				} else if (Point.dist(first, vp) < Point.dist(first, best.getPoint())) {
					best = v;
				}
			}
			
		}
		
		if (best != null) {
			
			if (adj.size() == 1) {
				adj.set(0, best.getPoint());
			} else {
				if (!adj.get(1).equals(best.getPoint())) {
					adj.set(0, best.getPoint());
					first = adj.get(0);
				} else {
					adj.remove(0);
					s = s-1;
					first = adj.get(0);
				}
			}
			
		}
		
		best = null;
		
		for (Vertex v : MODEL.getVertices()) {
			
			Point vp = v.getPoint();
			
			if ((!last.equals(vp)) && Point.dist(last, vp) <= 40.0) {
				if (best == null) {
					best = v;
				} else if (Point.dist(last, vp) < Point.dist(last, best.getPoint())) {
					best = v;
				}
			}
		}
		
		if (best != null) {
			
			if (adj.size() == 1) {
				adj.set(s-1, best.getPoint());
			} else {
				if (!adj.get(s-2).equals(best.getPoint())) {
					adj.set(s-1, best.getPoint());
					last = adj.get(s-1);
				} else {
					adj.remove(s-1);
					s = s-1;
					last = adj.get(s-1);
				}
			}
			
		}
		
		return adj;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		;
	}
	
}
