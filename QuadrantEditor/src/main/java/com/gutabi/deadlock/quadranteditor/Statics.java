package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.geom.AABB;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.world.WorldPanel;

public class Statics {
	
	public static Point editorToWorldPanel(Point p, WorldPanel worldPanel) {
		return new Point(p.x - worldPanel.aabb.x, p.y - worldPanel.aabb.y);
	}
	
	public static Point worldPanelToEditor(Point p, WorldPanel worldPanel) {
		return new Point(p.x + worldPanel.aabb.x, p.y + worldPanel.aabb.y);
	}
	
	public static Point worldPanelToEditor(double x, double y, WorldPanel worldPanel) {
		return new Point(x + worldPanel.aabb.x, y + worldPanel.aabb.y);
	}
	
	public static AABB worldPanelToEditor(AABB aabb, WorldPanel worldPanel) {
		Point ul = worldPanelToEditor(aabb.x, aabb.y, worldPanel);
		Point br = worldPanelToEditor(aabb.brX, aabb.brY, worldPanel);
		return new AABB(ul.x, ul.y, br.x - ul.x, br.y - ul.y);
	}
	
	public static Point panelToEditor(Point p, QuadrantEditor editor) {
		return new Point(p.x - editor.aabb.x, p.y - editor.aabb.y);
	}
	
}
