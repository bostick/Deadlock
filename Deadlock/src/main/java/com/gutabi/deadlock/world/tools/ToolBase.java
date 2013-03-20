package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.InputEvent;

public abstract class ToolBase extends Tool {
	
	public ToolBase() {
		
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
		APP.worldScreen.world.quadrantMap.toggleGrid();
		
		APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
		
		APP.worldScreen.world.render_worldPanel();
		APP.worldScreen.world.render_preview();
		APP.worldScreen.contentPane.repaint();
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
		
		APP.worldScreen.world.zoomRelative(1.1);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		APP.worldScreen.world.quadrantMap.computeGridSpacing();
		
		APP.worldScreen.world.render_worldPanel();
		APP.worldScreen.world.render_preview();
		
		APP.worldScreen.contentPane.repaint();
		APP.debuggerScreen.contentPane.repaint();
		
	}

	public void minusKey() {
		
		APP.worldScreen.world.zoomRelative(0.9);
		
//		screen.contentPane.moved(screen.contentPane.getLastMovedContentPanePoint());
		
		APP.worldScreen.world.quadrantMap.computeGridSpacing();
		
		APP.worldScreen.world.render_worldPanel();
		APP.worldScreen.world.render_preview();
		
		APP.worldScreen.contentPane.repaint();
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

}
