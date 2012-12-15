package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.World;

public class QuadrantEditor extends ScreenBase {
	
	public static final int EDITOR_WIDTH = 800;
	public static final int EDITOR_HEIGHT = 600;
	
	World world;
	
	public QuadrantEditor() {
		
		world = new WorldA();
		world.canvasPostDisplay(new Dim(480, 480));
	}
	
	public void init() throws Exception {
		world.init();
	}
	
	public void canvasPostDisplay(Dim d) {
		
	}
	
	public void escKey(InputEvent ev) {
		
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
		
		world.renderCanvas();
		
	}
	
	public void repaint() {
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.translate(VIEW.canvas.getWidth()/2 - EDITOR_WIDTH/2, VIEW.canvas.getHeight()/2 - EDITOR_HEIGHT/2);
				
				paintEditor(ctxt);
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());

	}
	
	private void paintEditor(RenderingContext ctxt) {
		
		ctxt.setColor(Color.BLUE);
		ctxt.fillRect(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		
		int redWidth = 3 * EDITOR_WIDTH / 4;
		int redHeight = 3 * EDITOR_HEIGHT / 4;
		
		ctxt.setColor(Color.RED);
		ctxt.fillRect(0, 0, redWidth, redHeight);
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 3 * EDITOR_HEIGHT / 4, 3 * EDITOR_WIDTH / 4, EDITOR_HEIGHT / 4);
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(3 * EDITOR_WIDTH / 4, 0, EDITOR_WIDTH / 4, 3 * EDITOR_HEIGHT / 4);
		
		ctxt.setColor(Color.WHITE);
		ctxt.fillRect(3 * EDITOR_WIDTH / 4, 3 * EDITOR_HEIGHT / 4, EDITOR_WIDTH / 4, EDITOR_HEIGHT / 4);
		
		AffineTransform origTrans = ctxt.getTransform();
//		ctxt.translate(redWidth / 2 - 480/2, redHeight/2 - 480/2);
		ctxt.scale(APP.PIXELS_PER_METER);
		ctxt.translate(-world.worldViewport.x, -world.worldViewport.y);
		world.paintWorld(ctxt);
		ctxt.setTransform(origTrans);
	}
	
	public void paint(PaintEvent ev) {
		if (ev.c == VIEW.canvas) {
			VIEW.canvas.bs.show();
		} else {
			assert false;
		}
	}
	
}
