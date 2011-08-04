package com.gutabi.deadlock.view;

import android.graphics.Color;
import android.graphics.Paint;

public class Road2Paint extends Paint {
	
	public Road2Paint() {
		setAntiAlias(true);
		setDither(true);
		setColor(Color.YELLOW);
		setStyle(Paint.Style.STROKE);
		setStrokeJoin(Paint.Join.ROUND);
		setStrokeCap(Paint.Cap.ROUND);
		setStrokeWidth(1);
	}

}
