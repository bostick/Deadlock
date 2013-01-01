package com.gutabi.deadlock.quadranteditor;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import javax.swing.JFrame;

import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.ContentPane;

@SuppressWarnings("serial")
public class QuadrantEditorContentPane extends ContentPane {
	
	public QuadrantEditor screen;
	
	public QuadrantEditorPanel panel;
	
	public QuadrantEditorContentPane(QuadrantEditor screen) {
		
		panel = new QuadrantEditorPanel(screen) {{
			setLocation(0, 0);
		}};
		
		children.add(panel);
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
