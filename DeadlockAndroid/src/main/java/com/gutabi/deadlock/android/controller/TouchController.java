package com.gutabi.deadlock.android.controller;

import static com.gutabi.deadlock.android.MainActivity.PLATFORMVIEW;
import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gutabi.core.DPoint;
import com.gutabi.deadlock.core.controller.InputEvent;

public class TouchController implements View.OnTouchListener {
	
	public boolean onTouch(View ignored, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			CONTROLLER.inputStart(new InputEvent(new DPoint(Math.round(x), Math.round(y))));
			PLATFORMVIEW.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "MOVE <"+x+","+y+">");
			CONTROLLER.inputMove(new InputEvent(new DPoint(Math.round(x), Math.round(y))));
			PLATFORMVIEW.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "UP   <"+x+","+y+">");
			CONTROLLER.inputEnd();
			PLATFORMVIEW.invalidate();
			break;
		}
		return true;
	}
	
}
