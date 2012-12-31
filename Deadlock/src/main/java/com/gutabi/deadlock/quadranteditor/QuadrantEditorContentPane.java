package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class QuadrantEditorContentPane extends ContentPane {
	
	public QuadrantEditorPanel panel;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		super(screen);
		
		panel = new QuadrantEditorPanel(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(panel);
	}
	
}
