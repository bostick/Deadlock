package com.gutabi.deadlock.world;

import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.gutabi.deadlock.controller.InputEvent;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.geom.AABB;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;

public class Preview {
	
	public static final int PREVIEW_WIDTH = 100;
	public static final int PREVIEW_HEIGHT = 100;
	
	public final World world;
	
	public BufferedImage previewImage;
	
	public Preview(World world) {
		this.world = world;
	}
	
	public void init() {
		previewImage = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	public void dragged(InputEvent ev) {
		if (VIEW.previewPanel.penDragPreviewPoint != null) {
			
			double dx = VIEW.previewPanel.lastDragPreviewPoint.x - VIEW.previewPanel.penDragPreviewPoint.x;
			double dy = VIEW.previewPanel.lastDragPreviewPoint.y - VIEW.previewPanel.penDragPreviewPoint.y;
			
			pan(new Point(dx, dy));
			
			world.render();
			world.repaint();
		}
	}
	
	public Point previewToWorld(Point p) {
		return new Point((world.worldWidth / PREVIEW_WIDTH) * p.x, (world.worldHeight / PREVIEW_HEIGHT) * p.y);
	}
	
	public Point worldToPreview(Point p) {
		return new Point((PREVIEW_WIDTH / world.worldWidth) * p.x, (PREVIEW_HEIGHT / world.worldHeight) * p.y);
	}
	
	public void pan(Point prevDp) {
		Point worldDP = previewToWorld(prevDp);
		
		world.worldViewport = new AABB(
				world.worldViewport.x + worldDP.x,
				world.worldViewport.y + worldDP.y,
				world.worldViewport.width,
				world.worldViewport.height);
	}
	
	public void render() {
		
		Graphics2D previewImageG2 = previewImage.createGraphics();
		
		previewImageG2.setColor(Color.WHITE);
		previewImageG2.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		previewImageG2.scale(PREVIEW_WIDTH / world.worldWidth, PREVIEW_HEIGHT / world.worldHeight);
		
		RenderingContext previewContext = new RenderingContext(previewImageG2, RenderingContextType.PREVIEW);
		
		world.quadrantMap.renderBackground(previewContext);
		
		world.graph.renderBackground(previewContext);
		
		previewImageG2.dispose();
		
	}
	
	public void paint(RenderingContext ctxt) {
		
		if (previewImage != null) {
			
//			g2.drawImage(previewImage, 0, 0, null);
			ctxt.paintImage(0, 0, previewImage,
					0, 0, previewImage.getWidth(), previewImage.getHeight(),
					0, 0, previewImage.getWidth(), previewImage.getHeight());
			
			Point prevLoc = worldToPreview(world.worldViewport.ul);
			
			Point prevDim = worldToPreview(new Point(world.worldViewport.width, world.worldViewport.height));
			
			AABB prev = new AABB(prevLoc.x, prevLoc.y, prevDim.x, prevDim.y);
			
//			g2.setColor(Color.BLUE);
//			g2.drawRect((int)prevLoc.x, (int)prevLoc.y, (int)prevDim.x, (int)prevDim.y);
			
			ctxt.setColor(Color.BLUE);
			prev.draw(ctxt);
			
		}
		
	}
	
}
