package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

@SuppressWarnings("static-access")
public class DeadlockView {
	
	public JFrame frame;
	public WorldPanel panel;
	public ControlPanel controlPanel;
	
	public final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public DeadlockView() {
		;
	}
	
	public void init() throws Exception {
		frame = createFrame(false);
	}
	
	public JFrame createFrame(boolean fullScreen) {
		
		JFrame newFrame;
		
		newFrame = new JFrame("Deadlock Viewer");
		
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new WorldPanel();
		panel.setFocusable(true);
		
		controlPanel = new ControlPanel();
		controlPanel.init();
		
		Container cp = newFrame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(panel);
		cp.add(controlPanel);
		
		newFrame.pack();
		
		newFrame.setSize(WindowInfo.windowWidth(), WindowInfo.windowHeight());
		newFrame.setLocation(WindowInfo.windowX(), WindowInfo.windowY());
		
		return newFrame;
	}
	
	public void repaint() {
		assert !Thread.holdsLock(MODEL);
		
		int x = (int)(panel.getWidth() * 0.5 - (MODEL.world.WORLD_WIDTH * 0.5 * MODEL.PIXELS_PER_METER) + (MODEL.world.renderingUpperLeft.x * MODEL.PIXELS_PER_METER));
		int y = (int)(panel.getHeight() * 0.5 - (MODEL.world.WORLD_HEIGHT * 0.5 * MODEL.PIXELS_PER_METER) + (MODEL.world.renderingUpperLeft.y * MODEL.PIXELS_PER_METER));
		
		panel.repaint(
				x,
				y,
				(int)(((MODEL.world.renderingBottomRight.x - MODEL.world.renderingUpperLeft.x) * MODEL.PIXELS_PER_METER)),
				(int)(((MODEL.world.renderingBottomRight.y - MODEL.world.renderingUpperLeft.y) * MODEL.PIXELS_PER_METER)));
		
	}
	
}
