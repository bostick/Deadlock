package com.gutabi.deadlock.menu;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Dim;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.quadranteditor.QuadrantEditor;
import com.gutabi.deadlock.view.Canvas;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.PaintEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.examples.FourByFourGridWorld;
import com.gutabi.deadlock.world.examples.OneByOneWorld;
import com.gutabi.deadlock.world.examples.WorldA;

//@SuppressWarnings("static-access")
public class MainMenu extends ScreenBase {
	
	public static final int MENU_WIDTH = 800;
	public static final int MENU_HEIGHT = 600;
	
	public Canvas canvas;
	
	int canvasWidth;
	int canvasHeight;
	
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public MenuItem hilited;
	public MenuItem firstMenuItem;
	
	double widest;
	int totalHeight;
	
	static Logger logger = Logger.getLogger(MainMenu.class);
	
	public MainMenu() {
		
		MenuItem oneMenuItem = new MenuItem(MainMenu.this,"1x1 Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = OneByOneWorld.createOneByOneWorld(s);
				
//				APP.screen = s;
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.render();
//				APP.screen.repaint();
				s.repaintCanvas();
//				VIEW.previewPanel.repaint();
				s.controlPanel.repaint();
//				APP.container.getContentPane().repaint();
			}
		};
		add(oneMenuItem);
		
