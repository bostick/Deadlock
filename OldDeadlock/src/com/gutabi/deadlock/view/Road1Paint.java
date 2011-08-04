package com.gutabi.deadlock.view;

import android.graphics.Paint;

public class Road1Paint extends Paint {
	
	public Road1Paint() {
		setAntiAlias(true);
		setDither(true);
		setColor(0xff888888);
		setStyle(Paint.Style.STROKE);
		setStrokeJoin(Paint.Join.ROUND);
		setStrokeCap(Paint.Cap.ROUND);
		setStrokeWidth(10);
	}
	
}
