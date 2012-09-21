package com.gutabi.deadlock.core;

public class SinkedPosition extends Position {
	
	Sink s;
	
	public SinkedPosition(Sink s) {
		super(s.getPoint());
		this.s = s;
	}
	
	public Driveable getDriveable() {
		return s;
	}
	
	public Sink getSink() {
		return s;
	}
	
	public Position nextToward(Position goal) {
		
		if (goal instanceof EdgePosition) {
			EdgePosition ge = (EdgePosition)goal;
			
			
			
		} else if (goal instanceof Vertex) {
			Vertex gv = (Vertex)goal;
			
			
		} else {
			SinkedPosition gs = (SinkedPosition)goal;
			
		}
		
	}
	
	public double distanceTo(Position b) {
		return s.distanceTo(b);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof SinkedPosition)) {
			return false;
		} else {
			SinkedPosition b = (SinkedPosition)o;
			return (s == b.s);
		}
	}
}
