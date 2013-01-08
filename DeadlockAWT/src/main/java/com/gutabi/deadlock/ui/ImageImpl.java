package com.gutabi.deadlock.ui;

import java.awt.image.BufferedImage;

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

}
