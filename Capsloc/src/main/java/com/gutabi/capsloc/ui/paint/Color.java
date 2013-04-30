package com.gutabi.capsloc.ui.paint;

public class Color {
	
	public static final Color RED = new Color(255, 0, 0, 255);
	public static final Color ORANGE = new Color(255, 127, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0, 255);
	public static final Color GREEN = new Color(0, 255, 0, 255);
	public static final Color LIGHTGREEN = new Color(128, 255, 128, 255);
	public static final Color DARKGREEN = new Color(0, 128, 0, 255);
	public static final Color BLUE = new Color(0, 0, 255, 255);
	
	public static final Color WHITE = new Color(255, 255, 255, 255);
	public static final Color GRAY = new Color(128, 128, 128, 255);
	public static final Color LIGHT_GRAY = new Color(192, 192, 192, 255);
	public static final Color DARK_GRAY = new Color(64, 64, 64, 255);
	public static final Color BLACK = new Color(0, 0, 0, 255);
	
	public static final Color menuBackground = new Color(0x88, 0x88, 0x88, 255);
	
	public static final Color brown = new Color(150, 75, 0, 255);
	
	public static final Color redOrange = new Color(255, 67, 0, 255);
	
	public static final Color fixtureHiliteColor = new Color(0, 255, 255, 255);
	
	public static final Color roadHiliteColor = new Color(0xff ^ 0x88, 0xff ^ 0x88, 0xff ^ 0x88, 0xff);
	
	public final int r;
	public final int g;
	public final int b;
	public final int a;
	
	public Color(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
}
