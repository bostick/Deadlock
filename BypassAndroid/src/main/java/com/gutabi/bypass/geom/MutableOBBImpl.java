package com.gutabi.bypass.geom;

import android.graphics.Paint.Style;
import android.graphics.Path;

import com.gutabi.bypass.ui.paint.RenderingContextImpl;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.RenderingContext;

public class MutableOBBImpl extends MutableOBB {
	
	Path p;
	
	public MutableOBBImpl() {
		
	}
	
	public void setShape(Point center, double preA, double xExtant, double yExtant) {
		super.setShape(center, preA, xExtant, yExtant);
		
		p = null;
	}
	
	private void computePath() {
		
		p = new Path();
		p.moveTo((float)p0.x, (float)p0.y);
		p.lineTo((float)p1.x, (float)p1.y);
		p.lineTo((float)p2.x, (float)p2.y);
		p.lineTo((float)p3.x, (float)p3.y);
		p.close();
	}

	public void paint(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		if (p == null) {
			computePath();
		}
		
		c.paint.setStyle(Style.FILL);
		c.canvas.drawPath(p, c.paint);
	}

	public void draw(RenderingContext ctxt) {
		RenderingContextImpl c = (RenderingContextImpl)ctxt;
		
		c.paint.setStyle(Style.STROKE);
		c.canvas.drawPath(p, c.paint);
	}
}
