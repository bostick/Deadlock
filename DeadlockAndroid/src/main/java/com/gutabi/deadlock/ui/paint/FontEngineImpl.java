package com.gutabi.deadlock.ui.paint;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;
import com.gutabi.deadlock.geom.AABB;

public class FontEngineImpl implements FontEngine {

	public AABB bounds(String text, Resource font, FontStyle fontStyle, int fontSize) {
		
		Paint textPaint = new Paint();
		int s = -1;
		switch (fontStyle) {
		case PLAIN:
			s = Typeface.NORMAL;
			break;
		}
		
		textPaint.setTypeface(((ResourceImpl)font).face);
		textPaint.setTextSize(fontSize);
		
		Rect bounds = new Rect();
		textPaint.getTextBounds(text,0,text.length(), bounds);

		return APP.platform.createShapeEngine().createAABB(bounds.left, bounds.top, bounds.width(), bounds.height());
	}

}
