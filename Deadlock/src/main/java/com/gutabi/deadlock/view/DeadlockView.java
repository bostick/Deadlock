package com.gutabi.deadlock.view;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

//@SuppressWarnings("static-access")
public class DeadlockView {
	
	public static DeadlockView VIEW = new DeadlockView();
	
	public BufferedImage sheet;
	public BufferedImage explosionSheet;
	public BufferedImage titleBackground;
	public BufferedImage title_white;
	public BufferedImage copyright;
	
	public Color LIGHTGREEN = new Color(128, 255, 128);
	public Color DARKGREEN = new Color(0, 128, 0);
	
	public Color menuBackground = new Color(0x88, 0x88, 0x88);
	
	public Color brown = new Color(150, 75, 0);
	
	public Color redOrange = new Color(255, 67, 0);
	
	public Color fixtureHiliteColor = new Color(0, 255, 255);
	
	public Color roadHiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
	
	
	static final Logger logger = Logger.getLogger(DeadlockView.class);
	
	public void init() throws Exception {
		sheet = ImageIO.read(DeadlockView.class.getResource("/img/sheet.png"));
		explosionSheet = ImageIO.read(DeadlockView.class.getResource("/img/explosionSheet.png"));
		titleBackground = ImageIO.read(DeadlockView.class.getResource("/img/title_background.png"));
		title_white = ImageIO.read(DeadlockView.class.getResource("/img/title_white.png"));
		copyright = ImageIO.read(DeadlockView.class.getResource("/img/copyright.png"));
	}
	
}
