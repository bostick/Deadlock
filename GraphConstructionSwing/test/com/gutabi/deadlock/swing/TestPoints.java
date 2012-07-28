package com.gutabi.deadlock.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Point;

public class TestPoints {

	@Test
	public void test() throws Exception {
		Point a = new Point(0, 0);
		Point b = new Point(2, 2);
		Point c = new Point(2, 0);
		Point d = new Point(0, 2);
		
		DPoint i = Point.intersection(a, b, c, d);
		assertEquals(new DPoint(1, 1), i);
		assertTrue(Point.intersect(new Point(1, 1), a, b));
	}
	
	@Test
	public void testCo1() throws Exception {
		assertTrue(Point.colinear(new Point(0, 0), new Point(0, 1), new Point(0, 2)));	
	}
	
	@Test
	public void testCo2() throws Exception {
		try {
			Point.colinear(new Point(0, 0), new Point(0, 2), new Point(0, 1));
			fail();
		} catch (IllegalArgumentException e) {
			
		}
	}

}
