package com.gutabi.deadlock.android.controller;

import static com.gutabi.deadlock.android.MainActivity.VIEW;
import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.controller.InputEvent;

public class TouchController implements View.OnTouchListener {
	
	/*
	 * round a to nearest multiple of b
	 */
	static float round(float a, int b) {
		return Math.round(a / b) * b;
	}
	
	/*
	 * round coords from event to nearest multiple of:
	 */
	int roundingFactor = 5;
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		x = round(x, roundingFactor);
		y = round(y, roundingFactor);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			CONTROLLER.inputStart(new InputEvent(new Point(Math.round(x), Math.round(y))));
			VIEW.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			CONTROLLER.inputMove(new InputEvent(new Point(Math.round(x), Math.round(y))));
			VIEW.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			CONTROLLER.inputEnd();
			VIEW.invalidate();
			break;
		}
		return true;
	}
	
}
