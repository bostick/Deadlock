package com.gutabi.deadlock.model.menu;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Font;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

@SuppressWarnings("static-access")
public class MainMenu extends Menu {
	
	private final MenuItem sandboxMenuItem;
	private final MenuItem quitMenuItem;
	
	private Font f = new Font("Times", Font.PLAIN, 96);
	
	public MainMenu() {
		
		sandboxMenuItem = new MenuItem("Sandbox Mode...", new Point(VIEW.CANVAS_WIDTH/2 - 800/2, VIEW.CANVAS_HEIGHT/2));
		
		quitMenuItem = new MenuItem("Quit", new Point(VIEW.CANVAS_WIDTH/2 - 800/2, VIEW.CANVAS_HEIGHT/2 + 100));
		
		sandboxMenuItem.down = quitMenuItem;
		sandboxMenuItem.up = quitMenuItem;
		
		quitMenuItem.down = sandboxMenuItem;
		quitMenuItem.up = sandboxMenuItem;
		
		firstMenuItem = sandboxMenuItem;
//		hilited = sandboxMenuItem;
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		VIEW.PIXELS_PER_METER_DEBUG = 1.0;
		
		ctxt.paintImage(VIEW.CANVAS_WIDTH/2 - 800/2, VIEW.CANVAS_HEIGHT/2 - 600/2, VIEW.titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
		
		ctxt.paintImage(VIEW.CANVAS_WIDTH/2 - 498/2, VIEW.CANVAS_HEIGHT/2 - 600/2 + 20, VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		Font origFont = ctxt.getFont();
		ctxt.setFont(f);
		
		ctxt.setColor(Color.WHITE);
		
		sandboxMenuItem.paint(ctxt);
		
		quitMenuItem.paint(ctxt);
		
		if (hilited != null) {
			
			hilited.paintHilited(ctxt);
			
		}
		
		ctxt.setFont(origFont);
		
	}
	
}
