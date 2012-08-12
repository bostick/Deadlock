package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;
import static org.junit.Assert.assertEquals;

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
import com.gutabi.deadlock.core.Vertex;

public class TestSimpleGraphs {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		VIEW.init();
		
		CONTROLLER.init();
		
		VIEW.frame.setVisible(true);
		VIEW.panel.requestFocusInWindow();
		
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
	
	Comparator<Vertex> vertexComparator = new Comparator<Vertex>() {
		@Override
		public int compare(Vertex a, Vertex b) {
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
	List<Vertex> vertices;
	
	@Test
	public void test1() throws Exception {

		CONTROLLER.mc.pressed(new Point(0, 0));
		CONTROLLER.mc.dragged(new Point(2, 3));
		CONTROLLER.mc.dragged(new Point(1, 0));
		CONTROLLER.mc.released();

		CONTROLLER.mc.pressed(new Point(3, 0));
		CONTROLLER.mc.dragged(new Point(0, 2));
		CONTROLLER.mc.released();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				edges = new ArrayList<Edge>(MODEL.getEdges());
				vertices = new ArrayList<Vertex>(MODEL.getVertices());
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