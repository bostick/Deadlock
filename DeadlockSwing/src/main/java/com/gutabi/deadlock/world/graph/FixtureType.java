package com.gutabi.deadlock.world.graph;

public enum FixtureType {

	SOURCE,
	
	SINK;
	
	public FixtureType other() {
		switch (this) {
		case SOURCE:
			return SINK;
		case SINK:
			return SOURCE;
		}
		return null;
	}
	
}
