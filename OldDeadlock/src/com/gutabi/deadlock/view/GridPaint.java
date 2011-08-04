package com.gutabi.deadlock.view;

import android.graphics.Paint;

public class GridPaint extends Paint {
	
	public GridPaint() {
		setAntiAlias(true);
		setDither(true);
		setColor(0xffaaaaaa);
		setStyle(Paint.Style.STROKE);
		//gPaint.setStrokeJoin(Paint.Join.ROUND);
		//gPaint.setStrokeCap(Paint.Cap.ROUND);
		setStrokeWidth(2);
	}

}
