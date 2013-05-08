package com.gutabi.capsloc.world.graph;

import static com.gutabi.capsloc.CapslocApplication.APP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.paint.Cap;
import com.gutabi.capsloc.ui.paint.Color;
import com.gutabi.capsloc.ui.paint.Join;
import com.gutabi.capsloc.ui.paint.RenderingContext;
import com.gutabi.capsloc.world.World;

public class Intersection extends Vertex {
	
	public Intersection(World world, Point p) {
		super(world, p);
	}
	
	public boolean isUserDeleteable() {
		return true;
	}
	
	public boolean supportsStopSigns() {
		return true;
	}
	
	public void preStart() {
		
	}
	
	public void postStop() {
		driverQueue.clear();
	}
	
	public boolean preStep(double t) {
		return false;
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
	
	public static Intersection fromFileString(World world, String s) {
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
		
		Intersection i = new Intersection(world, p);
		
		i.id = id;
		
		return i;
	}
	
	public void render() {
		
	}
	
	public void paint_panel(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		shape.paint(ctxt);
		
		if (APP.DEBUG_DRAW) {
			shape.aabb.draw(ctxt);
		}
		
	}
	
	public void paint_preview(RenderingContext ctxt) {
		
		ctxt.setColor(Color.GRAY);
		
		shape.paint(ctxt);
		
	}
	
	public void paintScene(RenderingContext ctxt) {
		
	}
	
	public void paintHilite(RenderingContext ctxt) {
		
		ctxt.setColor(Color.WHITE);
		ctxt.setStroke(0.0, Cap.SQUARE, Join.MITER);
		
		shape.draw(ctxt);
		
	}
}
