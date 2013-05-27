package com.brentonbostick.bypass.awt.ui;

import java.awt.image.BufferedImage;

import com.brentonbostick.capsloc.ui.Image;

public class ImageImpl implements Image {

	public BufferedImage img;
	
	public ImageImpl(BufferedImage img) {
		this.img = img;
	}
	
	public int getWidth() {
		return img.getWidth();
	}

	public int getHeight() {
		return img.getHeight();
	}
	
	public void dispose() {
		img = null;
	}
}
