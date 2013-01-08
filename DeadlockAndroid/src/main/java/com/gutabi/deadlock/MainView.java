package com.gutabi.deadlock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gutabi.deadlock.ui.ContentPane;

public class MainView extends android.view.View {
	
	public MainView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	public void setContentPane(ContentPane content) {
		
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(0xFFdddddd);
		
		Bitmap carSheet = BitmapFactory.decodeResource(getResources(), R.drawable.carsheet);
		
		//canvas.drawRect(0, 0, 50, 50, new TouchPaint());
		canvas.drawBitmap(carSheet, new Rect(0, 0, 256, 461), new RectF(20, 20, 20+256, 20+461), null);
		
	}
	
}
