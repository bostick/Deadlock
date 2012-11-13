package com.gutabi.deadlock.core.geom;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gutabi.deadlock.core.Point;

public class TestShapes {
	
	@Test
	public void test1() throws Exception {
		
		Circle c = new Circle(null, new Point(9.160789639164582, 6.0042), 0.7071067811865476);
		Capsule cap = new Capsule(null, new Circle(null, new Point(7.75, 6.09375), .7071067811865476), new Circle(null, new Point(8.1875, 6.71875), .7071067811865476));
		
		assertTrue(cap.intersect(c));
//		assertTrue(c.intersect(cap));
		
	}
	
}
