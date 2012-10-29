package com.gutabi.deadlock.swing;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.swing.TestDragging.testDragged;
import static com.gutabi.deadlock.swing.TestDragging.testPressed;
import static com.gutabi.deadlock.swing.TestDragging.testReleased;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.util.Comparator;
import java.util.List;

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
			Point aP = a.p;
			Point bP = b.p;
			if (aP.x < bP.x) {
				return -1;
			} else if (aP.x > bP.x) {
				return 1;
			} else {
				if (aP.y < bP.y) {
					return -1;
				} else if (aP.y > bP.y) {
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

		testPressed(new Point(0, 0));
		testDragged(new Point(2, 3));
		testDragged(new Point(1, 0));
		testReleased();

		testPressed(new Point(3, 0));
		testDragged(new Point(0, 2));
		testReleased();
		
//		SwingUtilities.invokeAndWait(new Runnable() {
//			@Override
//			public void run() {
//				edges = new ArrayList<Edge>(MODEL.world.graph.getEdges());
//				vertices = new ArrayList<Vertex>(MODEL.world.graph.getAllVertices());
//			}
//		});
//		
//		//Collections.sort(edges, edgeComparator);
//		Collections.sort(vertices, vertexComparator);
//		
//		assertEquals(5, edges.size());
//		assertEquals(6, vertices.size());
//		
//		assertEquals(new Point(0, 0), vertices.get(0).getPoint());
//		assertEquals(new Point(0, 2), vertices.get(1).getPoint());
//		assertEquals(new Point(1, 0), vertices.get(2).getPoint());
//		assertEquals(new Point(1, 1), vertices.get(3).getPoint());
//		assertEquals(new Point(2, 3), vertices.get(4).getPoint());
//		assertEquals(new Point(3, 0), vertices.get(5).getPoint());
		
	}

}
