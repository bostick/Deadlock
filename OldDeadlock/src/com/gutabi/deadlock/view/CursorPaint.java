package com.gutabi.deadlock.view;

import android.graphics.Paint;

public class CursorPaint extends Paint {
	
	public CursorPaint() {
		setAntiAlias(true);
		setDither(true);
		setStrokeCap(Paint.Cap.ROUND);
		setColor(0xff888888);
		setStrokeWidth(40);
	}
	
}