		MenuItem fourMenuItem = new MenuItem(MainMenu.this, "4x4 Grid Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = FourByFourGridWorld.createFourByFourGridWorld(s);
				
//				APP.screen = s;
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.render();
//				APP.screen.repaint();
//				VIEW.canvas.repaint();
				s.repaintCanvas();
				s.controlPanel.repaint();
				
			}
		};
		add(fourMenuItem);
		
		MenuItem aMenuItem = new MenuItem(MainMenu.this, "World A Demo") {
			public void action() {
				
				teardown(APP.container);
				
				WorldScreen s = new WorldScreen();
				s.world = WorldA.createWorldA(s);
				
//				APP.screen = s;
				
				s.setup(APP.container);
				
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.render();
//				APP.screen.repaint();
//				VIEW.canvas.repaint();
				s.repaintCanvas();
//				VIEW.previewPanel.repaint();
				s.controlPanel.repaint();
				
			}
		};
		add(aMenuItem);
		
		MenuItem dialogMenuItem = new MenuItem(MainMenu.this,  "Quadrant Editor...") {
			public void action() {
				
				teardown(APP.container);
				
				QuadrantEditor s = new QuadrantEditor();
//				APP.screen = s;
				
				s.setup(APP.container);
				((JFrame)APP.container).setVisible(true);
				
				s.postDisplay();
				s.render();
//				APP.screen.repaint();
//				VIEW.canvas.repaint();
				s.repaintCanvas();
//				VIEW.previewPanel.repaint();
			}
		};
		add(dialogMenuItem);
		
		MenuItem puzzleMenuItem = new MenuItem(MainMenu.this,  "Puzzle Mode") {
			public void action() {
				
			}
		};
		puzzleMenuItem.active = false;
		add(puzzleMenuItem);
		
		MenuItem loadMenuItem = new MenuItem(MainMenu.this, "Load...") {
			public void action() {
				
			}
		};
		loadMenuItem.active = false;
		add(loadMenuItem);
		
		MenuItem captureMenuItem = new MenuItem(MainMenu.this, "Capture the Flag") {
			public void action() {
				
			}
		};
		captureMenuItem.active = false;
		add(captureMenuItem);
		
		MenuItem quitMenuItem = new MenuItem(MainMenu.this, "Quit") {
			public void action() {
				
				APP.exit();
				
			}
		};
		add(quitMenuItem);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		canvas = new Canvas();
		
		Container cp = container.getContentPane();
		cp.add(canvas.java());
	}
	
	public void teardown(RootPaneContainer container) {
		
		Container cp = container.getContentPane();
		cp.remove(canvas.java());
		
		canvas = null;
	}
	
	public void postDisplay() {
		
		Dim canvasDim = canvas.postDisplay();
		canvasWidth = (int)canvasDim.width;
		canvasHeight = (int)canvasDim.height;
		
	}
	
	public void add(MenuItem item) {
		
		if (items.isEmpty()) {
			firstMenuItem = item;
		}
		
		items.add(item);
		
		int i = items.size()-1;
		
		MenuItem prev = items.get((i-1 + items.size()) % items.size());
		MenuItem first = items.get(0);
		
		prev.down = item;
		
		item.up = prev;
		item.down = first;
		
		first.up = item;
		
	}
	
	public MenuItem hitTest(Point p) {
		
		for (MenuItem item : items) {
			if (item.hitTest(p)) {
				return item;
			}
		}
		
		return null;
	}
	
	public void downKey(InputEvent ev) {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.down;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.down;
		}
		
		repaintCanvas();
	}
	
	public void upKey(InputEvent ev) {
		
		if (hilited == null) {
			
			hilited = firstMenuItem;
			
		} else {
			
			hilited = hilited.up;
			
		}
		
		while (!hilited.active) {
			hilited = hilited.up;
		}
		
		repaintCanvas();
	}
	
	public void enterKey(InputEvent ev) {
		
		if (hilited != null && hilited.active) {
			hilited.action();
		}
		
	}
	
	public Point canvasToMenu(Point p) {
		return new Point(p.x - (canvas.getWidth()/2 - MENU_WIDTH/2), p.y - (canvas.getHeight()/2 - MENU_HEIGHT/2));
	}
	
	public Point lastMovedMenuPoint;
	
	public void moved(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedMenuPoint = canvasToMenu(p);
		
		MenuItem hit = hitTest(lastMovedMenuPoint);
		if (hit != null && hit.active) {
			hilited = hit;
		} else {
			hilited = null;
		}
		
		repaintCanvas();
	}
	
	Point lastClickedMenuPoint;
	
	public void clicked(InputEvent ev) {
		
		lastClickedMenuPoint = canvasToMenu(ev.p);
		
		MenuItem item = hitTest(lastClickedMenuPoint);
		
		if (item != null && item.active) {
			item.action();
		}
		
	}
	
	public void render() {
		logger.debug("render");
		
		synchronized (VIEW) {
			
			BufferedImage canvasMenuImage = new BufferedImage(MENU_WIDTH, MENU_HEIGHT, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D canvasMenuImageG2 = canvasMenuImage.createGraphics();
			
			RenderingContext canvasMenuContext = new RenderingContext(RenderingContextType.CANVAS);
			canvasMenuContext.g2 = canvasMenuImageG2;
			
			for (MenuItem item : items) {
				item.renderLocal(canvasMenuContext);
				if ((int)item.localAABB.width > widest) {
					widest = (int)item.localAABB.width;
				}
				totalHeight += (int)item.localAABB.height;
			}
			
			AffineTransform origTransform = canvasMenuContext.getTransform();
			
			canvasMenuContext.translate(MENU_WIDTH/2 - widest/2, 150);
			
			for (MenuItem item : items) {
				item.render(canvasMenuContext);
				canvasMenuContext.translate(0, item.localAABB.height + 10);
			}
			
			canvasMenuContext.setTransform(origTransform);
			
			canvasMenuImageG2.dispose();
		}
		
	}
	
	public void repaintCanvas() {
		logger.debug("repaint");
		
		do {
			
			do {
				
				Graphics2D g2 = (Graphics2D)canvas.bs.getDrawGraphics();
				
				g2.setColor(Color.DARK_GRAY);
				g2.fillRect(0, 0, canvasWidth, canvasHeight);
				
				RenderingContext ctxt = new RenderingContext(RenderingContextType.CANVAS);
				ctxt.g2 = g2;
				
				AffineTransform origTrans = ctxt.getTransform();
				
				ctxt.translate(canvas.getWidth()/2 - MENU_WIDTH/2, canvas.getHeight()/2 - MENU_HEIGHT/2);
				
				AffineTransform menuTrans = ctxt.getTransform();
				
				ctxt.paintImage(VIEW.titleBackground, 0, 0, MENU_WIDTH, MENU_HEIGHT, 0, 0, MENU_WIDTH, MENU_HEIGHT);
				
				ctxt.translate(MENU_WIDTH/2 - 498/2, 20);
				ctxt.paintImage(VIEW.title_white, 0, 0, 498, 90, 0, 0, 498, 90);
				
				ctxt.setTransform(menuTrans);
				
				ctxt.translate(MENU_WIDTH/2 - 432/2, 550);
				ctxt.paintImage(VIEW.copyright, 0, 0, 432, 38, 0, 0, 432, 38);
				
				ctxt.setTransform(menuTrans);
				
				ctxt.setColor(VIEW.menuBackground);
				ctxt.fillRect((int)(MENU_WIDTH/2 - widest/2 - 5), 150 - 5, (int)(widest + 10), totalHeight + 10 * (items.size() - 1) + 5 + 5);
				
				for (MenuItem item : items) {
					item.paint(ctxt);
				}
				
				if (hilited != null) {
					hilited.paintHilited(ctxt);			
				}
				
				ctxt.setTransform(origTrans);
				
				g2.dispose();
				
			} while (canvas.bs.contentsRestored());
			
			canvas.bs.show();
			
		} while (canvas.bs.contentsLost());

	}
	
	public void paint(PaintEvent ev) {
		assert false;
	}
	
}
