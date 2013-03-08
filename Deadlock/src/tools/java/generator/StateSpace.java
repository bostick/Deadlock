package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import solver.Config;

public class StateSpace {
	
//	public Map<Config, Config> generatingMap = new HashMap<Config, Config>();
//	public Map<Config, Config> solvingMap = new HashMap<Config, Config>();
	
	public Config lastConfig;
	
	public List<Config> lastIteration = new ArrayList<Config>();
	public long lastIterationMoves;
	
//	public List<Config> allIterations = new ArrayList<Config>();
	
	public Set<Config> allIterations = new TreeSet<Config>(comparator for configs as byte[]);
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void putGenerating(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		
//		generatingMap.put(key, val);
		key.generatingVal = val;
		
		
		Config oldLastConfig = lastConfig;
		lastConfig = key;
		lastConfig.previousGeneratingConfig = oldLastConfig;
		
	}
	
	public void putSolving(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		
//		solvingMap.put(key, val);
		key.solvingVal = val;
		
	}
	
	public boolean allIterationsContains(Config k) {
		return allIterations.contains(k);
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
