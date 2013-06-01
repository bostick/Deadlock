package com.brentonbostick.bypass.solver;

import java.util.ArrayList;
import java.util.List;


public class Solver {
	
	public static List<String> solve(Board start) {
		
		SolvingStateSpace space = new SolvingStateSpace();
		space.putSolving(start, null);
		
		Board winner = null;
		loop:
		while (true) {
			
			List<Board> a = new ArrayList<Board>(space.lastSolvingIteration);
			space.lastSolvingIteration = new ArrayList<Board>();
			
			if (a.isEmpty()) {
				throw new IllegalArgumentException("ran out of boards");
			}
			
			for (int i = 0; i < a.size(); i++) {
				Board b = a.get(i);
				
				if (b.isWinning()) {
					winner = b;
					break loop;
				}
				
				List<Board> moves = b.possibleNextMoves();
				
				for (Board m : moves) {
					if (!space.allSolvingConfigsContains(m)) {
						space.putSolving(m.clone(), b);
					}
				}
				
			}
		}
		
		List<String> solution = new ArrayList<String>();
		
		String l = winner.toString();
		solution.add(l);
		while (true) {
			l = space.getSolving(l);
			if (l == null) {
				break;
			}
			solution.add(0, l);
		}
		
		return solution;
	}
	
}
