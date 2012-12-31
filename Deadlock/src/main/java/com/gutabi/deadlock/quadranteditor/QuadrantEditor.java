package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.menu.MainMenu;

public class QuadrantEditor extends ScreenBase {
	
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
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		s.setup(APP.container);
		((JFrame)APP.container).setVisible(true);
		s.postDisplay();
		
		s.contentPane.panel.render();
		s.contentPane.repaint();
	}
	
}
