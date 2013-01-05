package com.gutabi.deadlock.world.graph;

import java.awt.Color;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.ui.RenderingContext;
import com.gutabi.deadlock.world.World;

public class RushHourBoard {
	
	World world;
	Point p;
	
	RushHourStud[][] studs = new RushHourStud[6][6];
	
	public AABB aabb;
	
	public RushHourBoard(World world, Point p) {
		this.world = world;
		this.p = p;
		
		aabb = new AABB(p.x - 3, p.y - 3, 6, 6);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				
				studs[i][j] = new RushHourStud(world, new AABB(aabb.ul.x + j, aabb.ul.y + i, 1, 1));
				
			}
		}
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		aabb.paint(ctxt);
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				studs[i][j].paint(ctxt);
			}
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		aabb.paint(ctxt);
		
	}

}
