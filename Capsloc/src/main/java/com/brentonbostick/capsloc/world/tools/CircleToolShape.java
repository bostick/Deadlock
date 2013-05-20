package com.brentonbostick.capsloc.world.tools;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import com.brentonbostick.capsloc.geom.Ellipse;
import com.brentonbostick.capsloc.geom.GeometryPath;
import com.brentonbostick.capsloc.geom.Polyline;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.ui.paint.RenderingContext;
import com.brentonbostick.capsloc.world.graph.Vertex;

public class CircleToolShape {
	
	public final Point p;
	public final double xRadius;
	public final double yRadius;
	
	public final Ellipse c0;
	public final Ellipse c1;
	GeometryPath path = APP.platform.createGeometryPath();
	
	public final Point[] skeleton;
	public final Polyline skeletonShape;
	
	public CircleToolShape(Point p, double xRadius, double yRadius) {
		this.p = p;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		
		c0 = new Ellipse(p, xRadius, yRadius);
		c1 = new Ellipse(p, xRadius + 2 * Vertex.INIT_VERTEX_RADIUS, yRadius + 2 * Vertex.INIT_VERTEX_RADIUS);
		path.add(c0);
		path.add(c1);
		
		skeleton = new Ellipse(p, xRadius + Vertex.INIT_VERTEX_RADIUS, yRadius + Vertex.INIT_VERTEX_RADIUS).skeleton();
		skeletonShape = new Polyline(skeleton);
		
	}
	
	public void draw(RenderingContext ctxt) {
		path.draw(ctxt);
	}
	
}
