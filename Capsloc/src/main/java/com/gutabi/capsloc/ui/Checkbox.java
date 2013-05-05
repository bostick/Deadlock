package com.gutabi.capsloc.ui;

import static com.gutabi.capsloc.CapslocApplication.APP;

import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.FontStyle;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public abstract class Checkbox {
	
	Label lab;
	
	public boolean selected = false;
	
	public Checkbox() {
		lab = new Label(null);
		
		lab.fontFile = APP.platform.fontResource("visitor1");
		lab.fontStyle = FontStyle.PLAIN;
		lab.fontSize = 48;
	}
	
	public void setLocation(double x, double y) {
		lab.setLocation(x, y);
	}
	
	public double getWidth() {
		return lab.aabb.width;
	}
	
	public double getHeight() {
		return lab.aabb.height;
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
