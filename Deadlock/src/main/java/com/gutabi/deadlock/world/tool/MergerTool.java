package com.gutabi.deadlock.world.tool;

import java.awt.Color;
import java.util.Set;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.Shape;
import com.gutabi.deadlock.view.InputEvent;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

//@SuppressWarnings("static-access")
public class MergerTool extends ToolBase {
	
	MergerToolShape shape;
	
	public MergerTool(WorldScreen screen) {
		super(screen);
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
	
	public void escKey(InputEvent ev) {
		
		screen.tool = new RegularTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		
		screen.repaintCanvas();
	}
	
	public void insertKey(InputEvent ev) {
		if (screen.world.quadrantMap.completelyContains(shape)) {
			
			if (screen.world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = screen.world.createMerger(p);
				screen.world.graph.computeVertexRadii(affected);
				
				screen.tool = new RegularTool(screen);
				
				screen.tool.setPoint(screen.lastMovedWorldPoint);
				
				screen.render();
				screen.repaintCanvas();
				screen.repaintControlPanel();
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.lastMovedOrDraggedWorldPoint));
		screen.repaintCanvas();
	}
	
	public void exited(InputEvent ev) {
		screen.tool.setPoint(null);
		screen.repaintCanvas();
	}
	
	public void draw(RenderingContext ctxt) {
	
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setPixelStroke(1);
		
		shape.draw(ctxt);
		
	}

}
