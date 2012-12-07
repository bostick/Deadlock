package com.gutabi.deadlock.model.menu;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;

//@SuppressWarnings("static-access")
public class MainMenu extends Menu {
	
	private final MenuItem sandboxMenuItem;
	private final MenuItem quitMenuItem;
	
	public MainMenu() {
		
		sandboxMenuItem = new MenuItem("Sandbox Mode...") {
			public void action() {
				
			}
		};
		
		quitMenuItem = new MenuItem("Quit") {
			public void action() {
				System.exit(0);
			}
		};
		
		sandboxMenuItem.down = quitMenuItem;
		sandboxMenuItem.up = quitMenuItem;
		
		quitMenuItem.down = sandboxMenuItem;
		quitMenuItem.up = sandboxMenuItem;
		
		firstMenuItem = sandboxMenuItem;
//		hilited = sandboxMenuItem;
		
	}
	
	public MenuItem hitTest(Point p) {
		
		if (sandboxMenuItem.hitTest(p)) {
			return sandboxMenuItem;
		}
		
		if (quitMenuItem.hitTest(p)) {
			return quitMenuItem;
		}
		
		return null;
	}
	
	public void render(RenderingContext ctxt) {
		
		VIEW.PIXELS_PER_METER_DEBUG = 1.0;
		
		ctxt.paintImage(0, 0, VIEW.titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
		
		ctxt.paintImage(800/2 - 498/2, 20, VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		ctxt.setColor(Color.WHITE);
		
		sandboxMenuItem.renderLocal(ctxt);
		quitMenuItem.renderLocal(ctxt);
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(800/2 - sandboxMenuItem.localAABB.width/2, 600/2 - sandboxMenuItem.localAABB.height/2);
		
//		ctxt.translate(0, 100);
		
		sandboxMenuItem.render(ctxt);
		
		ctxt.translate(0, 100);
		
		quitMenuItem.render(ctxt);
		
		ctxt.setTransform(origTransform);
		
		sandboxMenuItem.paint(ctxt);
		quitMenuItem.paint(ctxt);
		
	}
	
	public void paint(RenderingContext ctxt) {
		ctxt.paintImage(
				0, 0, VIEW.canvasMenuImage, 0, 0, VIEW.canvasMenuImage.getWidth(), VIEW.canvasMenuImage.getHeight(),
				0, 0, VIEW.canvasMenuImage.getWidth(), VIEW.canvasMenuImage.getHeight());
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}

	}
	
}
