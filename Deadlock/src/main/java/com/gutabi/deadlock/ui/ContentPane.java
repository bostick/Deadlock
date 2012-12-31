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

import com.gutabi.deadlock.Screen;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("serial")
public class ContentPane extends Container {
	
	Screen screen;
	
	protected List<Panel> children = new ArrayList<Panel>();
	
	private JavaListener jl;
	
	static Logger logger = Logger.getLogger(ContentPane.class);
	
	public ContentPane(Screen screen) {
		this.screen = screen;
		
		jl = new JavaListener();
		addKeyListener(jl);
		addMouseListener(jl);
		addMouseMotionListener(jl);
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
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
			Point p = new Point(ev.getX(), ev.getY());
			for (Panel child : children) {
				if (child.aabb.hitTest(p)) {
					child.exited(new InputEvent(p.minus(child.aabb.ul)));
					return;
				}
			}
		}
		
		public void keyPressed(KeyEvent ev) {
			;
		}

		public void keyReleased(KeyEvent ev) {
			if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
				screen.insertKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
				screen.deleteKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
				screen.escKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
				screen.qKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_W) {
				screen.wKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_G) {
				screen.gKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_1) {
				screen.d1Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_2) {
				screen.d2Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_3) {
				screen.d3Key();
			} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
				screen.plusKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
				screen.minusKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
				screen.downKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				screen.upKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				screen.enterKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_A) {
				screen.aKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_S) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					screen.ctrlSKey();
				} else {
					screen.sKey();
				}
				
			} else if (ev.getKeyCode() == KeyEvent.VK_D) {
				screen.dKey();
			} else if (ev.getKeyCode() == KeyEvent.VK_O) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					screen.ctrlOKey();
				}
				
			}
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
	}
	
	public void enableKeyListener() {
		addKeyListener(jl);
	}
	
	public void disableKeyListener() {
		removeKeyListener(jl);
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
