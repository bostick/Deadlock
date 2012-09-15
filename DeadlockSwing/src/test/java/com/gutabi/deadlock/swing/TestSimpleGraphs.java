package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import static org.junit.Assert.assertEquals;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Intersection;

public class TestSimpleGraphs {
	
	static Point OFFSET = new Point(0, 0);
	
	Runnable empty = new Runnable(){
		@Override
		public void run() {
			;
		}};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
	}

	public void testPressed(Point p) throws Exception {
		Point pp = p.add(OFFSET);
		CONTROLLER.mc.pressed(new MouseEvent(null, 0, 0, 0, (int)pp.getX(), (int)pp.getY(), 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testDragged(Point p) throws Exception {
		Point pp = p.add(OFFSET);
		CONTROLLER.mc.dragged(new MouseEvent(null, 0, 0, 0, (int)pp.getX(), (int)pp.getY(), 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
		VIEW.repaint();
	}
	
	public void testReleased() throws Exception {
		CONTROLLER.mc.released(new MouseEvent(null, 0, 0, 0, 0, 0, 0, false));
		CONTROLLER.queueAndWait(empty);
		Thread.sleep(10);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		MODEL.clear();
	}
	
//	Comparator<Edge> edgeComparator = new Comparator<Edge>() {
//		@Override
//		public int compare(Edge a, Edge b) {
//			if (a == b) {
//				return 0;
//			}
//			
//		}
//	};
	
	Comparator<Intersection> vertexComparator = new Comparator<Intersection>() {
		@Override
		public int compare(Intersection a, Intersection b) {
			if (a == b) {
				return 0;
			}
			Point aP = a.getPoint();
			Point bP = b.getPoint();
			if (aP.getX() < bP.getX()) {
				return -1;
			} else if (aP.getX() > bP.getX()) {
				return 1;
			} else {
				if (aP.getY() < bP.getY()) {
					return -1;
				} else if (aP.getY() > bP.getY()) {
					return 1;
				} else {
					throw new AssertionError("Vertices are not equal but have the same point");
				}
			}
		}
	};
	
	List<Edge> edges;
	List<Intersection> vertices;
	
	@Test
	public void test1() throws Exception {

		testPressed(new Point(0, 0));
		testDragged(new Point(2, 3));
		testDragged(new Point(1, 0));
		testReleased();

		testPressed(new Point(3, 0));
		testDragged(new Point(0, 2));
		testReleased();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = new ArrayList<Edge>(MODEL.getEdges());
				vertices = new ArrayList<Intersection>(MODEL.getIntersections());
			}
		});
		
		//Collections.sort(edges, edgeComparator);
		Collections.sort(vertices, vertexComparator);
		
		assertEquals(5, edges.size());
		assertEquals(6, vertices.size());
		
		assertEquals(new Point(0, 0), vertices.get(0).getPoint());
		assertEquals(new Point(0, 2), vertices.get(1).getPoint());
		assertEquals(new Point(1, 0), vertices.get(2).getPoint());
		assertEquals(new Point(1, 1), vertices.get(3).getPoint());
		assertEquals(new Point(2, 3), vertices.get(4).getPoint());
		assertEquals(new Point(3, 0), vertices.get(5).getPoint());
		
	}

}
