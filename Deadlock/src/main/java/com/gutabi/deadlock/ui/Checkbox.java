package com.gutabi.deadlock.ui;

import java.awt.Color;
import java.awt.Font;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.paint.RenderingContext;



public abstract class Checkbox {
	
	Label lab;
	
	public boolean selected = false;
	
	static private Font f = new Font("Visitor TT1 BRK", Font.PLAIN, 48);
	
	public Checkbox() {
		lab = new Label(null);
		lab.font = f;
	}
	
	public void setLocation(double x, double y) {
		lab.setLocation(x, y);
	}
	
	public int getWidth() {
		return lab.getWidth();
	}
	
	public int getHeight() {
		return lab.getHeight();
	}
	
	public boolean hitTest(Point p) {
		if (lab.aabb.hitTest(p)) {
			return true;
		}
		return false;
	}
	
	public abstract void action();
	
	public void render() {
		if (selected) {
			lab.text = "X";
		} else {
			lab.text = "O";
		}
		lab.renderLocal();
		lab.color = Color.BLACK;
		lab.render();
	}
	
	public void paint(RenderingContext ctxt) {
		lab.paint(ctxt);
		ctxt.setColor(Color.BLACK);
		lab.aabb.draw(ctxt);
	}
	
}
