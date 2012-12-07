package com.gutabi.deadlock.model.graph;

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
