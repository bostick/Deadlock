package generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
//	public Map<Config, Config> map = new HashMap<Config, Config>();
	
	public List<Config> lastIteration = new ArrayList<Config>();
	
	public List<Config> allIterations = new ArrayList<Config>();
	
	Set<Config> set = new HashSet<Config>();
	
//	public int hashCode() {
//		return map.hashCode();
//	}
	
	public void putGenerating(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		set.add(key);
		
		key.vGen = val;
		
	}
	
	public void putSolving(Config key, Config val) {
		lastIteration.add(key);
		allIterations.add(key);
		set.add(key);
		
		key.vSolve = val;
		
	}
	
	public boolean allIterationsContains(Config k) {
		return set.contains(k);
	}
	
//	public Set<Config> keySet() {
//		return map.keySet();
//	}
	
	public Config getSolving(Config key) {
		return key.vSolve;
	}
	
	public Config getGenerating(Config key) {
		return key.vGen;
	}
	
}
