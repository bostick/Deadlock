package com.gutabi.deadlock.android.controller;

import static com.gutabi.deadlock.android.MainActivity.PLATFORMVIEW;
import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gutabi.deadlock.core.DPoint;

public class TouchController implements View.OnTouchListener {
	
	public boolean onTouch(View ignored, MotionEvent event) {
		final float x = event.getX();
		final float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touch", "DOWN <"+x+","+y+">");
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.pressed(new DPoint(x, y));
					PLATFORMVIEW.postInvalidate();
				}});
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touch", "DOWN <"+x+","+y+">");
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.dragged(new DPoint(x, y));
					PLATFORMVIEW.postInvalidate();
				}});
			break;
		case MotionEvent.ACTION_UP:
			Log.d("touch", "DOWN <"+x+","+y+">");
			CONTROLLER.queue(new Runnable(){
				@Override
				public void run() {
					CONTROLLER.released();
					PLATFORMVIEW.postInvalidate();
				}});
			break;
		}
		return true;
	}
	
}
