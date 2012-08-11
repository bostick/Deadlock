package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.core.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.core.view.DeadlockView.VIEW;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.core.DPoint;
import com.gutabi.deadlock.core.Edge;
import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.core.Vertex;
import com.gutabi.deadlock.core.controller.ControlMode;
import com.gutabi.deadlock.core.model.Car;

@SuppressWarnings("serial")
public class WorldPanel extends JPanel {
	
	public Color background = new Color(0, 127, 0);
	
	Logger logger = Logger.getLogger("deadlock");
	
	public WorldPanel() {
		
		//setPreferredSize(new Dimension(1600, 900));
		setPreferredSize(new Dimension(MODEL.WORLD_WIDTH, MODEL.WORLD_HEIGHT));
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Dimension dim = this.getSize();
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 0, MODEL.WORLD_WIDTH, 10);
		g2.fillRect(0, 10, 10, MODEL.WORLD_HEIGHT-10);
		g2.fillRect(MODEL.WORLD_WIDTH-10, 10, 10, MODEL.WORLD_HEIGHT-10);
		g2.fillRect(10, MODEL.WORLD_HEIGHT-10, MODEL.WORLD_WIDTH-20, 10);
		
		g2.setColor(background);
		g2.fillRect(10, 10, MODEL.WORLD_WIDTH-20, MODEL.WORLD_HEIGHT-20);
		
		g2.scale(VIEW.getZoom(), VIEW.getZoom());
		g2.translate((double)-VIEW.viewLoc.x, (double)-VIEW.viewLoc.y);
		
		
		
		List<Edge> edgesCopy;
		List<Vertex> verticesCopy;
		ControlMode modeCopy;
		List<DPoint> curStrokeCopy;
		DPoint lastPointCopy;
		List<Car> carsCopy;
		
		synchronized (MODEL) {
			edgesCopy = new ArrayList<Edge>();
			for (Edge e : MODEL.getEdges()) {
				edgesCopy.add(e.copy());
			}
			verticesCopy = new ArrayList<Vertex>();
			for (Vertex v : MODEL.getVertices()) {
				verticesCopy.add(v.copy());
			}
			modeCopy = MODEL.getMode();
			curStrokeCopy = new ArrayList<DPoint>(MODEL.curStrokeRaw);
			lastPointCopy = MODEL.lastPointRaw;
			carsCopy = new ArrayList<Car>();
			for (Car c : MODEL.cars) {
				carsCopy.add(c);
			}
		}
		
		for (Edge e : edgesCopy) {
			paintEdge1(e, g2);
		}
		for (Vertex v : verticesCopy) {
			paintVertex(v, g2);
		}
		for (Edge e : edgesCopy) {
			paintEdge2(e, g2);
		}
		
		if (modeCopy == ControlMode.DRAFTING) {
			g2.setColor(Color.RED);
			int size = curStrokeCopy.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				DPoint p = curStrokeCopy.get(i);
				xPoints[i] = (int)p.x;
				yPoints[i] = (int)p.y;
			}
			
			g2.setStroke(new Road1Stroke());
			g2.drawPolyline(xPoints, yPoints, size);
			
			g2.setColor(Color.RED);
			if (lastPointCopy != null) {
				g2.fillOval((int)(lastPointCopy.x-5), (int)(lastPointCopy.y-5), 10, 10);
			}
		} else if (modeCopy == ControlMode.RUNNING) {
			
			g2.setColor(Color.BLUE);
			for (Car c : carsCopy) {
				DPoint pos = c.getPosition();
				g2.fillOval((int)(pos.x-5), (int)(pos.y-5), 10, 10);
			}
			
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
		
		switch (v.getType()) {
		case SINK:
			g.setColor(Color.RED);
			break;
		case SOURCE:
			g.setColor(Color.GREEN);
			break;
		case COMMON:
			g.setColor(new Color(0x44, 0x44, 0x44, 0xff));
			break;
		}
		
		Point p = v.getPoint();
		g.fillOval(p.x-5, p.y-5, 10, 10);
		
	}
	
}
