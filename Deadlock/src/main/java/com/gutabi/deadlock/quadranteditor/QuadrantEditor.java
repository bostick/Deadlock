package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Model;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.Button;
import com.gutabi.deadlock.ui.Menu;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.WorldPanel;
import com.gutabi.deadlock.world.sprites.SpriteSheet.SpriteSheetSprite;

public class QuadrantEditor implements Model {
	
	public WorldPanel worldPanel;
	
	public World world;
	
	public AABB hilited;
	
	public AABB aabb = new AABB(0, 0, 800, 600);
	
	int quadrantRows;
	int quadrantCols;
	
	int[][] ini;
	
	Button removeCol;
	Button addCol;
	Button removeRow;
	Button addRow;
	Button removeBoth;
	Button addBoth;
	Button go;
	
	public QuadrantEditor() {
		
		ini = new int[][] {
				{1, 1, 1},
				{1, 1, 0},
				{0, 1, 0}
			};
		
		worldPanel = new WorldPanel();
		
		world = QuadrantEditorWorld.createWorld(ini);
		
		worldPanel.aabb = new AABB(50, 50, 350, 350);
		
		double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
		double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
		world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
		
		world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
		
		world.panelPostDisplay();
		
		removeRow = new Button() {
			
			public void action() {
				
				int[][] old = ini;
				
				ini = new int[old.length-1][old[0].length];
				
				for (int i = 0; i < ini.length; i++) {
					for (int j = 0; j < ini[0].length; j++) {
						ini[i][j] = old[i][j];
					}
				}
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
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
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
				
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
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
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
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
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
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
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
				
				world = QuadrantEditorWorld.createWorld(ini);
				
				double pixelsPerMeterWidth = worldPanel.aabb.width / world.quadrantMap.worldWidth;
				double pixelxPerMeterHeight = worldPanel.aabb.height / world.quadrantMap.worldHeight;
				world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelxPerMeterHeight);
				
				world.worldCamera.worldViewport = new AABB(0, 0, world.quadrantMap.worldWidth, world.quadrantMap.worldHeight);
				world.panelPostDisplay();
				
				world.render_worldPanel();
				APP.appScreen.contentPane.repaint();
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
				
				QuadrantEditorWorld.action(ini);
				
//				WorldScreen s = new WorldScreen();
//				DebuggerScreen debuggerScreen = new DebuggerScreen(s);
//				ControlPanel controlPanel = new ControlPanel() {{
//					setLocation(0, 0);
//				}};
//				debuggerScreen.contentPane.pcp.getChildren().add(controlPanel);
//				
//				World world = World.createWorld(ini);
//				APP.model = world;
//				
//				APP.platform.setupAppScreen(s.contentPane.pcp);
//				
//				APP.platform.setupDebuggerScreen(debuggerScreen.contentPane.pcp);
//				
//				s.postDisplay();
//				
//				debuggerScreen.postDisplay();
//				
//				world.render_worldPanel();
//				world.render_preview();
//				s.contentPane.repaint();
//				
//				APP.platform.showAppScreen();
//				APP.platform.showDebuggerScreen();
			}
			
			public void paint(RenderingContext ctxt) {
				ctxt.setColor(Color.BLACK);
				aabb.draw(ctxt);
			}
		};
		go.setBounds(700, 500, 50, 50);
		
	}
	
	public static void action() {
		
		QuadrantEditor editor = new QuadrantEditor();
		APP.model = editor;
		
		QuadrantEditorScreen s = new QuadrantEditorScreen();
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		s.postDisplay();
		
		s.contentPane.repaint();
		
		APP.platform.showAppScreen();
	}
	
	public Menu getMenu() {
		return null;
	}
	
	Point lastMovedEditorPoint;
	Point lastMovedOrDraggedEditorPoint;
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.translate(aabb.ul);
		
		ctxt.setColor(Color.GRAY);
		ctxt.fillRect(0, 0, (int)aabb.width, (int)aabb.height);
		
		ctxt.setColor(Color.LIGHT_GRAY);
		worldPanel.aabb.paint(ctxt);
		
		Transform origTrans = ctxt.getTransform();
		ctxt.translate(worldPanel.aabb.ul);
		
		world.paint_panel(ctxt);
		
		ctxt.setTransform(origTrans);
		
		removeRow.paint(ctxt);
		addRow.paint(ctxt);
		removeCol.paint(ctxt);
		addCol.paint(ctxt);
		removeBoth.paint(ctxt);
		addBoth.paint(ctxt);
		go.paint(ctxt);
		
		if (hilited != null) {
			ctxt.setColor(Color.RED);
			hilited.draw(ctxt);
		}
		
	}
}
