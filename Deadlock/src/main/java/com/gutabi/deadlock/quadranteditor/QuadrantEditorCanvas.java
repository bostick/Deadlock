package com.gutabi.deadlock.quadranteditor;

import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;

//@SuppressWarnings("serial")
public class QuadrantEditorCanvas {
	
	QuadrantEditor screen;
	
//	private BufferStrategy bs;
	
//	private java.awt.Canvas c;
	
	AABB aabb = new AABB(0, 0, 0, 0);
	
	static Logger logger = Logger.getLogger(QuadrantEditorCanvas.class);
	
	public QuadrantEditorCanvas(final QuadrantEditor screen) {
		this.screen = screen;
		
//		c = new java.awt.Canvas() {
//			public void paint(Graphics g) {
//				super.paint(g);
//				
//				RenderingContext ctxt = new RenderingContext((Graphics2D)g);
//				
//				QuadrantEditorCanvas.this.paint(ctxt);
//			}
//		};
//		
//		c.setSize(new Dimension(1584, 822));
//		c.setPreferredSize(new Dimension(1584, 822));
//		c.setMaximumSize(new Dimension(1584, 822));
//		c.setFocusable(false);
		
		aabb = new AABB(aabb.x, aabb.y, 1584, 822);
		
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
		
		return new Dim(aabb.width, aabb.height);
	}
	
//	public void repaint() {
//		c.repaint();
//	}
	
	public void paint(RenderingContext ctxt) {
	
		ctxt.cam = screen.worldScreen.cam;
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.translate(aabb.width/2 - screen.EDITOR_WIDTH/2, aabb.height/2 - screen.EDITOR_HEIGHT/2);
		
		screen.paintEditor(ctxt);
		
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
//				RenderingContext ctxt = new RenderingContext(g2);
//				ctxt.cam = screen.worldScreen.cam;
//				
//				AffineTransform origTrans = ctxt.getTransform();
//				
//				ctxt.translate(getWidth()/2 - screen.EDITOR_WIDTH/2, getHeight()/2 - screen.EDITOR_HEIGHT/2);
//				
//				screen.paintEditor(ctxt);
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
