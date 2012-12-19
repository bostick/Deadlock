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
	
	public static FixtureType fromFileString(String s) {
		if (s.equals("SOURCE")) {
			return FixtureType.SOURCE;
		} else if (s.equals("SINK")) {
			return FixtureType.SINK;
		}
		assert false;
		return null;
	}
	
}
