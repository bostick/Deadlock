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
import com.gutabi.deadlock.world.DebuggerScreen;
import com.gutabi.deadlock.world.WorldScreen;
import com.gutabi.deadlock.world.graph.Vertex;

public class MergerTool extends ToolBase {
	
	MergerToolShape shape;
	
	public MergerTool(WorldScreen worldScreen, DebuggerScreen debuggerScreen) {
		super(worldScreen, debuggerScreen);
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
		
		APP.tool = new RegularTool(worldScreen, debuggerScreen);
		
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
		
		worldScreen.contentPane.repaint();
	}
	
	public void insertKey() {
		if (worldScreen.contentPane.worldPanel.world.quadrantMap.contains(shape)) {
			
			if (worldScreen.contentPane.worldPanel.world.graph.pureGraphIntersect(shape) == null) {
				
				Set<Vertex> affected = worldScreen.contentPane.worldPanel.world.createMerger(p);
				worldScreen.contentPane.worldPanel.world.graph.computeVertexRadii(affected);
				
				APP.tool = new RegularTool(worldScreen, debuggerScreen);
				
				APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.lastMovedWorldPoint);
				
				worldScreen.contentPane.worldPanel.world.render_worldPanel();
				worldScreen.contentPane.worldPanel.world.render_preview();
				worldScreen.contentPane.repaint();
			}
			
		}
	}
	
	public void moved(InputEvent ev) {
		APP.tool.setPoint(worldScreen.contentPane.worldPanel.world.quadrantMap.getPoint(worldScreen.contentPane.worldPanel.world.lastMovedOrDraggedWorldPoint));
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
