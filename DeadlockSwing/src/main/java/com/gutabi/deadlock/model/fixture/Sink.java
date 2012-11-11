package com.gutabi.deadlock.model.fixture;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.graph.Axis;
import com.gutabi.deadlock.model.Fixture;

//@SuppressWarnings("static-access")
public abstract class Sink extends Fixture {
	
	public Sink(Point p, Axis a) {
		super(p, a);
	}

}
