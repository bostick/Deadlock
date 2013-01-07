package com.gutabi.deadlock.world.tools;

import java.util.Set;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.math.geom.Shape;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

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
	
	public void escKey() {
		
		screen.tool = new RegularTool(screen);
		
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		
		screen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (screen.world.quadrantMap.completelyContains(shape)) {
			
			if (screen.world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = screen.world.createMerger(p);
				screen.world.graph.computeVertexRadii(affected);
				
				screen.tool = new RegularTool(screen);
				
				screen.tool.setPoint(screen.world.lastMovedWorldPoint);
				
				screen.world.render_worldPanel();
				screen.world.render_preview();
				screen.contentPane.repaint();
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		screen.tool.setPoint(screen.world.quadrantMap.getPoint(screen.world.lastMovedOrDraggedWorldPoint));
		screen.contentPane.repaint();
	}
	
	public void draw(RenderingContext ctxt) {
	
		if (p == null) {
			return;
		}
		
		ctxt.setColor(Color.WHITE);
		ctxt.setXORMode(Color.BLACK);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		shape.draw(ctxt);
		
		ctxt.setPaintMode();
	}

}
