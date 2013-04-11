package com.gutabi.bypass.ui.paint;

import com.gutabi.deadlock.ui.Transform;

public class TransformImpl implements Transform {
	
	java.awt.geom.AffineTransform t;
	
	public TransformImpl(java.awt.geom.AffineTransform t) {
		this.t = t;
	}

	public double getTranslateX() {
		return t.getTranslateX();
	}

	public double getTranslateY() {
		return t.getTranslateY();
	}
	
}
