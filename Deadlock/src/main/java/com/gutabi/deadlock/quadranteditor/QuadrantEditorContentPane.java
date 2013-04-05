package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import com.gutabi.deadlock.ui.ContentPane;

public class QuadrantEditorContentPane extends ContentPane {
	
	public QuadrantEditorContentPane(QuadrantEditorScreen screen) {
		
		this.pcp = APP.platform.createPlatformContentPane(screen);
		
		QuadrantEditorPanel panel = new QuadrantEditorPanel() {{
			setLocation(0, 0);
		}};
		
		pcp.getChildren().add(panel);
	}
	
}
