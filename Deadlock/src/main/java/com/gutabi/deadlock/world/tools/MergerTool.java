package com.gutabi.deadlock.world.tools;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.graph.Vertex;

public class MergerTool extends ToolBase {
	
	MergerToolShape shape;
	
	public MergerTool() {
		super();
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
		
		APP.worldScreen.tool = new RegularTool();
		
		APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
		
		APP.worldScreen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (APP.worldScreen.world.quadrantMap.contains(shape)) {
			
			if (APP.worldScreen.world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = APP.worldScreen.world.createMerger(p);
				APP.worldScreen.world.graph.computeVertexRadii(affected);
				
				APP.worldScreen.tool = new RegularTool();
				
				APP.worldScreen.tool.setPoint(APP.worldScreen.world.lastMovedWorldPoint);
				
				APP.worldScreen.world.render_worldPanel();
				APP.worldScreen.world.render_preview();
				APP.worldScreen.contentPane.repaint();
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		APP.worldScreen.tool.setPoint(APP.worldScreen.world.quadrantMap.getPoint(APP.worldScreen.world.lastMovedOrDraggedWorldPoint));
		APP.worldScreen.contentPane.repaint();
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
