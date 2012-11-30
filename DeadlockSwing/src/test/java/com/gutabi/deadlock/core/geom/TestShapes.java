package com.gutabi.deadlock.core.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gutabi.deadlock.core.Point;

public class TestShapes {
	
	@Test
	public void test1() throws Exception {
		
		Circle c = new Circle(null, new Point(9.160789639164582, 6.0042), 0.7071067811865476);
		Capsule cap = new Capsule(null, new Circle(null, new Point(7.75, 6.09375), .7071067811865476), new Circle(null, new Point(8.1875, 6.71875), .7071067811865476));
		
		assertTrue(ShapeUtils.intersect(cap, c));
//		assertTrue(c.intersect(cap));
		
	}
	
	@Test
	public void test2() throws Exception {
		
		Quad q = new Quad(null, new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1));
		
		assertTrue(q.hitTest(new Point(0.5, 0.5)));
	}
	
	@Test
	public void test3() throws Exception {
		
		int s = Geom.halfPlane(new Point(0.5, 0.5), new Point(0, 0), new Point(1, 0));
		assertEquals(s, 1);
	}
}
