package generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
	public Map<Config, Config> map = new HashMap<Config, Config>();
	public Map<Config, Integer> distMap = new HashMap<Config, Integer>();
	
	public Map<Config, Set<Config>> childrenMap = new HashMap<Config, Set<Config>>();
	
	public int hashCode() {
		int h = 17;
		h = 37 * h + map.hashCode();
		h = 37 * h + distMap.hashCode();
		return h;
	}
	
	public void remove(Config key, Config val) {
		
//		assert key.parent == val;
		assert childrenMap.get(val).contains(key);
		
		boolean res = childrenMap.get(val).remove(key);
		assert res;
//		key.parent = null;
		
	}
	
	public void put(Config key, Config val) {
		
		int dist;
		if (val != null) {
			Set<Config> children = childrenMap.get(val);
			if (children == null) {
				children = new HashSet<Config>();
				childrenMap.put(val, children);
			}
			boolean res = children.add(key);
			assert res;
			dist = distMap.get(val)+1;
		} else {
			dist = 0;
		}
//		key.parent = val;
		
		map.put(key, val);
		
		
		if (key.hashCode() == 884982332 && dist == 229) {
			String.class.getName();
		}
		if (key.hashCode() == -810170180 && dist == 232) {
			String.class.getName();
		}
		Integer oldDist = distMap.put(key, dist);
		
		if (oldDist != null) {
			/*
			 * update all children recursively
			 */
			int diff = oldDist - dist;
			
			updateChildren(key, diff);
			
		} else {
			Set<Config> children = childrenMap.get(key);
			assert children == null || children.isEmpty();
		}
		
	}
	
	void updateChildren(Config a, int diff) {
		
		Set<Config> children = childrenMap.get(a);
		if (children == null) {
			return;
		}
		for (Config c : children) {
			int oldDist = distMap.get(c);
			
			if (c.hashCode() == -810170180 && oldDist-diff == 232) {
				String.class.getName();
			}
			distMap.put(c, oldDist-diff);
			
			updateChildren(c, diff);
		}
		
	}
	
	public Set<Config> keySet() {
		return map.keySet();
	}
	
	public Config get(Config key) {
		Config val = map.get(key);
//		assert key.parent == val;
		return val;
	}
	
	public int distanceToStart(Config c) {
		return distMap.get(c);
	}
	
}
