package com.gutabi.deadlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.gutabi.deadlock.controller.DeadlockController;
import com.gutabi.deadlock.model.DeadlockModel;

/**
 * 
 * model and controller don't exist when view is created,
 * so must set them later
 * 
 */
public class DeadlockView extends View {
	
	private Renderer renderer;
	
	public DeadlockView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		//renderer.update();
		renderer.paintCanas(canvas);
		
	}
	
	public void setController(DeadlockController controller) {
		setOnTouchListener(controller);
	}
	
	public void setModel(DeadlockModel model) {
		
		renderer = new Renderer(model);
		
	}
	
}

