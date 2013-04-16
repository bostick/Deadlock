package com.gutabi.bypass.ui.paint;

import com.gutabi.deadlock.ui.Transform;

public class TransformImpl implements Transform {
	
	java.awt.geom.AffineTransform t;
	
	public TransformImpl() {
		
	}

	public double getTranslateX() {
		return t.getTranslateX();
	}

	public double getTranslateY() {
		return t.getTranslateY();
	}
	
}
