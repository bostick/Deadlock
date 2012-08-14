package com.gutabi.deadlock.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestDMath {
	
	@Test
	public void test1() throws Exception {
		
		/*
		 * Point.intersection around line 101 used to have 
		 * 
		 * assert du == (d.getY() - a.getY()) / yba;, which can fail
		 * 
		 */
		
		double du = 6.606837606837639;
		Point d = new Point(721.0, 469.0);
		Point a = new Point(760.0, 464.0);
		double yba = 0.756791720569197;
		
		assertTrue(du != (d.getY() - a.getY()) / yba);
		
		assertTrue(DMath.doubleEquals(du, (d.getY() - a.getY()) / yba));
		
	}
	
}
