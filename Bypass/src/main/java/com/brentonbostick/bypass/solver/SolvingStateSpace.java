package com.brentonbostick.bypass.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolvingStateSpace {
	
	public List<Board> lastSolvingIteration = new ArrayList<Board>();
	public Map<Board, Board> solvingMap = new HashMap<Board, Board>();
	
	public void putSolving(Board key, Board val) {
		
		Board k = key.clone();
		
		lastSolvingIteration.add(k);
		
		if (val == null) {
			solvingMap.put(k, null);
		} else {
			solvingMap.put(k, val.clone());
		}
	}
	
	public boolean allSolvingConfigsContains(Board k) {
		return solvingMap.containsKey(k);
	}
	
	public Board getSolving(Board key) {
		return solvingMap.get(key);
	}
	
}
