package com.gutabi.deadlock.world.graph;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.ui.RenderingContext;

public class Intersection extends Vertex {
	
	public Intersection(Point p) {
		super(p);
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public boolean supportsStopSigns() {
		return true;
	}
	
	public void preStart() {
		;
	}
	
	public void postStop() {
		driverQueue.clear();
	}
	
	public void preStep(double t) {
		;
	}
	
	public boolean postStep(double t) {
		return true;
	}
	
	public String toFileString() {
		StringBuilder s = new StringBuilder();
		
		s.append("start intersection\n");
		
		s.append("id " + id + "\n");
		
		s.append("point " + p.toFileString() + "\n");
		
		s.append("end intersection\n");
		
		return s.toString();
	}
	
	public static Intersection fromFileString(String s) {
		BufferedReader r = new BufferedReader(new StringReader(s));
		
		Point p = null;
		int id = -1;
		
		try {
			String l = r.readLine();
			assert l.equals("start intersection");
			
			l = r.readLine();
			Scanner sc = new Scanner(l);
			String tok = sc.next();
			assert tok.equals("id");
			id = sc.nextInt();
			sc.close();
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("point");
			
			String rest = sc.useDelimiter("\\A").next();
			sc.close();
			
			p = Point.fromFileString(rest);
			
			l = r.readLine();
			assert l.equals("end intersection");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intersection i = new Intersection(p);
		
		i.id = id;
		
		return i;
	}
	
	public void paint(RenderingContext ctxt) {
		
		switch (ctxt.type) {
		case CANVAS:
			ctxt.setColor(Color.GRAY);
			
			shape.paint(ctxt);
			
			if (ctxt.DEBUG_DRAW) {
				shape.getAABB().draw(ctxt);
			}
			
			break;
		case PREVIEW:
			ctxt.setColor(Color.GRAY);
			
			shape.paint(ctxt);
			break;
		}
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		;
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setPixelStroke(1.0);
		
		shape.draw(ctxt);
		
	}
}
