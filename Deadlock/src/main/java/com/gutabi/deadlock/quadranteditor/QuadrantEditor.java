package com.gutabi.deadlock.quadranteditor;

import com.gutabi.deadlock.geom.AABB;

public class QuadrantEditor {
	
	public QuadrantEditorContentPane contentPane;
	
	int quadrantRows;
	int quadrantCols;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
		contentPane = new QuadrantEditorContentPane(this);
		
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
}
