package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.Transform;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.World;
import com.gutabi.deadlock.world.graph.Vertex;

public class MergerTool extends WorldToolBase {
	
	MergerToolShape shape;
	
	public MergerTool() {
		
	}
	
	public void setPoint(Point p) {
		this.p = p;
		
		if (p != null) {
			shape = new MergerToolShape(p);
		} else {
			shape = null;
		}	
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void escKey() {
		World world = (World)APP.model;
		
		APP.tool = new RegularTool();
		
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
		
	}
	
	public void insertKey() {
		World world = (World)APP.model;
		
		if (world.quadrantMap.contains(shape)) {
			
			if (world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = world.createMerger(p);
				world.graph.computeVertexRadii(affected);
				
				APP.tool = new RegularTool();
				
				APP.tool.setPoint(world.lastMovedWorldPoint);
				
				world.render_worldPanel();
				world.render_preview();
			}
			
		}
	}
	
	public void moved(InputEvent ignore) {
		super.moved(ignore);
		
		World world = (World)APP.model;
		
		APP.tool.setPoint(world.quadrantMap.getPoint(world.lastMovedOrDraggedWorldPoint));
	}
	
	
	
	Transform origTransform = APP.platform.createTransform();
	
	public void paint_panel(RenderingContext ctxt) {
		World world = (World)APP.model;
		
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		ctxt.getTransform(origTransform);
		
		ctxt.scale(world.worldCamera.pixelsPerMeter);
		ctxt.translate(-world.worldCamera.worldViewport.x, -world.worldCamera.worldViewport.y);
		
		shape.draw(ctxt);
		
		ctxt.setTransform(origTransform);
		
		ctxt.clearXORMode();
	}

}
