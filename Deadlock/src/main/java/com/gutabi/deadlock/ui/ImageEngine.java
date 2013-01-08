package com.gutabi.deadlock.ui;

import java.net.URL;

public interface ImageEngine {
	
	public Image readImage(URL url) throws Exception;
	
	public Image createImage(int w, int h);
	
	public Image createTransparentImage(int w, int h);
	
}
