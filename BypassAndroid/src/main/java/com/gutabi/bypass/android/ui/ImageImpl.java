package com.gutabi.bypass.android.ui;

import android.graphics.Bitmap;

import com.gutabi.capsloc.ui.Image;

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
