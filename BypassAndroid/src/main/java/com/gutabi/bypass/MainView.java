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
	
	public MainView(Context c, AttributeSet s) {
		super(c, s);
		
//		ctxt = APP.platform.createRenderingContext();
	}
	
	public void setContentPane(PlatformContentPane content) {
		this.content = content;
	}
	
	
	
	Paint paint = new Paint();
	RenderingContext ctxt;
	
	public void onDraw(Canvas canvas) {
		
		APP.platform.setRenderingContextFields2(ctxt, canvas, paint);
		
		content.paint(ctxt);
	}

}
