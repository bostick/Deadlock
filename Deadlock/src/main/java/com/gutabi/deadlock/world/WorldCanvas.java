package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Entity;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.ComponentBase;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen.WorldScreenMode;

@SuppressWarnings("serial")
public class WorldCanvas extends ComponentBase {
	
	WorldScreen screen;
	
	private BufferStrategy bs;
	
	private java.awt.Canvas c;
	private JavaListener jl;
	
	static Logger logger = Logger.getLogger(WorldCanvas.class);
	
	public WorldCanvas(final WorldScreen screen) {
		this.screen = screen;
		
		c = new java.awt.Canvas() {
			public void paint(Graphics g) {
				WorldCanvas.this.repaint();
			}
		};
		
		c.setSize(new Dimension(1384, 822));
		c.setPreferredSize(new Dimension(1384, 822));
		c.setMaximumSize(new Dimension(1384, 822));
		
		jl = new JavaListener();
		c.addMouseListener(jl);
		c.addMouseMotionListener(jl);
		c.addKeyListener(jl);
	}
	
	public void enableKeyListener() {
		c.addKeyListener(jl);
	}
	
	public void disableKeyListener() {
		c.removeKeyListener(jl);
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
		public void keyPressed(KeyEvent ev) {
			;
		}

		public void keyReleased(KeyEvent ev) {
			if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
				insertKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
				qKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_W) {
				wKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_G) {
				gKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_1) {
				d1Key(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_2) {
				d2Key(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_3) {
				d3Key(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
				plusKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
				minusKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
				downKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				upKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				enterKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_A) {
				aKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_S) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					ctrlSKey(new InputEvent(WorldCanvas.this, null));
				} else {
					sKey(new InputEvent(WorldCanvas.this, null));
				}
				
			} else if (ev.getKeyCode() == KeyEvent.VK_D) {
				dKey(new InputEvent(WorldCanvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_O) {
				
				int mods = ev.getModifiersEx();
				
				if ((mods & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
					ctrlOKey(new InputEvent(WorldCanvas.this, null));
				}
				
			}
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
		public void mousePressed(MouseEvent ev) {
			pressed(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseReleased(MouseEvent ev) {
			released(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}
		
		public void mouseDragged(MouseEvent ev) {
			dragged(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseMoved(MouseEvent ev) {
			moved(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseClicked(MouseEvent ev) {
			clicked(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseEntered(MouseEvent ev) {
			entered(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseExited(MouseEvent ev) {
			exited(new InputEvent(WorldCanvas.this, new Point(ev.getX(), ev.getY())));
		}
		
	}
	
	public int getWidth() {
		return c.getWidth();
	}
	
	public int getHeight() {
		return c.getHeight();
	}
	
	public java.awt.Canvas java() {
		return c;
	}
	
	public Dim postDisplay() {
		
		c.requestFocusInWindow();
		
		c.createBufferStrategy(2);
		bs = c.getBufferStrategy();
		
		return new Dim(getWidth(), getHeight());
	}
	
	public void pressed(InputEvent ev) {
		screen.pressed(ev);
	}
	
	public void dragged(InputEvent ev) {
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		screen.dragged(ev);
	}
	
	public void released(InputEvent ev) {
		
		screen.released(ev);
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		screen.moved(ev);
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		screen.clicked(ev);
	}
	
	public void entered(InputEvent ev) {
		
	}
	
	public void exited(InputEvent ev) {
		screen.exited(ev);
	}
	
	public void qKey(InputEvent ev) {
		screen.qKey(ev);
	}
	
	public void wKey(InputEvent ev) {
		screen.wKey(ev);
	}
	
	public void gKey(InputEvent ev) {
		screen.gKey(ev);
	}
	
	public void deleteKey(InputEvent ev) {
		screen.deleteKey(ev);
	}
	
	public void insertKey(InputEvent ev) {
		screen.insertKey(ev);
	}
	
	public void escKey(InputEvent ev) {
		screen.escKey(ev);
	}
	
	public void d1Key(InputEvent ev) {
		screen.d1Key(ev);
	}
	
	public void d2Key(InputEvent ev) {
		screen.d2Key(ev);
	}
	
	public void d3Key(InputEvent ev) {
		screen.d3Key(ev);
	}
	
	public void plusKey(InputEvent ev) {
		screen.plusKey(ev);
	}
	
	public void minusKey(InputEvent ev) {
		screen.minusKey(ev);
	}
	
	public void downKey(InputEvent ev) {
		screen.downKey(ev);
	}

	public void upKey(InputEvent ev) {
		screen.upKey(ev);
	}
	
	public void enterKey(InputEvent ev) {
		screen.enterKey(ev);
	}
	
	public void aKey(InputEvent ev) {
		screen.aKey(ev);
	}
	
	public void sKey(InputEvent ev) {
		screen.sKey(ev);
	}
	
	public void ctrlSKey(InputEvent ev) {
		screen.ctrlSKey(ev);
	}
	
	public void dKey(InputEvent ev) {
		screen.dKey(ev);
	}
	
	public void ctrlOKey(InputEvent ev) {
		screen.ctrlOKey(ev);
	}
	
	
	RenderingContext ctxt = new RenderingContext();
	
	public void repaint() {
		
		if (SwingUtilities.isEventDispatchThread()) {
			if (screen.mode == WorldScreenMode.RUNNING) {
				return;
			}
		}
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)bs.getDrawGraphics();
				
				ctxt.g2 = g2;
				ctxt.cam = screen.cam;
				ctxt.FPS_DRAW = screen.FPS_DRAW;
				
				//synchronized (VIEW) {
				paint_canvas(ctxt);
				//}
				
				g2.dispose();
				
			} while (bs.contentsRestored());
			
			bs.show();
			
		} while (bs.contentsLost());
		
	}
	
	private void paint_canvas(RenderingContext ctxt) {
		
		screen.world.paint_canvas(ctxt);
		
		AffineTransform origTrans = ctxt.getTransform();
		
		ctxt.scale(ctxt.cam.pixelsPerMeter);
		ctxt.translate(-ctxt.cam.worldViewport.x, -ctxt.cam.worldViewport.y);
		
		Entity hilitedCopy;
		synchronized (APP) {
			hilitedCopy = screen.hilited;
		}
		
		if (hilitedCopy != null) {
			hilitedCopy.paintHilite(ctxt);
		}
		
		screen.tool.draw(ctxt);
		
		if (ctxt.FPS_DRAW) {
			
			ctxt.translate(ctxt.cam.worldViewport.x, ctxt.cam.worldViewport.y);
			
			screen.stats.paint(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
