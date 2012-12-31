package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("serial")
public class MenuCanvas {
	
//	int width = 1584;
//	int height = 822;
	
	MainMenu screen;
	
	AABB aabb = new AABB(0, 0, 1584, 822);
	
//	private BufferStrategy bs;
	
//	private java.awt.Canvas c;
	
	static Logger logger = Logger.getLogger(MenuCanvas.class);
	
	public MenuCanvas(final MainMenu screen) {
		this.screen = screen;
		
//		c = new java.awt.Canvas() {
//			public void paint(Graphics g) {
//				super.paint(g);
//				
//				RenderingContext ctxt = new RenderingContext((Graphics2D)g);
//				
//				MenuCanvas.this.paint(ctxt);
//				
//			}
//		};
		
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
//	public int getWidth() {
//		return c.getWidth();
//	}
//	
//	public int getHeight() {
//		return c.getHeight();
//	}
//	
//	public java.awt.Canvas java() {
//		return c;
//	}
	
	public Dim postDisplay() {
		
//		c.requestFocusInWindow();
		
//		c.createBufferStrategy(2);
//		bs = c.getBufferStrategy();
		
//		c.setSize(new Dimension(1584, 822));
//		c.setPreferredSize(new Dimension(1584, 822));
//		c.setMaximumSize(new Dimension(1584, 822));
		
//		return new Dim(getWidth(), getHeight());
		return new Dim(aabb.width, aabb.height);
	}
	
	public void render() {
//		logger.debug("render");
		
		synchronized (APP) {
			
			BufferedImage canvasMenuImage = new BufferedImage(screen.MENU_WIDTH, screen.MENU_HEIGHT, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D canvasMenuImageG2 = canvasMenuImage.createGraphics();
			
			for (MenuItem item : screen.items) {
				if ((int)item.localAABB.width > screen.widest) {
					screen.widest = (int)item.localAABB.width;
				}
				screen.totalHeight += (int)item.localAABB.height;
			}
			
			RenderingContext canvasMenuContext = new RenderingContext(canvasMenuImageG2);
			
			AffineTransform origTransform = canvasMenuContext.getTransform();
			
			canvasMenuContext.translate(screen.MENU_WIDTH/2 - screen.widest/2, 150);
			
			for (MenuItem item : screen.items) {
				item.render(canvasMenuContext);
				canvasMenuContext.translate(0, item.localAABB.height + 10);
			}
			
			canvasMenuContext.setTransform(origTransform);
			
			canvasMenuImageG2.dispose();
		}
		
	}
	
//	public void repaint() {
//		c.repaint();
//	}
	
	void paint(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.translate(aabb.width/2 - screen.MENU_WIDTH/2, aabb.height/2 - screen.MENU_HEIGHT/2);
		
		AffineTransform menuTrans = ctxt.getTransform();
		
		ctxt.paintImage(APP.titleBackground, 0, 0, screen.MENU_WIDTH, screen.MENU_HEIGHT, 0, 0, screen.MENU_WIDTH, screen.MENU_HEIGHT);
		
		ctxt.translate(screen.MENU_WIDTH/2 - 498/2, 20);
		ctxt.paintImage(APP.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(screen.MENU_WIDTH/2 - 432/2, 550);
		ctxt.paintImage(APP.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.setColor(APP.menuBackground);
		ctxt.fillRect((int)(screen.MENU_WIDTH/2 - screen.widest/2 - 5), 150 - 5, (int)(screen.widest + 10), screen.totalHeight + 10 * (screen.items.size() - 1) + 5 + 5);
		
		for (MenuItem item : screen.items) {
			item.paint(ctxt);
		}
		
		if (screen.hilited != null) {
			screen.hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
//	public void repaint() {
//		
//		do {
//			
//			do {
//				
//				Graphics2D g2 = (Graphics2D)bs.getDrawGraphics();
//				
//				g2.translate(aabb.x, aabb.y);
//				
//				g2.setColor(Color.DARK_GRAY);
//				g2.fillRect(0, 0, screen.canvasWidth, screen.canvasHeight);
//				
//				RenderingContext ctxt = new RenderingContext(g2);
//				
//				AffineTransform origTrans = ctxt.getTransform();
//				
//				ctxt.translate(getWidth()/2 - screen.MENU_WIDTH/2, getHeight()/2 - screen.MENU_HEIGHT/2);
//				
//				AffineTransform menuTrans = ctxt.getTransform();
//				
//				ctxt.paintImage(APP.titleBackground, 0, 0, screen.MENU_WIDTH, screen.MENU_HEIGHT, 0, 0, screen.MENU_WIDTH, screen.MENU_HEIGHT);
//				
//				ctxt.translate(screen.MENU_WIDTH/2 - 498/2, 20);
//				ctxt.paintImage(APP.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
//				
//				ctxt.setTransform(menuTrans);
//				
//				ctxt.translate(screen.MENU_WIDTH/2 - 432/2, 550);
//				ctxt.paintImage(APP.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
//				
//				ctxt.setTransform(menuTrans);
//				
//				ctxt.setColor(APP.menuBackground);
//				ctxt.fillRect((int)(screen.MENU_WIDTH/2 - screen.widest/2 - 5), 150 - 5, (int)(screen.widest + 10), screen.totalHeight + 10 * (screen.items.size() - 1) + 5 + 5);
//				
//				for (MenuItem item : screen.items) {
//					item.paint(ctxt);
//				}
//				
//				if (screen.hilited != null) {
//					screen.hilited.paintHilited(ctxt);			
//				}
//				
//				ctxt.setTransform(origTrans);
//				
//				g2.dispose();
//				
//			} while (bs.contentsRestored());
//			
//			bs.show();
//			
//		} while (bs.contentsLost());
//
//	}
	
}
