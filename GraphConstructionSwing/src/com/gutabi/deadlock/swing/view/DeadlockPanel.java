package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.Point;

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
		
		for (Edge e : MODEL.getEdges()) {
			e.paint(g2);
		}
		for (Vertex v : MODEL.getVertices()) {
			v.paint(g2);
		}
		
		g2.setColor(Color.RED);
		for (int i = 0; i < MODEL.curStrokeRaw.size()-1; i++) {
			Point a = MODEL.curStrokeRaw.get(i);
			Point b = MODEL.curStrokeRaw.get(i+1);
			g2.drawLine(a.x, a.y, b.x, b.y);
		}
		
		g2.setColor(Color.RED);
		if (MODEL.lastPointRaw != null) {
			g2.fillOval(MODEL.lastPointRaw.x-5, MODEL.lastPointRaw.y-5, 10, 10);
		}
		
//		g2.setColor(Color.GREEN);
//		for (int i = 0; i < MODEL.curStroke1.size()-1; i++) {
//			Point a = MODEL.curStroke1.get(i);
//			Point b = MODEL.curStroke1.get(i+1);
//			g2.drawLine(a.x, a.y, b.x, b.y);
//		}
		
	}
	
}
