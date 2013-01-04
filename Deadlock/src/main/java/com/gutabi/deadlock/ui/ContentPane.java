package com.gutabi.deadlock.ui;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Point;

@SuppressWarnings("serial")
public class ContentPane extends Container implements KeyListener, MouseListener, MouseMotionListener {
	
	protected List<Panel> children = new ArrayList<Panel>();
	
//	private JavaListener jl;
	
	static Logger logger = Logger.getLogger(ContentPane.class);
	
	public ContentPane() {
		
//		jl = new JavaListener();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
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

	
	public Point lastMovedContentPanePoint;
	
	public void mouseMoved(MouseEvent ev) {
		lastMovedContentPanePoint = new Point(ev.getX(), ev.getY());
		moved(lastMovedContentPanePoint);
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
//		Point p = new Point(ev.getX(), ev.getY());
//		for (Panel child : children) {
//			if (child.aabb.hitTest(p)) {
//				child.exited(new InputEvent(p.minus(child.aabb.ul)));
//				return;
//			}
//		}
	}
	
	public void keyPressed(KeyEvent ev) {
		;
	}

	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
			insertKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
			escKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
			qKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_W) {
			wKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_G) {
			gKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_1) {
			d1Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_2) {
			d2Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_3) {
			d3Key();
		} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
			plusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
			minusKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			downKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			upKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			enterKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_A) {
			aKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_S) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				ctrlSKey();
			} else {
				sKey();
			}
			
		} else if (ev.getKeyCode() == KeyEvent.VK_D) {
			dKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_F) {
			fKey();
		} else if (ev.getKeyCode() == KeyEvent.VK_O) {
			
			int mods = ev.getModifiersEx();
			
			if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				ctrlOKey();
			}
			
		}
	}

	public void keyTyped(KeyEvent ev) {
		;
	}
	
	public void enableKeyListener() {
		addKeyListener(this);
	}
	
	public void disableKeyListener() {
		removeKeyListener(this);
	}
	
	public void ctrlOKey() {
		// TODO Auto-generated method stub
		
	}

	public void dKey() {
		// TODO Auto-generated method stub
		
	}

	public void upKey() {
		// TODO Auto-generated method stub
		
	}

	public void enterKey() {
		// TODO Auto-generated method stub
		
	}

	public void aKey() {
		// TODO Auto-generated method stub
		
	}

	public void sKey() {
		// TODO Auto-generated method stub
		
	}

	public void ctrlSKey() {
		// TODO Auto-generated method stub
		
	}

	public void downKey() {
		// TODO Auto-generated method stub
		
	}

	public void minusKey() {
		// TODO Auto-generated method stub
		
	}

	public void plusKey() {
		// TODO Auto-generated method stub
		
	}

	public void d3Key() {
		// TODO Auto-generated method stub
		
	}

	public void d2Key() {
		// TODO Auto-generated method stub
		
	}

	public void d1Key() {
		// TODO Auto-generated method stub
		
	}

	public void gKey() {
		// TODO Auto-generated method stub
		
	}

	public void wKey() {
		// TODO Auto-generated method stub
		
	}

	public void qKey() {
		// TODO Auto-generated method stub
		
	}

	public void escKey() {
		// TODO Auto-generated method stub
		
	}

	public void deleteKey() {
		// TODO Auto-generated method stub
		
	}

	public void insertKey() {
		// TODO Auto-generated method stub
		
	}
	
	public void fKey() {
		
	}
	
	
	
	
	public void postDisplay() {
		
		for (Panel child : children) {
			child.postDisplay();
		}
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		RenderingContext ctxt = new RenderingContext((Graphics2D)g);
		
		for (Panel child : children) {
			child.paint(ctxt);
		}
		
	}
}
