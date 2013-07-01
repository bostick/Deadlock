package com.brentonbostick.capsloc.world.graph;

import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.World;

public class BypassFixture extends Fixture {
	
	public BypassFixture(World w, Point p, Axis a) {
		super(w, p, a);
	}
	
	public void paint(RenderingContext ctxt) {
		
	}

}
