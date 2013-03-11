package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
	public Config lastGeneratingConfig;
	
	public List<Config> lastGeneratingIteration = new ArrayList<Config>();
	
	private Map<Config, Integer> allGeneratingConfigs = new HashMap<Config, Integer>();
	
	
	
	public List<Config> lastSolvingIteration = new ArrayList<Config>();
	private Set<Config> allSolvingConfigs = new HashSet<Config>();
	
	
	
	
	
	public void putGenerating(Config key, Config val, Integer dist) {
		
		lastGeneratingIteration.add(key);
		
		allGeneratingConfigs.put(key, dist);
		
		key.generatingVal = val;
		
		lastGeneratingConfig = key;
		
	}
	
	public void putSolving(Config key, Config val) {
		
		lastSolvingIteration.add(key);
		
//		allSolvingConfigs.put(key, );
		allSolvingConfigs.add(key);
		
//		solvingMap.put(key, val);
		key.solvingVal = val;
		
	}
	
	public boolean allGeneratingConfigsContains(Config k) {
		return k.isWinning() || allGeneratingConfigs.keySet().contains(k);
	}
	
	public boolean allSolvingConfigsContains(Config k) {
		return allSolvingConfigs.contains(k);
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
	
	public int allGeneratingConfigsSize() {
		return allGeneratingConfigs.size();
	}
	
	public void clearAllGeneratingConfigs() {
		allGeneratingConfigs.clear();
	}
	
	public int allGeneratingConfigsGet(Config c) {
		return (c.isWinning() ? 0 : allGeneratingConfigs.get(c));
	}
	
	public void allGeneratingConfigsRemove(Config key) {
		allGeneratingConfigs.remove(key);
	}
	
}
