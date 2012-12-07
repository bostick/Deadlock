package com.gutabi.deadlock.model.menu;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import com.gutabi.deadlock.controller.ControlMode;
import com.gutabi.deadlock.examples.OneByOneWorld;
import com.gutabi.deadlock.view.RenderingContext;

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
		
		MenuItem sandboxMenuItem = new MenuItem("Sandbox Mode") {
			public void action() {
				
				try {
					
					MODEL.world = new OneByOneWorld();
					
					MODEL.world.init();
					
//					VIEW.PIXELS_PER_METER_DEBUG = 12.5;
//					
//					VIEW.worldViewport = new AABB(
//							-(VIEW.CANVAS_WIDTH / VIEW.PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldWidth/2 ,
//							-(VIEW.CANVAS_HEIGHT / VIEW.PIXELS_PER_METER_DEBUG) / 2 + MODEL.world.worldHeight/2,
//							VIEW.CANVAS_WIDTH / VIEW.PIXELS_PER_METER_DEBUG,
//							VIEW.CANVAS_HEIGHT / VIEW.PIXELS_PER_METER_DEBUG);
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					VIEW.postDisplay();
					
					CONTROLLER.mode = ControlMode.IDLE;
					VIEW.renderWorldBackground();
					
					VIEW.repaintCanvas();
					VIEW.repaintControlPanel();
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(sandboxMenuItem);
		
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
	
	public void render(RenderingContext ctxt) {
		
		ctxt.paintImage(0, 0, VIEW.titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
		
		ctxt.paintImage(800/2 - 498/2, 20, VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		ctxt.paintImage(800/2 - 432/2, 550, VIEW.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
		
		int widest = 0;
		int totalHeight = 0;
		for (MenuItem item : items) {
			item.renderLocal(ctxt);
			if ((int)item.localAABB.width > widest) {
				widest = (int)item.localAABB.width;
			}
			totalHeight += (int)item.localAABB.height;
		}
		
		AffineTransform origTransform = ctxt.getTransform();
		
		ctxt.translate(800/2 - widest/2, 200);
		
		for (MenuItem item : items) {
			item.render(ctxt);
			ctxt.translate(0, item.localAABB.height + 10);
		}
		
		ctxt.setTransform(origTransform);
		
		ctxt.setColor(menuBackground);
		ctxt.fillRect((int)(800/2 - widest/2 - 5), 200 - 5, (int)(widest + 10), totalHeight + 10 * (items.size() - 1) + 5 + 5);
		
		for (MenuItem item : items) {
			item.paint(ctxt);
		}
		
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
