package com.gutabi.deadlock.test;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.gutabi.deadlock.model.OverlappingException;
import com.gutabi.deadlock.model.PointFUtils;

public class PointFUtilsTest extends TestCase {
	
	public void testSkewNotIntersecting() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.2f, 2.3f);
		PointF c = new PointF(10.0f, 0.0f);
		PointF d = new PointF(11.0f, 43.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertNull(inter);
		
	}
	
	public void testSkewSinglePoint() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.2f, 23.0f);
		PointF c = new PointF(1.2f, 23.0f);
		PointF d = new PointF(100.0f, 102.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertEquals(inter.x, 1.2f);
		assertEquals(inter.y, 23.0f);
		
	}
	
	public void testSkewIntersecting() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.2f, 3.0f);
		PointF c = new PointF(1.2f, 0.0f);
		PointF d = new PointF(0.0f, 3.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertEquals(inter.x, 0.6f);
		assertEquals(inter.y, 1.5f);
		
	}
	
	public void testParallel() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.2f, 1.0f);
		PointF c = new PointF(10.0f, 0.0f);
		PointF d = new PointF(11.2f, 1.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertNull(inter);
		
	}
	
	public void testColinearNotIntersecting() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.f, 1.0f);
		PointF c = new PointF(2.0f, 2.0f);
		PointF d = new PointF(3.0f, 3.0f);

		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertNull(inter);
		
	}
	
	public void testColinearSinglePoint() throws OverlappingException {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(1.0f, 1.0f);
		PointF c = new PointF(1.0f, 1.0f);
		PointF d = new PointF(2.0f, 2.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertEquals(inter.x, 1.0f);
		assertEquals(inter.y, 1.0f);
		
	}
	
	public void testOverlapping() {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(2.0f, 2.0f);
		PointF c = new PointF(1.0f, 1.0f);
		PointF d = new PointF(3.0f, 3.0f);
		
		try {
			PointF inter = PointFUtils.intersection(a, b, c, d);
			fail();
		} catch (OverlappingException e) {
			
		}
		
	}
	
	public void testIdentical() {
		
		PointF a = new PointF(0.0f, 0.0f);
		PointF b = new PointF(2.0f, 2.0f);
		PointF c = new PointF(0.0f, 0.0f);
		PointF d = new PointF(2.0f, 2.0f);
		
		try {
			PointF inter = PointFUtils.intersection(a, b, c, d);
			fail();
		} catch (OverlappingException e) {
			
		}
		
	}
	
	public void test() throws OverlappingException {
		
		PointF a = new PointF(240.0f, 240.0f);
		PointF b = new PointF(240.0f, 300.0f);
		PointF c = new PointF(240.0f, 180.0f);
		PointF d = new PointF(240.0f, 240.0f);
		
		PointF inter = PointFUtils.intersection(a, b, c, d);
		
		assertEquals(inter.x, 240.0f);
		assertEquals(inter.y, 240.0f);
	}

}
