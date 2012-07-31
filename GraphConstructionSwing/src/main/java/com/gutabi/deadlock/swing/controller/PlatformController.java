package com.gutabi.deadlock.swing.controller;

import static com.gutabi.deadlock.swing.Main.PLATFORMVIEW;

public class PlatformController {
	
	public MouseController mc;
	public KeyboardController kc;
	
	public void init() {
		
		mc = new MouseController();
		
		kc = new KeyboardController();
		
		PLATFORMVIEW.panel.addMouseListener(mc);
		PLATFORMVIEW.panel.addMouseMotionListener(mc);
		PLATFORMVIEW.panel.addKeyListener(kc);
		
	}
	
}
