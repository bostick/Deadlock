package com.gutabi.deadlock.world.tools;

import java.util.Set;

import com.gutabi.deadlock.geom.Shape;
import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.paint.Cap;
import com.gutabi.deadlock.ui.paint.Color;
import com.gutabi.deadlock.ui.paint.Join;
import com.gutabi.deadlock.ui.paint.RenderingContext;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class MergerTool extends ToolBase {
	
	MergerToolShape shape;
	
	public MergerTool(WorldScreen worldScreen) {
		super(worldScreen);
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
		
		worldScreen.tool = new RegularTool(worldScreen);
		
		worldScreen.tool.setPoint(worldScreen.world.quadrantMap.getPoint(worldScreen.world.lastMovedOrDraggedWorldPoint));
		
		worldScreen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (worldScreen.world.quadrantMap.contains(shape)) {
			
			if (worldScreen.world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = worldScreen.world.createMerger(p);
				worldScreen.world.graph.computeVertexRadii(affected);
				
				worldScreen.tool = new RegularTool(worldScreen);
				
				worldScreen.tool.setPoint(worldScreen.world.lastMovedWorldPoint);
				
				worldScreen.world.render_worldPanel();
				worldScreen.world.render_preview();
				worldScreen.contentPane.repaint();
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		worldScreen.tool.setPoint(worldScreen.world.quadrantMap.getPoint(worldScreen.world.lastMovedOrDraggedWorldPoint));
		worldScreen.contentPane.repaint();
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
