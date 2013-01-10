package com.gutabi.deadlock.ui;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.gutabi.deadlock.Resource;
import com.gutabi.deadlock.ResourceImpl;

public class ImageEngineImpl implements ImageEngine {
	
	public Image readImage(Resource res) throws Exception {
		
		BufferedImage img = ImageIO.read(this.getClass().getResource(((ResourceImpl)res).name));
		
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
