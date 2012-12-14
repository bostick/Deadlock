package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import com.gutabi.deadlock.examples.FourByFourGridWorld;
import com.gutabi.deadlock.examples.OneByOneWorld;
import com.gutabi.deadlock.examples.WorldA;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.view.Canvas;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

//@SuppressWarnings("static-access")
public class MainMenu extends Menu {
	
	static Color menuBackground = new Color(0x88, 0x88, 0x88);
	
	public MainMenu() {
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Dialog A...") {
			public void action() {
				
				try {
					
					APP.screen = new QuadrantEditor();
					
					APP.screen.init();
					
					APP.screen.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(dialogMenuItem);
		
		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem);
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1") {
			public void action() {
				
				try {
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					APP.screen = new OneByOneWorld();
					
					APP.screen.init();
					
					VIEW.postDisplay();
					
					APP.screen.render();
					
					APP.screen.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid") {
			public void action() {
				
				try {
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					APP.screen = new FourByFourGridWorld();
					
					APP.screen.init();
					
					VIEW.postDisplay();
					
					APP.screen.render();
					
					APP.screen.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A") {
			public void action() {
				
				try {
					
					APP.screen = new WorldA();
					
					APP.screen.init();
					
					VIEW.teardownCanvas(VIEW.container);
					
					VIEW.setupCanvasAndControlPanel(VIEW.container);
					
					((JFrame)VIEW.container).setVisible(true);
					VIEW.canvas.requestFocusInWindow();
					
					VIEW.postDisplay();
					
					APP.screen.render();
					
					APP.screen.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		add(aMenuItem);
		
		MenuItem quadrantEditorMenuItem = new MenuItem(MainMenu.this, "Quadrant Editor...") {
			public void action() {
				
			}
		};
		add(quadrantEditorMenuItem);
		
		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem);
		
		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem);
		
		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
			public void action() {
				System.exit(0);
			}
		};
		add(quitMenuItem);
		
	}
	
	public void init() {
		
	}
	
	public void canvasPostDisplay() {
		
	}
	
	public void render() {
		
		synchronized (VIEW) {
			Graphics2D canvasMenuImageG2 = canvasMenuImage.createGraphics();
			
			RenderingContext canvasMenuContext = new RenderingContext(canvasMenuImageG2, RenderingContextType.CANVAS);
			
			canvasMenuContext.paintImage(0, 0, VIEW.titleBackground, 0, 0, 800, 600, 0, 0, 800, 600);
			
			canvasMenuContext.paintImage(800/2 - 498/2, 20, VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
			
			canvasMenuContext.paintImage(800/2 - 432/2, 550, VIEW.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
			
			int totalHeight = 0;
			for (MenuItem item : items) {
				item.renderLocal(canvasMenuContext);
				if ((int)item.localAABB.width > widest) {
					widest = (int)item.localAABB.width;
				}
				totalHeight += (int)item.localAABB.height;
			}
			
			AffineTransform origTransform = canvasMenuContext.getTransform();
			
			canvasMenuContext.translate(800/2 - widest/2, 150);
			
			for (MenuItem item : items) {
				item.render(canvasMenuContext);
				canvasMenuContext.translate(0, item.localAABB.height + 10);
			}
			
			canvasMenuContext.setTransform(origTransform);
			
			canvasMenuContext.setColor(menuBackground);
			canvasMenuContext.fillRect((int)(800/2 - widest/2 - 5), 150 - 5, (int)(widest + 10), totalHeight + 10 * (items.size() - 1) + 5 + 5);
			
			for (MenuItem item : items) {
				item.paint(canvasMenuContext);
			}
			
			canvasMenuImageG2.dispose();
		}
		
	}
	
	public void repaint() {
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)VIEW.canvas.bs.getDrawGraphics();
				
				RenderingContext ctxt = new RenderingContext(g2, RenderingContextType.CANVAS);
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.translate(VIEW.canvas.getWidth()/2 - 800/2, VIEW.canvas.getHeight()/2 - 600/2);
				
				ctxt.paintImage(
						0, 0, canvasMenuImage, 0, 0, canvasMenuImage.getWidth(), canvasMenuImage.getHeight(),
						0, 0, canvasMenuImage.getWidth(), canvasMenuImage.getHeight());
				
				if (hilited != null) {
					hilited.paintHilited(ctxt);			
				}
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (VIEW.canvas.bs.contentsRestored());
			
			VIEW.canvas.bs.show();
			
		} while (VIEW.canvas.bs.contentsLost());

	}
	
	public void paint(PaintEvent ev) {
		if (ev.c instanceof Canvas) {
			VIEW.canvas.bs.show();
		}
	}
	
}
