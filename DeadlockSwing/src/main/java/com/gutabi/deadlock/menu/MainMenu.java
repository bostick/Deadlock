package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.examples.FourByFourGridWorld;
import com.gutabi.deadlock.examples.OneByOneWorld;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.Stroke;

//@SuppressWarnings("static-access")
public class MainMenu extends Menu {
	
	static Color menuBackground = new Color(0x88, 0x88, 0x88);
	
	public MainMenu() {
		
		MenuItem puzzleMenuItem = new MenuItem("Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem);
		
		MenuItem oneMenuItem = new MenuItem("1x1") {
			public void action() {
				
				try {
					
					APP.world = new OneByOneWorld();
					APP.world.init();
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					CONTROLLER.mode = ControlMode.WORLD;
					
					VIEW.postDisplay();
					
					APP.world.render();
					
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
					Stroke s = new Stroke();
					s.add(new Point(1, 1));
					s.add(new Point(10, 10));
					s.add(new Point(10, 2));
					s.add(new Point(2, 10));
					s.add(new Point(-6, 18));
					s.add(new Point(0, 18));
					s.add(new Point(6, 18));
					s.add(new Point(0, 14));
					s.add(new Point(-6, 10));
					
					s.finish();
					
					APP.world.processNewStroke(s);
					
					APP.world.render();
					
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem("4x4 Grid") {
			public void action() {
				
				try {
					
					APP.world = new FourByFourGridWorld();
					APP.world.init();
					
					APP.world.PIXELS_PER_METER_DEBUG = 12.5;
					
					APP.world.worldViewport = new AABB(
							-(VIEW.canvas.getWidth() / APP.world.PIXELS_PER_METER_DEBUG) / 2 + APP.world.worldWidth/2 ,
							-(VIEW.canvas.getHeight() / APP.world.PIXELS_PER_METER_DEBUG) / 2 + APP.world.worldHeight/2,
							VIEW.canvas.getWidth() / APP.world.PIXELS_PER_METER_DEBUG,
							VIEW.canvas.getHeight() / APP.world.PIXELS_PER_METER_DEBUG);
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					CONTROLLER.mode = ControlMode.WORLD;
					
					VIEW.postDisplay();
					
					APP.world.render();
					
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(fourMenuItem);
		
		MenuItem editorMenuItem = new MenuItem("Map Editor...") {
			public void action() {
				
			}
		};
//		editorMenuItem.active = false;
		add(editorMenuItem);
		
		MenuItem loadMenuItem = new MenuItem("Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem);
		
		MenuItem captureMenuItem = new MenuItem("Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem);
		
		MenuItem quitMenuItem = new MenuItem("Quit") {
			public void action() {
				System.exit(0);
			}
		};
		add(quitMenuItem);
		
	}
	
	public void render() {
		
		synchronized (VIEW) {
			Graphics2D canvasMenuImageG2 = canvasMenuImage.createGraphics();
			
			RenderingContext canvasMenuContext = new RenderingContext(canvasMenuImageG2, RenderingContextType.CANVAS);
			
			canvasMenuContext.paintImage(0, 0, VIEW.titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
			
			canvasMenuContext.paintImage(800/2 - 498/2, 20, VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
			
			canvasMenuContext.paintImage(800/2 - 432/2, 550, VIEW.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
			
			int widest = 0;
			int totalHeight = 0;
			for (MenuItem item : items) {
				item.renderLocal(canvasMenuContext);
				if ((int)item.localAABB.width > widest) {
					widest = (int)item.localAABB.width;
				}
				totalHeight += (int)item.localAABB.height;
			}
			
			AffineTransform origTransform = canvasMenuContext.getTransform();
			
			canvasMenuContext.translate(800/2 - widest/2, 200);
			
			for (MenuItem item : items) {
				item.render(canvasMenuContext);
				canvasMenuContext.translate(0, item.localAABB.height + 10);
			}
			
			canvasMenuContext.setTransform(origTransform);
			
			canvasMenuContext.setColor(menuBackground);
			canvasMenuContext.fillRect((int)(800/2 - widest/2 - 5), 200 - 5, (int)(widest + 10), totalHeight + 10 * (items.size() - 1) + 5 + 5);
			
			for (MenuItem item : items) {
				item.paint(canvasMenuContext);
			}
			
			canvasMenuImageG2.dispose();
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(VIEW.canvas.getWidth()/2 - 800/2, VIEW.canvas.getHeight()/2 - 600/2);
		
		ctxt.paintImage(
				0, 0, canvasMenuImage, 0, 0, canvasMenuImage.getWidth(), canvasMenuImage.getHeight(),
				0, 0, canvasMenuImage.getWidth(), canvasMenuImage.getHeight());
		
		if (hilited != null) {
			hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTrans);

	}
	
}
