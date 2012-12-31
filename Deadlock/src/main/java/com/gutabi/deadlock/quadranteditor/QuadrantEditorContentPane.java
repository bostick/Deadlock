package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class QuadrantEditorContentPane extends ContentPane {
	
	public QuadrantEditorCanvas canvas;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		super(screen);
		
		canvas = new QuadrantEditorCanvas(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(canvas);
	}
	
}
