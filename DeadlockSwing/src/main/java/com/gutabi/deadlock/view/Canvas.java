package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;

@SuppressWarnings("serial")
//@SuppressWarnings({"serial", "static-access"})
public class Canvas extends Component {
	
	public BufferStrategy bs;
	
	private java.awt.Canvas c;
	
	static Logger logger = Logger.getLogger(Canvas.class);
	
	public Canvas() {
		
		c = new java.awt.Canvas() {
			public void paint(Graphics g) {
				logger.debug("paint");
				APP.screen.paint(new PaintEvent(Canvas.this, new RenderingContext((Graphics2D)g, RenderingContextType.CANVAS)));
			}
		};
		c.setFocusable(true);
		JavaListener jl = new JavaListener();
		c.addMouseListener(jl);
		c.addMouseMotionListener(jl);
		c.addKeyListener(jl);
	}
	
	class JavaListener implements KeyListener, MouseListener, MouseMotionListener {
		
		public void keyPressed(KeyEvent ev) {
			;
		}

		public void keyReleased(KeyEvent ev) {
			if (ev.getKeyCode() == KeyEvent.VK_INSERT) {
				insertKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_Q) {
				qKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_W) {
				wKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_G) {
				gKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_1) {
				d1Key(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_2) {
				d2Key(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_3) {
				d3Key(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_PLUS || ev.getKeyCode() == KeyEvent.VK_EQUALS) {
				plusKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_MINUS) {
				minusKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
				downKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
				upKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
				enterKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_A) {
				aKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_S) {
				sKey(new InputEvent(Canvas.this, null));
			} else if (ev.getKeyCode() == KeyEvent.VK_D) {
				dKey(new InputEvent(Canvas.this, null));
			}
		}

		public void keyTyped(KeyEvent ev) {
			;
		}
		
		public void mousePressed(MouseEvent ev) {
			pressed(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseReleased(MouseEvent ev) {
			released(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}
		
		public void mouseDragged(MouseEvent ev) {
			dragged(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseMoved(MouseEvent ev) {
			moved(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseClicked(MouseEvent ev) {
			clicked(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseEntered(MouseEvent ev) {
			entered(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
		}

		public void mouseExited(MouseEvent ev) {
			exited(new InputEvent(Canvas.this, new Point(ev.getX(), ev.getY())));
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
	
	public void canvasPostDisplay() {
		
		c.createBufferStrategy(2);
		bs = c.getBufferStrategy();
		
		APP.screen.canvasPostDisplay(new Dim(getWidth(), getHeight()));
	}
	
	public void pressed(InputEvent ev) {
		
		c.requestFocusInWindow();
		
		APP.screen.pressed(ev);
	}
	
	public void dragged(InputEvent ev) {
		
		c.requestFocusInWindow();
		
		lastMovedOrDraggedCanvasPoint = ev.p;
		
		APP.screen.dragged(ev);
	}
	
	public void released(InputEvent ev) {
		
		c.requestFocusInWindow();
		
		APP.screen.released(ev);
	}
	
	public Point lastMovedCanvasPoint;
	public Point lastMovedOrDraggedCanvasPoint;
	
	public void moved(InputEvent ev) {
		
		c.requestFocusInWindow();
		
		lastMovedCanvasPoint = ev.p;
		lastMovedOrDraggedCanvasPoint = lastMovedCanvasPoint;
		
		APP.screen.moved(ev);
	}
	
	Point lastClickedCanvasPoint;
	
	public void clicked(InputEvent ev) {
		
		c.requestFocusInWindow();
		
		APP.screen.clicked(ev);
	}
	
	public void entered(InputEvent ev) {
		
	}
	
	public void exited(InputEvent ev) {
		APP.screen.exited(ev);
	}
	
	public void requestFocusInWindow() {
		c.requestFocusInWindow();
	}
	
	public void qKey(InputEvent ev) {
		APP.screen.qKey(ev);
	}
	
	public void wKey(InputEvent ev) {
		APP.screen.wKey(ev);
	}
	
	public void gKey(InputEvent ev) {
		APP.screen.gKey(ev);
	}
	
	public void deleteKey(InputEvent ev) {
		APP.screen.deleteKey(ev);
	}
	
	public void insertKey(InputEvent ev) {
		APP.screen.insertKey(ev);
	}
	
	public void escKey(InputEvent ev) {
		APP.screen.escKey(ev);
	}
	
	public void d1Key(InputEvent ev) {
		APP.screen.d1Key(ev);
	}
	
	public void d2Key(InputEvent ev) {
		APP.screen.d2Key(ev);
	}
	
	public void d3Key(InputEvent ev) {
		APP.screen.d3Key(ev);
	}
	
	public void plusKey(InputEvent ev) {
		APP.screen.plusKey(ev);
	}
	
	public void minusKey(InputEvent ev) {
		APP.screen.minusKey(ev);
	}
	
	public void downKey(InputEvent ev) {
		APP.screen.downKey(ev);
	}

	public void upKey(InputEvent ev) {
		APP.screen.upKey(ev);
	}
	
	public void enterKey(InputEvent ev) {
		APP.screen.enterKey(ev);
	}
	
	public void aKey(InputEvent ev) {
		APP.screen.aKey(ev);
	}
	
	public void sKey(InputEvent ev) {
		APP.screen.sKey(ev);
	}
	
	public void dKey(InputEvent ev) {
		APP.screen.dKey(ev);
	}
}
