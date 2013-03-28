package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;

public abstract class ToolBase implements Tool {
	
	public Point p;
	
	final WorldScreen worldScreen;
	DebuggerScreen debuggerScreen;
	
	public ToolBase(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		
		this.worldScreen = worldScreen;
		this.debuggerScreen = debuggerScreen;
		
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void qKey() {
		
	}

	public void wKey() {
		
	}

	public void aKey() {
		
	}

	public void sKey() {
		
	}

	public void dKey() {
		
	}
	
	public void fKey() {
		
	}
	
	public void gKey() {
		worldScreen.contentPane.worldPanel.world.quadrantMap.toggleGrid();
		
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		
		worldScreen.contentPane.worldPanel.world.render_worldPanel();
		worldScreen.contentPane.worldPanel.world.render_preview();
		worldScreen.contentPane.repaint();
	}
	
	public void insertKey() {
		
	}

	public void d1Key() {
		
	}

	public void d2Key() {
		
	}

	public void d3Key() {
		
	}

	public void plusKey() {
		
		worldScreen.contentPane.worldPanel.worldCamera.zoomRelative(1.1);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		worldScreen.contentPane.worldPanel.world.quadrantMap.computeGridSpacing(worldScreen.contentPane.worldPanel.worldCamera);
		
		worldScreen.contentPane.worldPanel.world.render_worldPanel();
		worldScreen.contentPane.worldPanel.world.render_preview();
		
		worldScreen.contentPane.repaint();
		APP.debuggerScreen.contentPane.repaint();
		
	}

	public void minusKey() {
		
		worldScreen.contentPane.worldPanel.worldCamera.zoomRelative(0.9);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		worldScreen.contentPane.worldPanel.world.quadrantMap.computeGridSpacing(worldScreen.contentPane.worldPanel.worldCamera);
		
		worldScreen.contentPane.worldPanel.world.render_worldPanel();
		worldScreen.contentPane.worldPanel.world.render_preview();
		
		worldScreen.contentPane.repaint();
		APP.debuggerScreen.contentPane.repaint();
	}

	public void escKey() {
		
	}
	
	public void deleteKey() {
		
	}
	
	public void ctrlSKey() {
		
//		screen.mode = WorldScreenMode.DIALOG;
//		
//		screen.contentPane.disableKeyListener();
//		
//		screen.fc = new DLSFileChooser();
//		int res = screen.fc.showDialog(screen.contentPane, "Save");
//		
//		if (res == JFileChooser.APPROVE_OPTION) {
//			
//			try {
//				
//				File f = screen.fc.getSelectedFile();
//				
//				if (!f.exists()) {
//					f.createNewFile();
//				}
//				
//				FileWriter w = new FileWriter(f);
//				
//				w.write(screen.world.toFileString());
//				
//				w.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			screen.contentPane.enableKeyListener();
//			
//			screen.mode = WorldScreenMode.EDITING;
//			
//		} else {
//			
//			screen.contentPane.enableKeyListener();
//			
//			screen.mode = WorldScreenMode.EDITING;
//			
//		}
		
	}
	
	public void ctrlOKey() {
		
//		screen.mode = WorldScreenMode.DIALOG;
//		
//		screen.contentPane.disableKeyListener();
//		
//		screen.fc = new DLSFileChooser();
//		int res = screen.fc.showDialog(screen.contentPane, "Open");
//		
//		if (res == JFileChooser.APPROVE_OPTION) {
//			
//			String fileString = null;
//			
//			try {
//				
//				File f = screen.fc.getSelectedFile();
//				
//				Scanner s = new Scanner(f);
//				fileString = s.useDelimiter("\\A").next();
//				
//				s.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			screen.world = World.fromFileString(screen, fileString);
//			
//			screen.contentPane.enableKeyListener();
//			
//			screen.mode = WorldScreenMode.EDITING;
//			
//			screen.contentPane.postDisplay();
//			
//			screen.world.render_worldPanel();
//			screen.world.render_preview();
//			screen.contentPane.repaint();
//			
//		} else {
//			
//			screen.contentPane.enableKeyListener();
//			
//			screen.mode = WorldScreenMode.EDITING;
//			
//		}
		
	}
	
	public void enterKey() {
		
	}
	
	public void pressed(InputEvent ev) {
		
	}

	public void released(InputEvent ev) {
		
	}

	public void dragged(InputEvent ev) {
		
	}

	public void moved(InputEvent ev) {
		
	}

	public void clicked(InputEvent ev) {
		
	}
	
	public void draw(RenderingContext ctxt) {
		
	}
	
	public void draw_pixels(RenderingContext ctxt) {
		
	}

}
