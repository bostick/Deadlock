package generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import solver.Config;

public class StateSpace {
	
	public Config lastGeneratingConfig;
	public List<Config> lastGeneratingIteration = new ArrayList<Config>();
	private ConfigTrie allGeneratingConfigs = new ConfigTrie();
	
	public List<Config> lastSolvingIteration = new ArrayList<Config>();
	private Set<Config> allSolvingConfigs = new HashSet<Config>();
	
	public void putGenerating(Config key, Config val) {
		
		lastGeneratingIteration.add(key);
		
		allGeneratingConfigs.add(key);
		
		key.generatingVal = val;
		
		lastGeneratingConfig = key;
		
	}
	
	public void putSolving(Config key, Config val) {
		lastSolvingIteration.add(key);
		allSolvingConfigs.add(key);
		key.solvingVal = val;
	}
	
	public boolean allGeneratingConfigsContains(Config k) {
		return k.isWinning() || allGeneratingConfigs.contains(k);
	}
	
	public boolean allSolvingConfigsContains(Config k) {
		return allSolvingConfigs.contains(k);
	}
	
	public Config getSolving(Config key) {
		return key.solvingVal;
	}
	
	public int allGeneratingConfigsSize() {
		return allGeneratingConfigs.size();
	}
	
}
