package com.gutabi.deadlock.quadranteditor;

import java.awt.image.BufferedImage;

import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.Screen;
import com.gutabi.deadlock.core.geom.AABB;

public class QuadrantEditor extends Screen {
	
	public QuadrantEditorContentPane contentPane;
	
	int quadrantRows;
	int quadrantCols;
	
	BufferedImage quadrantGrass;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
		contentPane = new QuadrantEditorContentPane(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
		contentPane.setLayout(null);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
		contentPane.requestFocusInWindow();
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
}
