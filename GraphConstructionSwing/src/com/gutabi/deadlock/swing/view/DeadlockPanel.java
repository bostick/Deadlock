package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

@SuppressWarnings("serial")
public class DeadlockPanel extends JPanel {
	
	public Color background = Color.WHITE;
	
	Logger logger = Logger.getLogger("deadlock");
	
	public DeadlockPanel() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Dimension dim = this.getSize();
		
		g2.setColor(background);
		g2.fillRect(0, 0, dim.width, dim.height);
		
		g2.scale(VIEW.zoom, VIEW.zoom);
		g2.translate((double)-VIEW.cameraUpperLeft.x, (double)-VIEW.cameraUpperLeft.y);
		//g2.scale(VIEW.zoom, VIEW.zoom);
		
		for (Edge e : MODEL.graph.getEdges()) {
			paintEdge(e, g2);
		}
		for (Vertex v : MODEL.graph.getVertices()) {
			paintVertex(v, g2);
		}
		
		g2.setColor(Color.RED);
		for (int i = 0; i < CONTROLLER.curStrokeRaw.size()-1; i++) {
			Point a = CONTROLLER.curStrokeRaw.get(i);
			Point b = CONTROLLER.curStrokeRaw.get(i+1);
			g2.drawLine(a.x, a.y, b.x, b.y);
		}
		
		g2.setColor(Color.RED);
		if (CONTROLLER.lastPointRaw != null) {
			//g2.fillOval(CONTROLLER.lastPointRaw.x-5, CONTROLLER.lastPointRaw.y-5, 10, 10);
			g2.fillRect(CONTROLLER.lastPointRaw.x, CONTROLLER.lastPointRaw.y, 1, 1);
		}
		
	}
	
	private static void paintEdge(Edge e, Graphics2D g) {
		g.setColor(Color.BLUE);
		for (int i = 0; i < e.size()-1; i++) {
			Point prev = e.getPoint(i);
			Point cur = e.getPoint(i+1);
			//canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint1);
			g.drawLine(prev.x, prev.y, cur.x, cur.y);
		}
	}
	
	public static void paintVertex(Vertex v, Graphics2D g) {
		g.setColor(Color.GREEN);
		Point p = v.getPoint();
		//g.fillOval(p.x-3, p.y-3, 6, 6);
		//g.fillOval(p.x-1, p.y-1, 2, 2);
		g.fillRect(p.x, p.y, 1, 1);
	}
	
}
