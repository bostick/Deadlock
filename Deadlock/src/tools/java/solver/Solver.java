package solver;

import java.util.ArrayList;
import java.util.List;

public class Solver {
	
	static List<String> solve(byte[][] start) {
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null);
		
		byte[][] winner = null;
		loop:
		while (true) {
			
			List<byte[][]> a = new ArrayList<byte[][]>(space.lastSolvingIteration);
			space.lastSolvingIteration = new ArrayList<byte[][]>();
			
//			System.out.println(Config.toString(a));
			
			if (a.isEmpty()) {
				throw new IllegalArgumentException("ran out of boards");
			}
			
			for (int i = 0; i < a.size(); i++) {
				byte[][] b = a.get(i);
				
				if (Config.isWinning(b)) {
					winner = b;
					break loop;
				}
				
				List<byte[][]> moves = Config.possibleNextMoves(b);
				
				for (byte[][] m : moves) {
					if (!space.allSolvingConfigsContains(m)) {
						space.putSolving(Config.clone(m), b);
					}
				}
				
			}
		}
		
		List<String> solution = new ArrayList<String>();
		
		String l = Config.toString(winner);
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
