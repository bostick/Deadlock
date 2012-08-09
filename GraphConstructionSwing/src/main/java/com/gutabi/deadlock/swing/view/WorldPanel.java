package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.core.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.core.DPoint;
import com.gutabi.core.Edge;
import com.gutabi.core.Point;
import com.gutabi.core.Vertex;
import com.gutabi.deadlock.core.controller.ControlMode;

@SuppressWarnings("serial")
public class WorldPanel extends JPanel {
	
	public Color background = new Color(0, 127, 0);
	
	Logger logger = Logger.getLogger("deadlock");
	
	public WorldPanel() {
		
		//setPreferredSize(new Dimension(1600, 900));
		setPreferredSize(new Dimension(1400, 822));
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Dimension dim = this.getSize();
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 0, dim.width, 10);
		g2.fillRect(0, 10, 10, dim.height-10);
		g2.fillRect(dim.width-10, 10, 10, dim.height-10);
		g2.fillRect(10, dim.height-10, dim.width-20, 10);
		
		g2.setColor(background);
		g2.fillRect(10, 10, dim.width-20, dim.height-20);
		
		g2.scale(VIEW.getZoom(), VIEW.getZoom());
		g2.translate((double)-VIEW.viewLoc.x, (double)-VIEW.viewLoc.y);
		
		for (Edge e : MODEL.getEdges()) {
			paintEdge1(e, g2);
		}
		for (Vertex v : MODEL.getVertices()) {
			paintVertex(v, g2);
		}
		for (Edge e : MODEL.getEdges()) {
			paintEdge2(e, g2);
		}
		
		if (CONTROLLER.mode == ControlMode.DRAWING) {
			g2.setColor(Color.RED);
			int size = CONTROLLER.curStrokeRaw.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				DPoint p = CONTROLLER.curStrokeRaw.get(i);
				xPoints[i] = (int)p.x;
				yPoints[i] = (int)p.y;
			}
			
			g2.setStroke(new Road1Stroke());
			g2.drawPolyline(xPoints, yPoints, size);
			
			g2.setColor(Color.RED);
			if (CONTROLLER.lastPointRaw != null) {
				g2.fillOval((int)(CONTROLLER.lastPointRaw.x-5), (int)(CONTROLLER.lastPointRaw.y-5), 10, 10);
			}
		} else if (CONTROLLER.mode == ControlMode.RUNNING) {
			
			
			
		}
		
	}
	
	public static class Road1Stroke extends BasicStroke {
		
		public Road1Stroke() {
			super(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}
		
	}
	
	public static class Road2Stroke extends BasicStroke {
		
		public Road2Stroke() {
			super(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}

	}
	
	private static void paintEdge1(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = e.getPoint(i);
			xPoints[i] = p.x;
			yPoints[i] = p.y;
		}
		g.setColor(new Color(0x88, 0x88, 0x88, 0xff));
		g.setStroke(new Road1Stroke());
		g.drawPolyline(xPoints, yPoints, size);
		
	}
	
	private static void paintEdge2(Edge e, Graphics2D g) {
		int size = e.size();
		int[] xPoints = new int[size];
		int[] yPoints = new int[size];
		for (int i = 0; i < size; i++) {
			Point p = e.getPoint(i);
			xPoints[i] = p.x;
			yPoints[i] = p.y;
		}
		
		g.setColor(Color.YELLOW);
		g.setStroke(new Road2Stroke());
		g.drawPolyline(xPoints, yPoints, size);
	}
	
	public static void paintVertex(Vertex v, Graphics2D g) {
		
		//VertexType type = (VertexType)v.getMetaData().get("type");
		
		if (MODEL.getSources().contains(v)) {
			g.setColor(Color.GREEN);
		} else if (MODEL.getSinks().contains(v)) {
			g.setColor(Color.RED);
		} else {
			g.setColor(new Color(0x44, 0x44, 0x44, 0xff));
		}
		
		Point p = v.getPoint();
		g.fillOval(p.x-5, p.y-5, 10, 10);
		
	}
	
}
