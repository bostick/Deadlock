package generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
//	public Map<Config, Config> generatingMap = new HashMap<Config, Config>();
//	public Map<Config, Config> solvingMap = new HashMap<Config, Config>();
	
	public Config lastConfig;
	
	public List<Config> lastIteration = new ArrayList<Config>();
//	public long lastIterationMoves;
	
//	public List<Config> allIterations = new ArrayList<Config>();
	
//	public Set<Config> allIterations = new TreeSet<Config>(comparator for configs as byte[]);
	public Set<Config> allconfigs = new HashSet<Config>();
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void putGenerating(Config key, Config val) {
		
		lastIteration.add(key);
		
		allconfigs.add(key);
		
//		generatingMap.put(key, val);
		key.generatingVal = val;
		
		
		Config oldLastConfig = lastConfig;
		lastConfig = key;
		lastConfig.previousGeneratingConfig = oldLastConfig;
		
	}
	
	public void putSolving(Config key, Config val) {
		
		lastIteration.add(key);
		
		allconfigs.add(key);
		
//		solvingMap.put(key, val);
		key.solvingVal = val;
		
	}
	
	public boolean allConfigsContains(Config k) {
		return allconfigs.contains(k);
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
	
}
