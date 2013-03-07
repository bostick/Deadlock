package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
	public Map<Config, Config> generatingMap = new HashMap<Config, Config>();
	public Map<Config, Config> solvingMap = new HashMap<Config, Config>();
	
	public List<Config> lastIteration = new ArrayList<Config>();
	public long lastIterationMoves;
	
	public List<Config> allIterations = new ArrayList<Config>();
	
	Set<Config> set = new HashSet<Config>();
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void putGenerating(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		set.add(key);
		
		generatingMap.put(key, val);
		
	}
	
	public void putSolving(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		set.add(key);
		
		solvingMap.put(key, val);
		
	}
	
	public boolean allIterationsContains(Config k) {
		return set.contains(k);
	}
	
//	public Set<Config> keySet() {
//		return map.keySet();
//	}
	
	public Config getSolving(Config key) {
		return solvingMap.get(key);
	}
	
	public Config getGenerating(Config key) {
		return generatingMap.get(key);
	}
	
}
