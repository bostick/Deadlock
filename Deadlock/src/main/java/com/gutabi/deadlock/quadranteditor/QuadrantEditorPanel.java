package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.PanelBase;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.Quadrant;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldPanel;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public class QuadrantEditorPanel extends PanelBase {
	
	public final int EDITOR_WIDTH = 800;
	public final int EDITOR_HEIGHT = 600;
	
	QuadrantEditor screen;
	public WorldPanel worldPanel;
	
	int[][] ini;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button removeBoth;
	Button addBoth;
	Button go;
	
	public QuadrantEditorPanel(final QuadrantEditor screen) {
		this.screen = screen;
		
		aabb = new AABB(aabb.x, aabb.y, APP.QUADRANTEDITORPANEL_WIDTH, APP.QUADRANTEDITORPANEL_HEIGHT);
		
		ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		worldPanel = new WorldPanel();
		
		worldPanel.world = World.createWorld(null, null, ini);
		worldPanel.aabb = new AABB(50, 50, 350, 350);
		
		double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
		double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
		worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
		worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
		
		removeRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.x, aabb.y);
				ctxt.rotate(6 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
			
		};
		removeRow.setBounds(worldPanel.aabb.center.x - 50/2, worldPanel.aabb.brY + 40 - 50/2, 50, 50);
		
		
		addRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
				
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(2 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addRow.setBounds(worldPanel.aabb.center.x - 50/2, worldPanel.aabb.brY + 40 - 50/2 + 50, 50, 50);
		
		
		removeCol = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(4 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		removeCol.setBounds(worldPanel.aabb.brX + 40 - 50/2, worldPanel.aabb.center.y - 50/2, 50, 50);
		
		addCol = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(0 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
				
			}
		};
		addCol.setBounds(worldPanel.aabb.brX + 40 - 50/2 + 50, worldPanel.aabb.center.y - 50/2, 50, 50);
		
		removeBoth = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length-1];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(5 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		removeBoth.setBounds(worldPanel.aabb.brX + 40 - 50/2, worldPanel.aabb.brY + 40 - 50/2, 50, 50);
		
		addBoth = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length+1][old[0].length+1];
				
				for (int i = 0; i < old.length; i++) {
					for (int j = 0; j < old[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				screen.contentPane.repaint();
			}
			
			public void paint(RenderingContext ctxt) {
				
				Transform origTransform = ctxt.getTransform();
				
				ctxt.translate(aabb.ul.x, aabb.ul.y);
				ctxt.rotate(1 * Math.PI / 4, aabb.dim.multiply(0.5));
				
				APP.spriteSheet.paint(ctxt, SpriteSheetSprite.BLUEARROW, 0, 0, 50, 50);
				
				ctxt.setTransform(origTransform);
				
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		addBoth.setBounds(worldPanel.aabb.brX + 40 - 50/2 + 50, worldPanel.aabb.brY + 40 - 50/2 + 50, 50, 50);
		
		go = new Button() {
			
			public void action() {
				
				WorldScreen s = new WorldScreen();
				DebuggerScreen debuggerScreen = new DebuggerScreen(s);
				
				s.contentPane.worldPanel.world = World.createWorld(s, debuggerScreen, ini);
				
				APP.platform.setupAppScreen(s.contentPane.pcp);
				
				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
				
				s.postDisplay();
				
				debuggerScreen.postDisplay();
				
				s.contentPane.worldPanel.world.render_worldPanel();
				s.contentPane.worldPanel.world.render_preview();
				s.contentPane.repaint();
				
				APP.platform.showAppScreen();
				APP.platform.showDebuggerScreen();
			}
			
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		go.setBounds(700, 500, 50, 50);
	}
	
	public void setLocation(double x, double y) {
		aabb = new AABB(x, y, aabb.width, aabb.height);
	}
	
	public void postDisplay() {
		
	}
	
	public Point lastMovedPanelPoint;
	public Point lastMovedOrDraggedPanelPoint;
	Point lastClickedPanelPoint;
	Point lastMovedEditorPoint;
	Point lastMovedOrDraggedEditorPoint;
	
	public Point panelToEditor(Point p) {
		return new Point(p.x - (aabb.width/2 - EDITOR_WIDTH/2), p.y - (aabb.height/2 - EDITOR_HEIGHT/2));
	}
	
	public void moved(InputEvent ev) {
		
		lastMovedPanelPoint = ev.p;
		lastMovedOrDraggedPanelPoint = lastMovedPanelPoint;
		
		Point p = ev.p;
		
		lastMovedEditorPoint = panelToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldPanel.aabb.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldPanel = lastMovedOrDraggedEditorPoint.minus(worldPanel.aabb.center.minus(worldPanel.aabb.dim.multiply(0.5)));
			
			Point pWorld = Point.panelToWorld(pWorldPanel, worldPanel.worldCamera);
			
			Quadrant q = worldPanel.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				screen.hilited = Point.worldToPanel(q.aabb, worldPanel.worldCamera).plus(worldPanel.aabb.center.minus(worldPanel.aabb.dim.multiply(0.5)));
				
			} else {
				screen.hilited = null;
			}
			
		} else {
			
			if (removeCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeCol.aabb;
			} else if (addCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addCol.aabb;
			} else if (removeRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeRow.aabb;
			} else if (addRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addRow.aabb;
			} else if (removeBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = removeBoth.aabb;
			} else if (addBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = addBoth.aabb;
			} else if (go.hitTest(lastMovedOrDraggedEditorPoint)) {
				screen.hilited = go.aabb;
			} else {
				screen.hilited = null;
			}
			
		}
		
		screen.contentPane.repaint();
	}
	
	public void clicked(InputEvent ev) {
		
		Point p = ev.p;
		
		lastMovedEditorPoint = panelToEditor(p);
		lastMovedOrDraggedEditorPoint = lastMovedEditorPoint;
		
		if (worldPanel.aabb.hitTest(lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldPanel = lastMovedOrDraggedEditorPoint.minus(
					worldPanel.aabb.center.minus(worldPanel.aabb.dim.multiply(0.5)));
			
			Point pWorld = Point.panelToWorld(pWorldPanel, worldPanel.worldCamera);
			
			Quadrant q = worldPanel.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				ini[q.r][q.c] = (q.active?0:1);
				
				worldPanel.world = World.createWorld(null, null, ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / worldPanel.world.quadrantMap.worldWidth;
				double pixelsPerMeterHeight = worldPanel.aabb.height / worldPanel.world.quadrantMap.worldHeight;
				worldPanel.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
				worldPanel.worldCamera.worldViewport = new AABB(0, 0, worldPanel.world.quadrantMap.worldWidth, worldPanel.world.quadrantMap.worldHeight);
				worldPanel.world.panelPostDisplay(worldPanel.worldCamera);
				
				worldPanel.world.render_worldPanel();
				
				screen.contentPane.repaint();
				
			} else {
				
			}
			
		} else {
			
			if (removeCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeCol.action();
			} else if (addCol.hitTest(lastMovedOrDraggedEditorPoint)) {
				addCol.action();
			} else if (removeRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeRow.action();
			} else if (addRow.hitTest(lastMovedOrDraggedEditorPoint)) {
				addRow.action();
			} else if (removeBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				removeBoth.action();
			} else if (addBoth.hitTest(lastMovedOrDraggedEditorPoint)) {
				addBoth.action();
			} else if (go.hitTest(lastMovedOrDraggedEditorPoint)) {
				go.action();
			}
			
		}
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.cam = worldPanel.worldCamera;
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		ctxt.translate(aabb.width/2 - EDITOR_WIDTH/2, aabb.height/2 - EDITOR_HEIGHT/2);
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		worldPanel.aabb.paint(ctxt);
		
		Transform origTrans2 = ctxt.getTransform();
		ctxt.translate(
				worldPanel.aabb.center.minus(worldPanel.aabb.dim.multiply(0.5)));
		
		worldPanel.world.paint_panel_worldCoords(ctxt);
		
		ctxt.setTransform(origTrans2);
		
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeBoth.paint(ctxt);
		addBoth.paint(ctxt);
		go.paint(ctxt);
		
		if (screen.hilited != null) {
			ctxt.setColor(Color.RED);
			screen.hilited.draw(ctxt);
		}
		
		ctxt.setTransform(origTrans);
		
	}

}
