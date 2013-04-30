package com.gutabi.bypass.ui;

import java.awt.image.BufferedImage;

import com.gutabi.capsloc.ui.Image;

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
