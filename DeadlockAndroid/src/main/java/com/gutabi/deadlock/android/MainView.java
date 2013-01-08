package com.gutabi.deadlock.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

@SuppressLint("DrawAllocation")
public class MainView extends android.view.View {
	
	public MainView(Context c, AttributeSet s) {
		super(c, s);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		canvas.drawColor(0xFFdddddd);
		
		Bitmap carSheet = BitmapFactory.decodeResource(getResources(), R.drawable.carsheet);
		
		//canvas.drawRect(0, 0, 50, 50, new TouchPaint());
		canvas.drawBitmap(carSheet, new Rect(0, 0, 256, 461), new RectF(20, 20, 20+256, 20+461), null);
		
	}
	
	public static class TouchPaint extends Paint {
		
		public TouchPaint() {
			setAntiAlias(true);
			setDither(true);
			setStrokeCap(Paint.Cap.ROUND);
			setColor(Color.GREEN);
			setStrokeWidth(15);
		}
		
	}
	
}
