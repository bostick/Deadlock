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
//	Map<Config, Config> generatingMap = new HashMap<Config, Config>();
	private ConfigTrie allGeneratingConfigs = new ConfigTrie();
	
	public List<Config> lastSolvingIteration = new ArrayList<Config>();
	private Set<Config> allSolvingConfigs = new HashSet<Config>();
	Map<Config, Config> solvingMap = new HashMap<Config, Config>();
	
	public void putGenerating(Config key) {
		
		lastGeneratingIteration.add(key);
		
		allGeneratingConfigs.add(key);
		
//		generatingMap.put(key, val);
		
		lastGeneratingConfig = key;
		
	}
	
	public void putSolving(Config key, Config val) {
		lastSolvingIteration.add(key);
		allSolvingConfigs.add(key);
		
		solvingMap.put(key, val);
	}
	
	public boolean allGeneratingConfigsContains(Config k) {
		return k.isWinning() || allGeneratingConfigs.contains(k);
	}
	
	public boolean allSolvingConfigsContains(Config k) {
		return allSolvingConfigs.contains(k);
	}
	
	public Config getSolving(Config key) {
		return solvingMap.get(key);
	}
	
	public int allGeneratingConfigsSize() {
		return allGeneratingConfigs.size();
	}
	
}
