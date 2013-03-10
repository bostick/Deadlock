package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solver.Config;

public class StateSpace {
	
//	public Map<Config, Config> generatingMap = new HashMap<Config, Config>();
//	public Map<Config, Config> solvingMap = new HashMap<Config, Config>();
	
	public Config lastConfig;
	
	public List<Config> lastIteration = new ArrayList<Config>();
//	public long lastIterationMoves;
	
//	public List<Config> allIterations = new ArrayList<Config>();
	
//	public Set<Config> allIterations = new TreeSet<Config>(comparator for configs as byte[]);
	private Map<Config, Integer> allconfigs = new HashMap<Config, Integer>();
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void putGenerating(Config key, Config val, Integer dist) {
		
		lastIteration.add(key);
		
		allconfigs.put(key, dist);
		
//		generatingMap.put(key, val);
		key.generatingVal = val;
		
		
		Config oldLastConfig = lastConfig;
		lastConfig = key;
		lastConfig.previousGeneratingConfig = oldLastConfig;
		
	}
	
	public void putSolving(Config key, Config val, Integer dist) {
		
		lastIteration.add(key);
		
		allconfigs.put(key, dist);
		
//		solvingMap.put(key, val);
		key.solvingVal = val;
		
	}
	
	public boolean allConfigsContains(Config k) {
		return allconfigs.keySet().contains(k);
	}
	
//	public Set<Config> keySet() {
//		return map.keySet();
//	}
	
	public Config getSolving(Config key) {
//		return solvingMap.get(key);
		return key.solvingVal;
	}
	
	public Config getGenerating(Config key) {
//		return generatingMap.get(key);
		return key.generatingVal;
	}
	
	public int allConfigsSize() {
		return allconfigs.size();
	}
	
	public void clearAllConfigs() {
		allconfigs.clear();
	}
	
	public int allConfigsGet(Config c) {
		return allconfigs.get(c);
	}
	
}
