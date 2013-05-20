package com.brentonbostick.capsloc.geom;

import com.brentonbostick.capsloc.ui.paint.RenderingContext;

public interface GeometryPath {
	
	void reset();
	
	void add(QuadCurve q);
	
	void add(CubicCurve c);
	
	void add(Ellipse e);
	
	void add(Circle c);
	
	void add(OBB o);
	
	void add(MutableOBB o);
	
	void add(Triangle t);
	
	void add(Polygon p);
	
	void add(MutablePolygon p);
	
	void add(Polyline p);
	
	
	
	void paint(RenderingContext ctxt);

	void draw(RenderingContext ctxt);
}
