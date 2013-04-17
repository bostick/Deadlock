package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.QuadrantEditor;

public class Statics {
	
	public static Point panelToEditor(Point p, QuadrantEditor editor) {
		return new Point(p.x - editor.aabb.x, p.y - editor.aabb.y);
	}
	
}
