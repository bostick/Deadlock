package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.gutabi.deadlock.ui.Paintable;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainView extends View {
	
	Paintable paintable;
	
	public MainView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	public void setPaintable(Paintable paintable) {
		this.paintable = paintable;
	}
	
	
	Paint paint = new Paint();
	RenderingContext ctxt;
	
	public void onDraw(Canvas canvas) {
		
		APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
		
		paintable.paint(ctxt);
	}

}
