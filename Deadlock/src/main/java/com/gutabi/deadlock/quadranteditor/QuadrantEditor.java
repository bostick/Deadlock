package com.gutabi.deadlock.quadranteditor;

import java.awt.image.BufferedImage;

import com.gutabi.deadlock.Screen;
import com.gutabi.deadlock.math.geom.AABB;

public class QuadrantEditor extends Screen {
	
	public QuadrantEditorContentPane contentPane;
	
	int quadrantRows;
	int quadrantCols;
	
	BufferedImage quadrantGrass;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
		contentPane = new QuadrantEditorContentPane(this);
		
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
}
