package com.gutabi.deadlock.core;

import static com.gutabi.deadlock.model.DeadlockModel.MODEL;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public class Vertex extends GraphPosition implements Hilitable {
	
	private final int hash;
	
	protected final List<Edge> eds = new ArrayList<Edge>();
	
	protected boolean removed = false;
	
	public int id;
	
	protected Color color;
	protected Color hiliteColor;
	
	public Body b2dBody;
	public CircleShape b2dShape;
	public Fixture b2dFixture;
	
	public Vertex(Point p) {
		super(p);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position = new Vec2((float)p.getX(), (float)p.getY());
        b2dBody = MODEL.world.b2dWorld.createBody(bodyDef);
		b2dBody.setUserData(this);
        
        b2dShape = new CircleShape();
        b2dShape.m_radius = (float)MODEL.world.VERTEX_RADIUS;
        
        FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = b2dShape;
        fixtureDef.isSensor = true;

        b2dFixture = b2dBody.createFixture(fixtureDef);
		
		int h = 17;
		h = 37 * h + p.hashCode();
		hash = h;
		
	}
	
	public Hilitable getHilitable() {
		return this;
	}
	
	public int hashCode() {
		return hash;
	}
	
	public boolean isBound() {
		return true;
	}
	
	public GraphPosition nextBoundToward(GraphPosition goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			
			if (this == ge.getEdge().getEnd()) {
				return EdgePosition.nextBoundfromEnd(ge.getEdge());
			} else {
				return EdgePosition.nextBoundfromStart(ge.getEdge());
			}
			
		} else {
			Vertex gv = (Vertex)goal;
			
			if (gv == this) {
				throw new IllegalArgumentException();
			}
			
			Edge e = Vertex.commonEdge(this, gv);
			
			if (this == e.getEnd()) {
				return EdgePosition.nextBoundfromEnd(e);
			} else {
				return EdgePosition.nextBoundfromStart(e);
			}
			
		}
		
	}
	
	public boolean equalsP(GraphPosition o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vertex)) {
			return false;
		} else {
			Vertex b = (Vertex)o;
			return (p.equals(b.p));
		}
	}
	
	public String toString() {
		return "V " + p;
	}
	
	
	
	/**
	 * the specific way to travel
	 */
	public GraphPosition travel(Edge e, Vertex dest, double dist) {
		if (!(eds.contains(e))) {
			throw new IllegalArgumentException();
		}
		if (DMath.equals(dist, 0.0)) {
			return this;
		}
		if (dist < 0.0) {
			throw new IllegalArgumentException();
		}
		
		double totalEdgeLength = e.getTotalLength();
		if (DMath.equals(dist, totalEdgeLength)) {
			return dest;
		} else if (dist > totalEdgeLength) {
			throw new TravelException();
		}
		
		if (this == e.getStart()) {
			assert dest == e.getEnd();
			return EdgePosition.travelFromStart(e, dist);
		} else {
			assert this == e.getEnd();
			assert dest == e.getStart();
			return EdgePosition.travelFromEnd(e, dist);
		}
		
	}
	
	public double distanceTo(GraphPosition b) {
		if (b instanceof Vertex) {
			Vertex bb = (Vertex)b;
			
			double dist = MODEL.world.graph.distanceBetweenVertices(this, bb);
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		} else {
			EdgePosition bb = (EdgePosition)b;
			
			double bbStartPath = MODEL.world.graph.distanceBetweenVertices(this, bb.getEdge().getStart());
			double bbEndPath = MODEL.world.graph.distanceBetweenVertices(this, bb.getEdge().getEnd());
			
			double dist = Math.min(bbStartPath + bb.distanceToStartOfEdge(), bbEndPath + bb.distanceToEndOfEdge());
			
			assert DMath.greaterThanEquals(dist, 0.0);
			
			return dist;
		}
	}
	
	
	
	public static List<Edge> commonEdges(Vertex a, Vertex b) {
		
		if (a == b) {
			throw new IllegalArgumentException();
		}
		
		List<Edge> common = new ArrayList<Edge>();
		for (Edge e1 : a.eds) {
			for (Edge e2 : b.eds) {
				if (e1 == e2) {
					common.add(e1);
				}
			}
		}
		
		return common;
	}
	
	public static Edge commonEdge(Vertex a, Vertex b) {
		
		List<Edge> common = commonEdges(a, b);
		
		if (common.size() != 1) {
			throw new IllegalArgumentException();
		} else {
			return common.get(0);
		}
		
	}
	
	public void addEdge(Edge e) {
		assert e != null;
		if (removed) {
			throw new IllegalStateException("vertex has been removed");
		}
		eds.add(e);
	}
	
	public void removeEdge(Edge e) {
		if (removed) {
			throw new IllegalStateException();
		}
		assert eds.contains(e);
		eds.remove(e);
	}
	
	public List<Edge> getEdges() {
		if (removed) {
			throw new IllegalStateException();
		}
		return eds;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public void remove() {
		assert !removed;
		assert eds.size() == 0;
		
		b2dBody.destroyFixture(b2dFixture);
		MODEL.world.b2dWorld.destroyBody(b2dBody);
		
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void paint(Graphics2D g2) {
		
		g2.setColor(color);
		
		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-MODEL.world.VERTEX_RADIUS, -MODEL.world.VERTEX_RADIUS)));
		
		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER));
		
		g2.setColor(Color.WHITE);
		
		g2.drawString(Integer.toString(id), (int)loc.getX(), (int)(loc.getY() + MODEL.world.VERTEX_RADIUS * MODEL.world.PIXELS_PER_METER));
	}
	
	public void paintHilite(Graphics2D g2) {
		
		g2.setColor(hiliteColor);
		
		Point loc = VIEW.worldToPanel(getPoint().add(new Point(-MODEL.world.VERTEX_RADIUS, -MODEL.world.VERTEX_RADIUS)));
		
		g2.fillOval((int)loc.getX(), (int)loc.getY(), (int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER), (int)(MODEL.world.VERTEX_RADIUS * 2 * MODEL.world.PIXELS_PER_METER));
		
	}
	
	public void check() {
		assert !isRemoved();
		assert getPoint() != null;
		
		int edgeCount = eds.size();
		
		assert edgeCount != 0;
		
		/*
		 * edgeCount cannot be 2, edges should just be merged
		 */
		assert edgeCount != 2;
		
		int count;
		for (Edge e : eds) {
			
			assert !e.isRemoved();
			
			count = 0;
			for (Edge f : getEdges()) {
				if (e == f) {
					count++;
				}
			}
			if (e.getStart() == this && e.getEnd() == this) {
				/*
				 * loop with one intersection, so count of edges is 2 for this edge
				 */
				assert count == 2;
			} else {
				/*
				 * otherwise, all edges in v should be unique
				 */
				assert count == 1;
			}
		}
		
	}
	
}
