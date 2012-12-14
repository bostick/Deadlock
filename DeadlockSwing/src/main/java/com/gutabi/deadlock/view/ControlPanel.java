package com.gutabi.deadlock.view;


import static com.gutabi.deadlock.DeadlockApplication.APP;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings({"serial", "static-access"})
public class ControlPanel extends JPanel implements ActionListener {
	
	public JCheckBox normalCarButton;
	public JCheckBox fastCarButton;
	public JCheckBox randomCarButton;
	public JCheckBox reallyFastCarButton;
	
	public JButton startButton;
	public JButton stopButton;
	
	public JCheckBox fpsCheckBox;
	public JCheckBox stopSignCheckBox;
	public JCheckBox carTextureCheckBox;
	public JCheckBox explosionsCheckBox;
	public JCheckBox debugCheckBox;
	public JTextField dtField;
	
	public ControlPanel() {
		setSize(new Dimension(156, 822));
		setPreferredSize(new Dimension(156, 822));
		setMaximumSize(new Dimension(156, 822));
	}
	
	public void init() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		Box hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("Simulation Init:"));
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		normalCarButton = new JCheckBox("Normal Cars");
		normalCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(normalCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		fastCarButton = new JCheckBox("Fast Cars");
		fastCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(fastCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
//		randomCarButton = new JCheckBox("Random Cars");
//		randomCarButton.setSelected(true);
//		hBox = Box.createHorizontalBox();
//		hBox.add(randomCarButton);
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
		
		reallyFastCarButton = new JCheckBox("Really Fast Cars");
		reallyFastCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(reallyFastCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		hBox = Box.createHorizontalBox();
		hBox.add(startButton);
		hBox.add(stopButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("Simulation State:"));
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		fpsCheckBox = new JCheckBox("fps draw");
		fpsCheckBox.setSelected(APP.FPS_DRAW);
		fpsCheckBox.setActionCommand("fpsDraw");
		fpsCheckBox.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(fpsCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		stopSignCheckBox = new JCheckBox("draw stop signs");
		stopSignCheckBox.setSelected(APP.STOPSIGN_DRAW);
		stopSignCheckBox.setActionCommand("stopSignDraw");
		stopSignCheckBox.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(stopSignCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		carTextureCheckBox = new JCheckBox("draw car textures");
		carTextureCheckBox.setSelected(APP.CARTEXTURE_DRAW);
		carTextureCheckBox.setActionCommand("carTextureDraw");
		carTextureCheckBox.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(carTextureCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		explosionsCheckBox = new JCheckBox("draw explosions");
		explosionsCheckBox.setSelected(APP.EXPLOSIONS_DRAW);
		explosionsCheckBox.setActionCommand("explosionsDraw");
		explosionsCheckBox.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(explosionsCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		debugCheckBox = new JCheckBox("debug draw");
		debugCheckBox.setSelected(APP.DEBUG_DRAW);
		debugCheckBox.setActionCommand("debugDraw");
		debugCheckBox.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(debugCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		dtField = new JTextField();
		dtField.setText(Double.toString(APP.dt));
		dtField.setMaximumSize(new Dimension(10000, 100));
		dtField.setActionCommand("dt");
		dtField.addActionListener(this);
		hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("dt"));
		hBox.add(dtField);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		verticalBox.add(Box.createVerticalGlue());
		
		Box previewBox = Box.createHorizontalBox();
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		previewBox.add(VIEW.previewPanel);
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		verticalBox.add(previewBox);
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			VIEW.controlPanel.stopButton.setEnabled(true);
			
			APP.world.startRunning();
			
		} else if (e.getActionCommand().equals("stop")) {
			
			VIEW.controlPanel.startButton.setText("Start");
			VIEW.controlPanel.startButton.setActionCommand("start");
			
			VIEW.controlPanel.stopButton.setEnabled(false);
			
			APP.world.stopRunning();
			
		} else if (e.getActionCommand().equals("pause")) {
			
			VIEW.controlPanel.startButton.setText("Unpause");
			VIEW.controlPanel.startButton.setActionCommand("unpause");
			
			APP.world.pauseRunning();
			
		} else if (e.getActionCommand().equals("unpause")) {
			
			VIEW.controlPanel.startButton.setText("Pause");
			VIEW.controlPanel.startButton.setActionCommand("pause");
			
			APP.world.unpauseRunning();
			
		} else if (e.getActionCommand().equals("dt")) {
			
			String text = VIEW.controlPanel.dtField.getText();
			try {
				double dt = Double.parseDouble(text);
				APP.dt = dt;
			} catch (NumberFormatException ex) {
				
			}
			
		} else if (e.getActionCommand().equals("debugDraw")) {
			
			boolean state = VIEW.controlPanel.debugCheckBox.isSelected();
			
			APP.DEBUG_DRAW = state;
			
			APP.render();
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("fpsDraw")) {
			
			boolean state = VIEW.controlPanel.fpsCheckBox.isSelected();
			
			APP.FPS_DRAW = state;
			
			APP.render();
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("stopSignDraw")) {
			
			boolean state = VIEW.controlPanel.stopSignCheckBox.isSelected();
			
			APP.STOPSIGN_DRAW = state;
			
			APP.render();
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("carTextureDraw")) {
			
			boolean state = VIEW.controlPanel.carTextureCheckBox.isSelected();
			
			APP.CARTEXTURE_DRAW = state;
			
			VIEW.repaint();
			
		} else if (e.getActionCommand().equals("explosionsDraw")) {
			
			boolean state = VIEW.controlPanel.explosionsCheckBox.isSelected();
			
			APP.EXPLOSIONS_DRAW = state;
			
			VIEW.repaint();
			
		}
	}
	
}
