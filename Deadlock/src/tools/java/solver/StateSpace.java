package solver;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StateSpace {
	
	public long lastGeneratingConfig = -1;
	
	public TLongArrayList lastGeneratingIteration = new TLongArrayList();
	public TLongArrayList penGeneratingIteration = new TLongArrayList();
	
	private TLongHashSet allGeneratingConfigs = new TLongHashSet();
	
	public List<Board> lastSolvingIteration = new ArrayList<Board>();
	Map<String, String> solvingMap = new HashMap<String, String>();
	
	public void tryPutGenerating(Board key) {
		
		long info = key.getInfoLong();
		assert info != 0;
		
		boolean modified = allGeneratingConfigs.add(info);
		if (modified) {
			lastGeneratingIteration.add(info);
			
			lastGeneratingConfig = info;
		}
		
	}
	
	public void putSolving(Board key, Board val) {
		
		lastSolvingIteration.add(key);
		
//		allSolvingConfigs.add(Config.toString(key));
		
		if (val == null) {
			solvingMap.put(key.toString(), null);
		} else {
			solvingMap.put(key.toString(), val.toString());
		}
	}
	
	public boolean allGeneratingConfigsContains(Board k) {
		return k.isWinning() || allGeneratingConfigs.contains(k.getInfoLong());
	}
	
	public boolean allSolvingConfigsContains(Board k) {
		return solvingMap.containsKey(k.toString());
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
