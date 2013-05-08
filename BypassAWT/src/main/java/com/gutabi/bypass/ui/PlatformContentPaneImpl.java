package com.gutabi.bypass.ui;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.PlatformContentPane;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class PlatformContentPaneImpl extends PlatformContentPane implements java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
	
	public Container j;
	
	@SuppressWarnings("serial")
	public PlatformContentPaneImpl() {
		
		j = new Container() {
			
			
			RenderingContext ctxt = APP.platform.createRenderingContext();
			
			public void paint(Graphics g) {
				super.paint(g);
				
				((RenderingContextImpl)ctxt).g2 = (Graphics2D)g;
				
				PlatformContentPaneImpl.this.paint(ctxt);
				
			}
		};
		
		j.addKeyListener(this);
		j.addMouseListener(this);
		j.addMouseMotionListener(this);
	}
	
	public void mousePressed(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		pressedDriver(p);
	}

	public void mouseReleased(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		releasedDriver(p);
	}
	
	public void mouseDragged(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		draggedDriver(p);
	}
	
	public void mouseMoved(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		movedDriver(p);
	}
	
	public void mouseClicked(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		clickedDriver(p);
	}

	public void mouseEntered(MouseEvent ev) {
		
	}

	public void mouseExited(MouseEvent ev) {

	}
	
	public void keyPressed(KeyEvent ev) {
		
	}

	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
			APP.tool.insertKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
			APP.tool.deleteKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
			APP.tool.escKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
			APP.tool.qKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_W) {
			APP.tool.wKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_G) {
			APP.tool.gKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_1) {
			APP.tool.d1Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_2) {
			APP.tool.d2Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_3) {
			APP.tool.d3Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
			APP.tool.plusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
			APP.tool.minusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			APP.tool.downKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			APP.tool.upKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			APP.tool.enterKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_A) {
			APP.tool.aKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_S) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & java.awt.event.InputEvent.CTRL_DOWN_MASK) == java.awt.event.InputEvent.CTRL_DOWN_MASK) {
				APP.tool.ctrlSKey();
			} else {
				APP.tool.sKey();
			}
			
		} else if (ev.getKeyCode() == KeyEvent.VK_D) {
			APP.tool.dKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_F) {
			APP.tool.fKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_O) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & java.awt.event.InputEvent.CTRL_DOWN_MASK) == java.awt.event.InputEvent.CTRL_DOWN_MASK) {
				APP.tool.ctrlOKey();
			}
			
		}
	}

	public void keyTyped(KeyEvent ev) {
		
	}
	
	public void repaint() {
		
		j.repaint();
	}
	
}
