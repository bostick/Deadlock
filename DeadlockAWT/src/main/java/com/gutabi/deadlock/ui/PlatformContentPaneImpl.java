package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class PlatformContentPaneImpl extends PlatformContentPane implements java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
	
	KeyListener kl;
	
	public Container j;
	
	static Logger logger = Logger.getLogger(PlatformContentPaneImpl.class);
	
	@SuppressWarnings("serial")
	public PlatformContentPaneImpl(KeyListener kl) {
		
		this.kl = kl;
		assert kl != null;
		
		j = new Container() {
			public void paint(Graphics g) {
				super.paint(g);
				
				RenderingContext ctxt = APP.platform.createRenderingContext(g);
				
				PlatformContentPaneImpl.this.paint(ctxt);
				
			}
		};
		
		j.addKeyListener(this);
		j.addMouseListener(this);
		j.addMouseMotionListener(this);
	}
	
	public void mousePressed(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		pressed(new InputEvent(p));
	}

	public void mouseReleased(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		released(new InputEvent(p));
	}
	
	public void mouseDragged(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		dragged(new InputEvent(p));
	}
	
	public void mouseMoved(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		moved(new InputEvent(p));
	}
	
	public void mouseClicked(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		clicked(new InputEvent(p));
	}

	public void mouseEntered(MouseEvent ev) {
		
	}

	public void mouseExited(MouseEvent ev) {

	}
	
	public void keyPressed(KeyEvent ev) {
		;
	}

	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
			kl.insertKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
			kl.deleteKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
			kl.escKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
			kl.qKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_W) {
			kl.wKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_G) {
			kl.gKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_1) {
			kl.d1Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_2) {
			kl.d2Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_3) {
			kl.d3Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
			kl.plusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
			kl.minusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			kl.downKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			kl.upKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			kl.enterKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_A) {
			kl.aKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_S) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				kl.ctrlSKey();
			} else {
				kl.sKey();
			}
			
		} else if (ev.getKeyCode() == KeyEvent.VK_D) {
			kl.dKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_F) {
			kl.fKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_O) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				kl.ctrlOKey();
			}
			
		}
	}

	public void keyTyped(KeyEvent ev) {
		;
	}
	
	public void repaint() {
		
		j.repaint();
	}
	
}
