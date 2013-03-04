package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class StateSpace {
	
//	public Map<Config, Config> map = new HashMap<Config, Config>();
	
	public List<Config> lastIteration = new ArrayList<Config>();
	
	public List<Config> allIterations = new ArrayList<Config>();
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void put(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
//		map.put(key, val);
		
		key.v = val;
		
	}
	
//	public Set<Config> keySet() {
//		return map.keySet();
//	}
	
	public Config get(Config key) {
		return key.v;
	}
	
}
