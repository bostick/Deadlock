package com.gutabi.deadlock.ui.paint;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.gutabi.deadlock.geom.AABB;

import static com.gutabi.deadlock.DeadlockApplication.APP;

public class FontEngineImpl implements FontEngine {

	public AABB bounds(String text, String fontName, FontStyle fontStyle, int fontSize) {
		
		Paint textPaint = new Paint();
		int s = -1;
		switch (fontStyle) {
		case PLAIN:
			s = Typeface.NORMAL;
			break;
		}
		
		Typeface face = Typeface.create(fontName, s);
		
		textPaint.setTypeface(face);
		textPaint.setTextSize(fontSize);
		
		
		Rect bounds = new Rect();
		textPaint.getTextBounds(text,0,text.length(), bounds);

		return APP.platform.createShapeEngine().createAABB(bounds.left, bounds.top, bounds.width(), bounds.height());
	}

}
