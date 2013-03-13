package generator;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solver.Config;

public class StateSpace {
	
	public byte[] lastGeneratingConfig;
//	public List<byte[]> lastGeneratingIteration = new ArrayList<byte[]>();
	public TLongArrayList lastGeneratingIteration = new TLongArrayList(); 
//	Map<Config, Config> generatingMap = new HashMap<Config, Config>();
	private ConfigTrie allGeneratingConfigs = new ConfigTrie();
	
	public List<byte[]> lastSolvingIteration = new ArrayList<byte[]>();
//	private Set<String> allSolvingConfigs = new HashSet<String>();
	Map<String, String> solvingMap = new HashMap<String, String>();
	
	long min;
	long max;
	
	public void putGenerating(byte[] key) {
		
		long info = Config.getInfoLong(key);
		
		if (info < min) {
			min = info;
		}
		if (info > max) {
			max = info;
		}
		
//		assert (info & 0xff00000000000000L) != 0;
		
		lastGeneratingIteration.add(info);
		
		allGeneratingConfigs.add(info);
		
//		generatingMap.put(key, val);
		
		lastGeneratingConfig = key;
		
	}
	
	public void putSolving(byte[] key, byte[] val) {
		
		lastSolvingIteration.add(key);
		
//		allSolvingConfigs.add(Config.toString(key));
		
		if (val == null) {
			solvingMap.put(Config.toString(key), null);
		} else {
			solvingMap.put(Config.toString(key), Config.toString(val));
		}
	}
	
	public boolean allGeneratingConfigsContains(byte[] k) {
		return Config.isWinning(k) || allGeneratingConfigs.contains(Config.getInfoLong(k));
	}
	
	public boolean allSolvingConfigsContains(byte[] k) {
		return solvingMap.containsKey(Config.toString(k));
	}
	
	public String getSolving(String key) {
		return solvingMap.get(key);
	}
	
	public int allGeneratingConfigsSize() {
		return allGeneratingConfigs.size();
	}
	
//	static class BoardComparator implements Comparator {
//		
//	}
	
}
