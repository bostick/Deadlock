package com.gutabi.deadlock.ui;

import com.gutabi.deadlock.Resource;

public interface ImageEngine {
	
	public Image readImage(Resource res) throws Exception;
	
	public Image createImage(int w, int h);
	
	public Image createTransparentImage(int w, int h);
	
}
