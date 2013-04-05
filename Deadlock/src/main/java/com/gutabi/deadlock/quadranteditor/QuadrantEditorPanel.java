package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.ui.Panel;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.RenderingContext;

public class QuadrantEditorPanel extends Panel {
	
	public QuadrantEditorPanel() {
		
		aabb = new AABB(aabb.x, aabb.y, APP.MAINWINDOW_WIDTH, APP.MAINWINDOW_HEIGHT);
		
	}
	
	public void postDisplay() {
		QuadrantEditor editor = (QuadrantEditor)APP.model;
		
		editor.aabb = new AABB(aabb.width/2 - editor.aabb.width/2, aabb.height/2 - editor.aabb.height/2, editor.aabb.width, editor.aabb.height);
		
	}
	
	public void paint(RenderingContext ctxt) {
		QuadrantEditor editor = (QuadrantEditor)APP.model;
		
		ctxt.cam = editor.world.worldCamera;
		
		Transform origTrans = ctxt.getTransform();
		
		ctxt.translate(aabb.x, aabb.y);
		
		editor.paint_panel(ctxt);
		
		ctxt.setTransform(origTrans);
		
	}

}
