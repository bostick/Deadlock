package com.gutabi.capsloc.world.sprites;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.Resource;
import com.gutabi.capsloc.ui.Image;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class Sheet {
	
	Resource res;
	Image img;
	
	public void load() throws Exception {
		img = APP.platform.readImage(res);
	}
	
	public void paint(RenderingContext ctxt, Sprite s, int dx1, int dy1, int dx2, int dy2) {
		ctxt.paintImage(img, dx1, dy1, dx2, dy2, s.xStart(), s.yStart(), s.xEnd(), s.yEnd());
	}
	
	public void paint(RenderingContext ctxt, Sprite s, double orig, double dx1, double dy1, double dx2, double dy2) {
		ctxt.paintImage(img, orig, dx1, dy1, dx2, dy2, s.xStart(), s.yStart(), s.xEnd(), s.yEnd());
	}

}
