package com.gutabi.deadlock.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * model and controller don't exist when view is created,
 * so must set them later
 * 
 */
public class DeadlockView extends View {
	
	private Renderer renderer = new Renderer();
	
	public DeadlockView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		//renderer.update();
		renderer.paintCanas(canvas);
		
	}
	
//	public void setController(DeadlockController controller) {
//		setOnTouchListener(controller);
//	}
	
}

