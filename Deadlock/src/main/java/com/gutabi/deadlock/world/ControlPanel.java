package com.gutabi.deadlock.world;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gutabi.deadlock.ui.ComponentBase;

//@SuppressWarnings({"serial", "static-access"})
//@SuppressWarnings({"serial"})
public class ControlPanel extends ComponentBase {
	
	JPanel panel;
	
	WorldScreen screen;
	
	public PreviewPanel previewPanel;
	public Object o;
	
	public JCheckBox normalCarButton;
	public JCheckBox fastCarButton;
//	public JCheckBox randomCarButton;
	public JCheckBox reallyCarButton;
	
	public JButton startButton;
	public JButton stopButton;
	
	public JCheckBox fpsCheckBox;
	public JCheckBox stopSignCheckBox;
	public JCheckBox carTextureCheckBox;
	public JCheckBox explosionsCheckBox;
	public JCheckBox debugCheckBox;
	public JTextField dtField;
	
	public ControlPanel(WorldScreen screen) {
		
		this.screen = screen;
		
		this.panel = new JPanel();
		
		panel.setSize(new Dimension(200, 822));
		panel.setPreferredSize(new Dimension(200, 822));
		panel.setMaximumSize(new Dimension(200, 822));
	}
	
	public int getWidth() {
		return panel.getWidth();
	}
	
	public int getHeight() {
		return panel.getHeight();
	}
	
	public java.awt.Component java() {
		return panel;
	}
	
