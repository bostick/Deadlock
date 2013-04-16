package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.Tool;
import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.Quadrant;

public class QuadrantEditorTool extends Tool {
	
	public void escKey() {
		
//		MainMenu.action();
	}
	
	public void moved(InputEvent ev) {
		QuadrantEditor editor = (QuadrantEditor)APP.model;
		
		Point p = ev.p;
		
		editor.lastMovedEditorPoint = Point.panelToEditor(p, editor);
		editor.lastMovedOrDraggedEditorPoint = editor.lastMovedEditorPoint;
		
		if (editor.worldPanel.aabb.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
			
			Point pWorldPanel = Point.editorToWorldPanel(editor.lastMovedOrDraggedEditorPoint, editor.worldPanel);
			Point pWorld = Point.panelToWorld(pWorldPanel, editor.world.worldCamera);
			
			Quadrant q = editor.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				AABB aabbWorldPanel = Point.worldToPanel(q.aabb, editor.world.worldCamera);
				AABB aabbEditor = Point.worldPanelToEditor(aabbWorldPanel, editor.worldPanel);
				editor.hilited = aabbEditor;
				
			} else {
				editor.hilited = null;
			}
			
		} else {
			
			if (editor.removeCol.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.removeCol.aabb;
			} else if (editor.addCol.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.addCol.aabb;
			} else if (editor.removeRow.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.removeRow.aabb;
			} else if (editor.addRow.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.addRow.aabb;
			} else if (editor.removeBoth.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.removeBoth.aabb;
			} else if (editor.addBoth.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.addBoth.aabb;
			} else if (editor.go.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.hilited = editor.go.aabb;
			} else {
				editor.hilited = null;
			}
			
		}
		
//		APP.appScreen.contentPane.repaint();
		
	}
	
	public void clicked(InputEvent ev) {
		QuadrantEditor editor = (QuadrantEditor)APP.model;
		
		Point p = ev.p;
		
		editor.lastMovedEditorPoint = Point.panelToEditor(p, editor);
		editor.lastMovedOrDraggedEditorPoint = editor.lastMovedEditorPoint;
		
		if (editor.worldPanel.aabb.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
			
			/*
			 * editor -> world canvas
			 */
			Point pWorldPanel = editor.lastMovedOrDraggedEditorPoint.minus(
					editor.worldPanel.aabb.center.minus(editor.worldPanel.aabb.dim.multiply(0.5)));
			
			Point pWorld = Point.panelToWorld(pWorldPanel, editor.world.worldCamera);
			
			Quadrant q = editor.world.quadrantMap.findQuadrant(pWorld);
			
			if (q != null) {
				
				editor.ini[q.r][q.c] = (q.active?0:1);
				
				editor.world = QuadrantEditorWorld.createWorld(editor.ini);
				
				double pixelsPerMeterWidth = editor.worldPanel.aabb.width / editor.world.quadrantMap.worldWidth;
				double pixelsPerMeterHeight = editor.worldPanel.aabb.height / editor.world.quadrantMap.worldHeight;
				editor.world.worldCamera.pixelsPerMeter = Math.min(pixelsPerMeterWidth, pixelsPerMeterHeight);
				
				editor.world.worldCamera.worldViewport = new AABB(0, 0, editor.world.quadrantMap.worldWidth, editor.world.quadrantMap.worldHeight);
				editor.world.panelPostDisplay();
				
				editor.world.render_worldPanel();
				
//				APP.appScreen.contentPane.repaint();
				
			} else {
				
			}
			
		} else {
			
			if (editor.removeCol.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.removeCol.action();
			} else if (editor.addCol.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.addCol.action();
			} else if (editor.removeRow.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.removeRow.action();
			} else if (editor.addRow.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.addRow.action();
			} else if (editor.removeBoth.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.removeBoth.action();
			} else if (editor.addBoth.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.addBoth.action();
			} else if (editor.go.hitTest(editor.lastMovedOrDraggedEditorPoint)) {
				editor.go.action();
			}
			
		}
		
	}
	
	public void setPoint(Point p) {
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
	}

}
