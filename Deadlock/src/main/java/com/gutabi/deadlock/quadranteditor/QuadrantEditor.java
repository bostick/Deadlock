package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import com.gutabi.deadlock.ScreenBase;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.menu.MainMenu;

public class QuadrantEditor extends ScreenBase {
	
	public QuadrantEditorContentPane contentPane;
	
	int quadrantRows;
	int quadrantCols;
	
	BufferedImage quadrantGrass;
	
	AABB hilited;
	
	public QuadrantEditor() {
		
//		canvas = new QuadrantEditorCanvas(this);
		
	}
	
	public void setup(RootPaneContainer container) {
		
//		Container cp = container.getContentPane();
//		
//		cp.setLayout(null);
//		
//		cp.add(canvas.java());
//		
//		Dimension size = canvas.java().getSize();
//		canvas.java().setBounds(0, 0, size.width, size.height);
		
		contentPane = new QuadrantEditorContentPane(this);
		
		contentPane.setLayout(null);
		
		container.setContentPane(contentPane);
		contentPane.setFocusable(true);
		contentPane.requestFocusInWindow();
	}
	
	public void postDisplay() {
		
		contentPane.postDisplay();
		
	}
	
//	public Button hitTest(Point p) {
//		if (removeCol.hitTest(p)) {
//			return removeCol;
//		}
//		if (addCol.hitTest(p)) {
//			return addCol;
//		}
//		if (removeRow.hitTest(p)) {
//			return removeRow;
//		}
//		if (addRow.hitTest(p)) {
//			return addRow;
//		}
//		if (removeBoth.hitTest(p)) {
//			return removeBoth;
//		}
//		if (addBoth.hitTest(p)) {
//			return addBoth;
//		}
//		if (go.hitTest(p)) {
//			return go;
//		}
//		return null;
//	}
	
	public void escKey() {
		
		MainMenu s = new MainMenu();
		
		s.setup(APP.container);
		((JFrame)APP.container).setVisible(true);
		s.postDisplay();
		
		s.contentPane.canvas.render();
		s.contentPane.repaint();
	}
	
	public Point contentPaneToCanvas(Point p) {
		return new Point(p.x - 0, p.y - 0);
	}
	
}
