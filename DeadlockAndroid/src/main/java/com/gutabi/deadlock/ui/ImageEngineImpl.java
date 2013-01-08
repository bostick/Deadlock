package com.gutabi.deadlock.ui;

import java.net.URL;

public class ImageEngineImpl implements ImageEngine {
	
	public Image readImage(URL url) throws Exception {
		BufferedImage img = ImageIO.read(url);
		
		return new ImageImpl(img);
	}
	
	public Image createImage(int width, int height) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		return new ImageImpl(img);
	}
	
	public Image createTransparentImage(int width, int height) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		return new ImageImpl(img);
	}

}
