package com.gutabi.deadlock.world.graph;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import com.gutabi.deadlock.core.Point;
import com.gutabi.deadlock.view.RenderingContext;
import com.gutabi.deadlock.view.RenderingContextType;
import com.gutabi.deadlock.world.WorldCamera;

//@SuppressWarnings("static-access")
public class Intersection extends Vertex {
	
	public Intersection(WorldCamera cam, Point p) {
		super(cam, p);
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
	
	public static Intersection fromFileString(WorldCamera cam, String s) {
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
			
			l = r.readLine();
			sc = new Scanner(l);
			tok = sc.next();
			assert tok.equals("point");
			
			String rest = sc.useDelimiter("\\A").next();
			
			p = Point.fromFileString(rest);
			
			l = r.readLine();
			assert l.equals("end intersection");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intersection i = new Intersection(cam, p);
		
		i.id = id;
		
		return i;
	}
	
	public void paint(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		shape.paint(ctxt);
		
		if (ctxt.type == RenderingContextType.CANVAS) {
			if (APP.DEBUG_DRAW) {
				
				shape.getAABB().draw(ctxt);
				
			}
		}
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		;
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setPixelStroke(cam.pixelsPerMeter, 1);
		
		shape.draw(ctxt);
		
	}
}
