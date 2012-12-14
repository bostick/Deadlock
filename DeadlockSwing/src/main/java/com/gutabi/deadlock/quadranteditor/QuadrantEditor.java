package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.Canvas;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

public class QuadrantEditor extends ScreenBase {
	
	public void init() {
		
	}
	
	public void canvasPostDisplay() {
		
	}
	
	public void escKey() {
		
		try {
			
			APP.PIXELS_PER_METER = 1.0;
			APP.screen = new MainMenu();
			APP.screen.init();
			
			APP.screen.render();
			APP.screen.repaint();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void render() {
		
	}
	
	public void repaint() {
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.translate(VIEW.canvas.getWidth()/2 - 800/2, VIEW.canvas.getHeight()/2 - 600/2);
				
				paintEditor(ctxt);
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());

	}
	
	private void paintEditor(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLUE);
		ctxt.fillRect(0, 0, 800, 600);
		
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c instanceof Canvas) {
			VIEW.canvas.bs.show();
		}
	}
	
}
