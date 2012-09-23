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
		
		assertTrue(DMath.equals(du, (d.getY() - a.getY()) / yba));
		
	}
	
	@Test
	public void test2() throws Exception {
		
		/*
		 * the line (distanceToMove < distanceToEndOfEdge) in SimulationRunnable
		 * 
		 */
		
		double distanceToMove = 2.0;
		double distanceToEndOfEdge = 2.000000000000341;
		
		assertTrue(distanceToMove < distanceToEndOfEdge);
		
		assertTrue(DMath.equals(distanceToMove, distanceToEndOfEdge));
		
	}
	
	@Test
	public void test3() throws Exception {
		
		double cu = -3.999999999998863;
		Point c = new Point(1344.0, 295.0);
		Point a = new Point(1345.6, 295.8);
		double yba = 0.19999999999998863;
		
		double tmp = (c.getY() - a.getY()) / yba;
		
		assertTrue(cu != tmp);
		
		assertTrue(DMath.equals(cu, tmp));
		
	}
	
	@Test
	public void test4() throws Exception {
		
		double ux = 0.009001841302863665;
		double uy = 0.00900184130320779;
		
		assertTrue(ux != uy);
		
		assertTrue(DMath.equals(ux, uy));
		
	}
	
	@Test
	public void test5() {
		
		double time = 5.00000000012918;
		
		assertTrue(time != 5);
		
//		float uxf = (float)ux;
//		float uyf = (float)uy;
		
		assertTrue(DMath.equals(time, 5));
		
	}
}
