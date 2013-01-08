package com.gutabi.deadlock.ui.paint;

import com.gutabi.deadlock.ui.AffineTransform;

public class AffineTransformImpl implements AffineTransform {
	
	java.awt.geom.AffineTransform t;
	
	public AffineTransformImpl(java.awt.geom.AffineTransform t) {
		this.t = t;
	}

	public double getTranslateX() {
		return t.getTranslateX();
	}

	public double getTranslateY() {
		return t.getTranslateY();
	}
	
}
