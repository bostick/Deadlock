package com.gutabi.deadlock.swing;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gutabi.deadlock.swing.utils.Point;
import com.gutabi.deadlock.swing.utils.Rat;

public class Test3 {
	
	@Test
	public void test1() {
		Point b = new Point(new Rat(501, 1), new Rat(445, 1));
		Point c = new Point(new Rat(514, 1), new Rat(475, 1));
		Point d = new Point(new Rat(129431, 253), new Rat(118765, 253));
		
		Rat xdc = d.x.minus(c.x);
		Rat ydc = d.y.minus(c.y);
		
		Rat u = new Rat(275524301, 883015967);
		Rat x = c.x.plus(u.times(xdc));
		Rat y = c.y.plus(u.times(ydc));
		
		if (x.equals(b.x)) {
			fail();
		}
		
		if (y.equals(b.y)) {
			fail();
		}
	}
	
	@Test
	public void test2() {
		
		Point a = new Point(new Rat(0, 1), new Rat(0, 1));
		Point b = new Point(new Rat(2, 1), new Rat(2, 1));
		Point c = new Point(new Rat(2, 1), new Rat(0, 1));
		Point d = new Point(new Rat(0, 1), new Rat(2, 1));
		
		Point i = Point.intersection(a, b, c, d);
		assertEquals(new Point(new Rat(1, 1), new Rat(1, 1)), i);
		assertTrue(Point.intersect(new Point(new Rat(1, 1), new Rat(1, 1)), a, b));
	}

}
