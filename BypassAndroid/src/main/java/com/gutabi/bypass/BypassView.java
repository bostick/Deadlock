package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.gutabi.deadlock.ui.Paintable;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class BypassView extends View {
	
	public BypassActivity activity;
	
	public Paintable paintable;
	
	public BypassView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	
	Paint paint = new Paint();
	public RenderingContext ctxt;
	
	public void onDraw(Canvas canvas) {
		
		if (activity.state != ActivityState.PAUSE) {
			
			APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
			
			paintable.paint(ctxt);
			
		} else {
			
//			APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
//			
//			paintable.paint(ctxt);
			
		}
		
	}
	
}