	public void init() {
		
		previewPanel = new PreviewPanel(screen);
		
		panel.setLayout(null);
		
		JLabel simulationInitLab = new JLabel("Simulation Init:");
		simulationInitLab.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
		
		panel.add(simulationInitLab);
		
		Dimension size = simulationInitLab.getPreferredSize();
		simulationInitLab.setBounds(5, 5, size.width, size.height);
		
		normalCarButton = new JCheckBox();
		normalCarButton.setFocusable(false);
		normalCarButton.setSelected(true);
		
		panel.add(normalCarButton);
		
		size = normalCarButton.getPreferredSize();
		normalCarButton.setBounds(5, 5 + simulationInitLab.getHeight() + 5, size.width, size.height);
		
		JLabel normalCarsLab = new JLabel("Normal Cars");
		normalCarsLab.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
		
		panel.add(normalCarsLab);
		
		size = normalCarsLab.getPreferredSize();
		normalCarsLab.setBounds(5 + normalCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5, size.width, size.height);
		
		
		
		fastCarButton = new JCheckBox();
		fastCarButton.setFocusable(false);
		fastCarButton.setSelected(true);
		
		panel.add(fastCarButton);
		
		size = fastCarButton.getPreferredSize();
		fastCarButton.setBounds(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5, size.width, size.height);
		
		JLabel fastCarLab = new JLabel("Fast Cars");
		fastCarLab.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
		
		panel.add(fastCarLab);
		
		size = fastCarLab.getPreferredSize();
		fastCarLab.setBounds(5 + fastCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5, size.width, size.height);
		
		
		
		reallyCarButton = new JCheckBox();
		reallyCarButton.setFocusable(false);
		reallyCarButton.setSelected(true);
		
		panel.add(reallyCarButton);
		
		size = reallyCarButton.getPreferredSize();
		reallyCarButton.setBounds(5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5, size.width, size.height);
		
		JLabel reallyCarLab = new JLabel("Really Fast Cars");
		reallyCarLab.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
		
		panel.add(reallyCarLab);
		
		size = reallyCarLab.getPreferredSize();
		reallyCarLab.setBounds(5 + reallyCarButton.getWidth() + 5, 5 + simulationInitLab.getHeight() + 5 + normalCarButton.getHeight() + 5 + fastCarButton.getHeight() + 5, size.width, size.height);
		
		
		
		startButton = new JButton("Start");
		startButton.setFocusable(false);
		startButton.setActionCommand("start");
		startButton.addActionListener(screen);
		
		panel.add(startButton);
		
		size = startButton.getPreferredSize();
		startButton.setBounds(5, 120, size.width, size.height);
		
		stopButton = new JButton("Stop");
		stopButton.setFocusable(false);
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(screen);
		stopButton.setEnabled(false);
		
		panel.add(stopButton);
		
		size = stopButton.getPreferredSize();
		stopButton.setBounds(5 + startButton.getWidth() + 5, 120, size.width, size.height);
		
		
		
		
		JLabel statelab = new JLabel("Simulation State:");
		statelab.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
		
		panel.add(statelab);
		
		size = statelab.getPreferredSize();
		statelab.setBounds(5, 160, size.width, size.height);
		
		fpsCheckBox = new JCheckBox();
		fpsCheckBox.setFocusable(false);
//		fpsCheckBox.setSelected(screen.FPS_DRAW);
		fpsCheckBox.setActionCommand("fpsDraw");
		fpsCheckBox.addActionListener(screen);
		
		
		
//		hBox = Box.createHorizontalBox();
//		hBox.add(fpsCheckBox);
//		hBox.add(new Label("draw fps").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		stopSignCheckBox = new JCheckBox();
//		stopSignCheckBox.setFocusable(false);
//		stopSignCheckBox.setSelected(APP.STOPSIGN_DRAW);
//		stopSignCheckBox.setActionCommand("stopSignDraw");
//		stopSignCheckBox.addActionListener(APP.screen);
//		hBox = Box.createHorizontalBox();
//		hBox.add(stopSignCheckBox);
//		hBox.add(new Label("draw stop signs").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		carTextureCheckBox = new JCheckBox();
//		carTextureCheckBox.setFocusable(false);
//		carTextureCheckBox.setSelected(APP.CARTEXTURE_DRAW);
//		carTextureCheckBox.setActionCommand("carTextureDraw");
//		carTextureCheckBox.addActionListener(APP.screen);
//		hBox = Box.createHorizontalBox();
//		hBox.add(carTextureCheckBox);
//		hBox.add(new Label("draw car textures").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		explosionsCheckBox = new JCheckBox();
//		explosionsCheckBox.setFocusable(false);
//		explosionsCheckBox.setSelected(APP.EXPLOSIONS_DRAW);
//		explosionsCheckBox.setActionCommand("explosionsDraw");
//		explosionsCheckBox.addActionListener(APP.screen);
//		hBox = Box.createHorizontalBox();
//		hBox.add(explosionsCheckBox);
//		hBox.add(new Label("draw explosions").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		debugCheckBox = new JCheckBox();
//		debugCheckBox.setFocusable(false);
//		debugCheckBox.setSelected(APP.DEBUG_DRAW);
//		debugCheckBox.setActionCommand("debugDraw");
//		debugCheckBox.addActionListener(APP.screen);
//		hBox = Box.createHorizontalBox();
//		hBox.add(debugCheckBox);
//		hBox.add(new Label("draw debug").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		dtField = new JTextField();
//		dtField.setFocusable(false);
//		dtField.setText(Double.toString(APP.dt));
//		dtField.setMaximumSize(new Dimension(10000, 100));
//		dtField.setActionCommand("dt");
//		dtField.addActionListener(APP.screen);
//		hBox = Box.createHorizontalBox();
//		hBox.add(dtField);
//		hBox.add(new Label("dt").java());
//		hBox.add(Box.createHorizontalGlue());
//		verticalBox.add(hBox);
//		
//		verticalBox.add(Box.createVerticalGlue());
//		
//		previewPanel = new PreviewPanel();
//		
//		Box previewBox = Box.createHorizontalBox();
//		previewBox.add(Box.createHorizontalGlue());
//		previewBox.add(previewPanel.java());
//		previewBox.add(Box.createHorizontalGlue());
//		verticalBox.add(previewBox);
//		
//		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
//		
//		add(verticalBox);
	}
	
	public void repaint() {
		
		panel.repaint();
		
	}
	
}
