package generator;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solver.Config;

public class StateSpace {
	
	public long lastGeneratingConfig;
	public TLongArrayList lastGeneratingIteration = new TLongArrayList();
	private TLongHashSet allGeneratingConfigs = new TLongHashSet();
	
	public List<byte[][]> lastSolvingIteration = new ArrayList<byte[][]>();
	Map<String, String> solvingMap = new HashMap<String, String>();
	
	public void tryPutGenerating(byte[][] key) {
		
		long info = Config.getInfoLong(key);
		
		boolean modified = allGeneratingConfigs.add(info);
		if (modified) {
			lastGeneratingIteration.add(info);
			
			lastGeneratingConfig = info;
		}
		
	}
	
	public void putSolving(byte[][] key, byte[][] val) {
		
		lastSolvingIteration.add(key);
		
//		allSolvingConfigs.add(Config.toString(key));
		
		if (val == null) {
			solvingMap.put(Config.toString(key), null);
		} else {
			solvingMap.put(Config.toString(key), Config.toString(val));
		}
	}
	
	public boolean allGeneratingConfigsContains(byte[][] k) {
		return Config.isWinning(k) || allGeneratingConfigs.contains(Config.getInfoLong(k));
	}
	
	public boolean allSolvingConfigsContains(byte[][] k) {
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
