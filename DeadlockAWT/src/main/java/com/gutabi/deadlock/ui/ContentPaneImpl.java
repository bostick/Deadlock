package com.gutabi.deadlock.ui;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;

//@SuppressWarnings("serial")
public class ContentPaneImpl extends ContentPaneBase implements KeyListener, MouseListener, MouseMotionListener {
	
	ContentPane keyListener;
	
	public Container j;
	
	static Logger logger = Logger.getLogger(ContentPaneImpl.class);
	
	@SuppressWarnings("serial")
	public ContentPaneImpl(ContentPane keyListener) {
		
		this.keyListener = keyListener;
		
		j = new Container() {
			public void paint(Graphics g) {
				super.paint(g);
				
				RenderingContext ctxt = APP.platform.createRenderingContext(g);
				
				for (Panel child : children) {
					child.paint(ctxt);
				}
				
			}
		};
		
		j.addKeyListener(this);
		j.addMouseListener(this);
		j.addMouseMotionListener(this);
	}
	
	public void mousePressed(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.pressed(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}

	public void mouseReleased(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.released(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void mouseDragged(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.dragged(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void mouseMoved(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		setLastMovedContentPanePoint(p);
		moved(p);
	}
	
	public void moved(Point p) {
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.moved(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
	}
	
	public void mouseClicked(MouseEvent ev) {
		Point p = new Point(ev.getX(), ev.getY());
		for (Panel child : children) {
			if (child.aabb.hitTest(p)) {
				child.clicked(new InputEvent(p.minus(child.aabb.ul)));
				return;
			}
		}
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
			keyListener.insertKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
			keyListener.deleteKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
			keyListener.escKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
			keyListener.qKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_W) {
			keyListener.wKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_G) {
			keyListener.gKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_1) {
			keyListener.d1Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_2) {
			keyListener.d2Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_3) {
			keyListener.d3Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
			keyListener.plusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
			keyListener.minusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			keyListener.downKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			keyListener.upKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			keyListener.enterKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_A) {
			keyListener.aKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_S) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				keyListener.ctrlSKey();
			} else {
				keyListener.sKey();
			}
			
		} else if (ev.getKeyCode() == KeyEvent.VK_D) {
			keyListener.dKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_F) {
			keyListener.fKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_O) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				keyListener.ctrlOKey();
			}
			
		}
	}

	public void keyTyped(KeyEvent ev) {
		;
	}
	
	public void enableKeyListener() {
		j.addKeyListener(this);
	}
	
	public void disableKeyListener() {
		j.removeKeyListener(this);
	}
	
	public void repaint() {
		j.repaint();
	}
	
}
