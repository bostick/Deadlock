package com.gutabi.deadlock.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPoint {
	
	@Test
	public void test1() throws Exception {
		
		Point a = new Point(1.12345678901234, 2.3456789012345);
		Point b = new Point(1.12345678901235, 2.3456789012346);
		
		assertTrue(a.equals(b));
		
		assertEquals(a.hashCode(), b.hashCode());
		
	}
	
	/*
	 * exposed in TestDragging.testBug16
	 */
	@Test
	public void test2() {
		
		assertTrue(675.93103448275D != 675.93103448276D);
		
		//assertTrue(675.93103448275F == 675.93103448276F);
		
		Point a = new Point(823.1724137931, 675.93103448275);
		Point b = new Point(823.1724137931, 675.93103448276);
		
		assertTrue(a.equals(b));
	}
	
	@Test
	public void test3() {
		
		Point a = new Point(349, 377);
		Point b = new Point(379, 377);
		
		Point c = new Point(403, 377);
		
		double dist = Point.distance(c, a, b);
		
		assertTrue(dist != 0.0);
	}
}
