package com.gutabi.deadlock.view;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//@SuppressWarnings({"serial", "static-access"})
@SuppressWarnings({"serial"})
public class ControlPanel extends JPanel {
	
	public PreviewPanel previewPanel;
	
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
		normalCarButton.setFocusable(false);
		normalCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(normalCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		fastCarButton = new JCheckBox("Fast Cars");
		fastCarButton.setFocusable(false);
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
		reallyFastCarButton.setFocusable(false);
		reallyFastCarButton.setSelected(true);
		hBox = Box.createHorizontalBox();
		hBox.add(reallyFastCarButton);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		startButton = new JButton("Start");
		startButton.setFocusable(false);
		startButton.setActionCommand("start");
		startButton.addActionListener(APP.screen);
		
		stopButton = new JButton("Stop");
		stopButton.setFocusable(false);
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(APP.screen);
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
		fpsCheckBox.setFocusable(false);
		fpsCheckBox.setSelected(APP.FPS_DRAW);
		fpsCheckBox.setActionCommand("fpsDraw");
		fpsCheckBox.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(fpsCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		stopSignCheckBox = new JCheckBox("draw stop signs");
		stopSignCheckBox.setFocusable(false);
		stopSignCheckBox.setSelected(APP.STOPSIGN_DRAW);
		stopSignCheckBox.setActionCommand("stopSignDraw");
		stopSignCheckBox.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(stopSignCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		carTextureCheckBox = new JCheckBox("draw car textures");
		carTextureCheckBox.setFocusable(false);
		carTextureCheckBox.setSelected(APP.CARTEXTURE_DRAW);
		carTextureCheckBox.setActionCommand("carTextureDraw");
		carTextureCheckBox.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(carTextureCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		explosionsCheckBox = new JCheckBox("draw explosions");
		explosionsCheckBox.setFocusable(false);
		explosionsCheckBox.setSelected(APP.EXPLOSIONS_DRAW);
		explosionsCheckBox.setActionCommand("explosionsDraw");
		explosionsCheckBox.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(explosionsCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		debugCheckBox = new JCheckBox("debug draw");
		debugCheckBox.setFocusable(false);
		debugCheckBox.setSelected(APP.DEBUG_DRAW);
		debugCheckBox.setActionCommand("debugDraw");
		debugCheckBox.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(debugCheckBox);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		dtField = new JTextField();
		dtField.setFocusable(false);
		dtField.setText(Double.toString(APP.dt));
		dtField.setMaximumSize(new Dimension(10000, 100));
		dtField.setActionCommand("dt");
		dtField.addActionListener(APP.screen);
		hBox = Box.createHorizontalBox();
		hBox.add(new JLabel("dt"));
		hBox.add(dtField);
		hBox.add(Box.createHorizontalGlue());
		verticalBox.add(hBox);
		
		verticalBox.add(Box.createVerticalGlue());
		
		previewPanel = new PreviewPanel();
		
		Box previewBox = Box.createHorizontalBox();
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		previewBox.add(previewPanel.java());
		//buttonBox.add(Box.createHorizontalGlue());
		previewBox.add(Box.createHorizontalGlue());
		verticalBox.add(previewBox);
		
		verticalBox.add(Box.createRigidArea(new Dimension(0, 30)));
		
		add(verticalBox);
	}
	
}
