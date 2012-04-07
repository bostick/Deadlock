package com.gutabi.deadlock.swing.view;

import static com.gutabi.deadlock.swing.model.DeadlockModel.MODEL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.swing.model.Edge;
import com.gutabi.deadlock.swing.model.Vertex;
import com.gutabi.deadlock.swing.utils.Point;

@SuppressWarnings("serial")
public class DeadlockPanel extends JPanel {
	
	public Color background = Color.WHITE;
	
	//public ComponentListener listener = new Listener();
	
	Logger logger = Logger.getLogger("deadlock");
	
	public DeadlockPanel() {
		//addComponentListener(listener);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Dimension dim = this.getSize();
		
		g2.setColor(background);
		g2.fillRect(0, 0, dim.width, dim.height);
		
		//public void paintCanas(Canvas canvas) {
			
		//canvas.drawColor(0xFFdddddd);
		
		//Set<Vertex> vs = new HashSet<Vertex>();
		
		for (Edge e : MODEL.getEdges()) {
//			vs.add(e.getStart());
//			vs.add(e.getEnd());
			e.paint(g2);
		}
		for (Vertex v : MODEL.getVertices()) {
			v.paint(g2);
		}
		
		List<Point> points = MODEL.getPointsToBeProcessed();
		if (points != null) {
			for (int j = 1; j < points.size(); j++) {
				Point prev = points.get(j-1);
				Point cur = points.get(j);
				g2.setColor(Color.BLUE);
				g2.drawLine((int)prev.x, (int)prev.y, (int)cur.x, (int)cur.y);
			}
		}
			
		//}
		
	}
	
//	public class Listener implements ComponentListener {
//
//		@Override
//		public void componentHidden(@SuppressWarnings("unused") ComponentEvent arg0) {
//			/*
//			 * 
//			 */
//		}
//
//		@Override
//		public void componentMoved(@SuppressWarnings("unused") ComponentEvent arg0) {
//			/*
//			 * 
//			 */
//		}
//
//		@Override
//		public void componentResized(ComponentEvent e) {
//			Component c = e.getComponent();
//			if (c == VIEW.panel) {
//				setSize(c.getSize());
//			} else {
//				throw new AssertionError();
//			}
//		}
//
//		@Override
//		public void componentShown(@SuppressWarnings("unused") ComponentEvent arg0) {
//			/*
//			 * 
//			 */
//		}
//		
//	}
	
}
