package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.RenderingContext;

public class MenuCanvas extends PanelBase {
	
	public final int MENU_WIDTH = 800;
	public final int MENU_HEIGHT = 600;
	
	MainMenu screen;
	
	static Logger logger = Logger.getLogger(MenuCanvas.class);
	
	public MenuCanvas(final MainMenu screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, 1584, 822);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	public Point lastMovedMenuPoint;
	Point lastClickedCanvasPoint;
	
	Point lastClickedMenuPoint;
	
	public Point canvasToMenu(Point p) {
		return new Point(p.x - (aabb.width/2 - MENU_WIDTH/2), p.y - (aabb.height/2 - MENU_HEIGHT/2));
	}
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : screen.items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		Point p = ev.p;
		
		lastMovedMenuPoint = canvasToMenu(p);
		
		MenuItem hit = hitTest(lastMovedMenuPoint);
		if (hit != null && hit.active) {
			screen.hilited = hit;
		} else {
			screen.hilited = null;
		}
		
		screen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = canvasToMenu(ev.p);
		
		MenuItem item = hitTest(lastClickedMenuPoint);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public void render() {
//		logger.debug("render");
		
		synchronized (APP) {
			
			BufferedImage canvasMenuImage = new BufferedImage(MENU_WIDTH, MENU_HEIGHT, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D canvasMenuImageG2 = canvasMenuImage.createGraphics();
			
			for (MenuItem item : screen.items) {
				if ((int)item.localAABB.width > screen.widest) {
					screen.widest = (int)item.localAABB.width;
				}
				screen.totalHeight += (int)item.localAABB.height;
			}
			
			RenderingContext canvasMenuContext = new RenderingContext(canvasMenuImageG2);
			
			AffineTransform origTransform = canvasMenuContext.getTransform();
			
			canvasMenuContext.translate(MENU_WIDTH/2 - screen.widest/2, 150);
			
			for (MenuItem item : screen.items) {
				item.render(canvasMenuContext);
				canvasMenuContext.translate(0, item.localAABB.height + 10);
			}
			
			canvasMenuContext.setTransform(origTransform);
			
			canvasMenuImageG2.dispose();
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.setColor(Color.DARK_GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.translate(aabb.width/2 - MENU_WIDTH/2, aabb.height/2 - MENU_HEIGHT/2);
		
		AffineTransform menuTrans = ctxt.getTransform();
		
		ctxt.paintImage(APP.titleBackground, 0, 0, MENU_WIDTH, MENU_HEIGHT, 0, 0, MENU_WIDTH, MENU_HEIGHT);
		
		ctxt.translate(MENU_WIDTH/2 - 498/2, 20);
		ctxt.paintImage(APP.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.translate(MENU_WIDTH/2 - 432/2, 550);
		ctxt.paintImage(APP.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
		
		ctxt.setTransform(menuTrans);
		
		ctxt.setColor(APP.menuBackground);
		ctxt.fillRect((int)(MENU_WIDTH/2 - screen.widest/2 - 5), 150 - 5, (int)(screen.widest + 10), screen.totalHeight + 10 * (screen.items.size() - 1) + 5 + 5);
		
		for (MenuItem item : screen.items) {
			item.paint(ctxt);
		}
		
		if (screen.hilited != null) {
			screen.hilited.paintHilited(ctxt);			
		}
		
		ctxt.setTransform(origTrans);
		
	}
	
}
