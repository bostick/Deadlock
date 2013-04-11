package com.gutabi.bypass.ui;

import com.gutabi.deadlock.ui.Image;

import android.graphics.Bitmap;

public class ImageImpl implements Image {
	
	public Bitmap b;
	
	public ImageImpl(Bitmap b) {
		this.b = b;
	}
	
	public int getWidth() {
		return b.getWidth();
	}
	
	public int getHeight() {
		return b.getHeight();
	}
	
}
