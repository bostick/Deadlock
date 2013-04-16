package com.gutabi.bypass.ui.paint;

import android.graphics.Matrix;

import com.gutabi.deadlock.ui.Transform;

public class TransformImpl implements Transform {
	
	Matrix mat = new Matrix();
	
	public TransformImpl() {
		
	}
	
	public double getTranslateX() {
		float[] vals = new float[9];
		mat.getValues(vals);
		return vals[Matrix.MTRANS_X];
	}

	public double getTranslateY() {
		float[] vals = new float[9];
		mat.getValues(vals);
		return vals[Matrix.MTRANS_Y];
	}

}
