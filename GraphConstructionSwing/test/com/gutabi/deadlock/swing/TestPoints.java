package com.gutabi.deadlock.swing;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gutabi.deadlock.swing.utils.DPoint;
import com.gutabi.deadlock.swing.utils.Point;

public class TestPoints {

	@Test
	public void test() {
		Point a = new Point(0, 0);
		Point b = new Point(2, 2);
		Point c = new Point(2, 0);
		Point d = new Point(0, 2);
		
		DPoint i = Point.intersection(a, b, c, d);
		assertEquals(new DPoint(1, 1), i);
		assertTrue(Point.intersect(new Point(1, 1), a, b));
	}
	
	@Test
	public void testCo1() {
		assertTrue(Point.colinear(new Point(0, 0), new Point(0, 1), new Point(0, 2)));	
	}
	
	@Test
	public void testCo2() {
		try {
			Point.colinear(new Point(0, 0), new Point(0, 2), new Point(0, 1));
			fail();
		} catch (IllegalArgumentException e) {
			
		}
	}

}
