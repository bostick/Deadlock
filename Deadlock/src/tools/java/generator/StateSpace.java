package generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
	Map<Config, Config> map = new HashMap<Config, Config>();
	Map<Config, Integer> distMap = new HashMap<Config, Integer>();
	
	public int hashCode() {
		int h = 17;
		h = 37 * h + map.hashCode();
		h = 37 * h + distMap.hashCode();
		return h;
	}
	
	public void put(Config key, Config val, int dist) {
		map.put(key, val);
		distMap.put(key, dist);
		
		/*
		 * update all configs down the trail from key
		 */
		d;
		
	}
	
	public Set<Config> keySet() {
		return map.keySet();
	}
	
	public Config get(Config key) {
		return map.get(key);
	}
	
	public int distanceToStart(Config c) {
		return distMap.get(c);
	}
	
}
