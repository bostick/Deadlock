package com.gutabi.bypass;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.gutabi.deadlock.ui.PlatformContentPane;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class MainView extends android.view.View {
	
	PlatformContentPane content;
	
	Paint paint = new Paint();
	
	public MainView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	public void setContentPane(PlatformContentPane content) {
		this.content = content;
	}
	
	public void onDraw(Canvas canvas) {
		
		RenderingContext ctxt = APP.platform.createRenderingContext(canvas, paint);
		
		content.paint(ctxt);
		
	}

}
