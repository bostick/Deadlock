package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;
import solver.ParentConfig;

public class Generator {
	
	static byte[][] testIni = new byte[][] {
		{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
		{' ', 'A', 'X', 'X', 'X', 'X', 'B', ' '},
		{' ', 'A', 'X', 'X', 'X', 'X', 'B', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'R', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'R', ' '},
		{' ', 'X', 'X', 'C', 'X', 'X', 'X', ' '},
		{'K', 'X', 'X', 'C', 'X', 'X', 'X', 'J'},
		{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '},
	};
	
	public static void main(String[] args) throws Exception {
		
		generate();
	}
	
	static byte[][] boardIni = new byte[][] {
		{' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{'K', 'X', 'X', 'X', 'X', 'X', 'X', 'J'},
		{' ', ' ', ' ', ' ', 'K', ' ', 'J', ' '},
	};
	static ParentConfig par;
	static StateSpace explored = new StateSpace();
	
	public static void generate() throws Exception {
		
		long total = System.currentTimeMillis();
		long t = total;
		System.out.print("winning base cases... ");
		List<Config> winners = new ArrayList<Config>();
		
		par = new ParentConfig(boardIni);
		par.carMapPresent((byte)'R');
		par.carMapPresent((byte)'A');
		par.carMapPresent((byte)'B');
		par.carMapPresent((byte)'C');
		par.carMapPresent((byte)'D');
		
		byte[][] board = Config.newBoard(boardIni.length-2, boardIni[0].length-2);
		for (int i = 1; i < boardIni.length-1; i++) {
			for (int j = 1; j < boardIni[0].length-1; j++) {
				Config.boardSet(board, i-1, j-1, boardIni[i][j]);
			}
		}
		Config c = new Config(par, board);
		c = c.redCarWinningConfig();
		
		List<Config> placements0 = c.possible3CarPlacements();
		for (int i = 0; i < placements0.size(); i++) {
			Config d = placements0.get(i);
			
			List<Config> placements1 = d.possible3CarPlacements();
			for (int j = 0; j < placements1.size(); j++) {
				Config e = placements1.get(j);
				
//				winners.add(e);
				List<Config> possible3CarPlacements3 = e.possible3CarPlacements();
				for (int k = 0; k < possible3CarPlacements3.size(); k++) {
					Config f = possible3CarPlacements3.get(k);
					
//					winners.add(f);
					List<Config> possible3CarPlacements4 = f.possible3CarPlacements();
					for (int l = 0; l < possible3CarPlacements4.size(); l++) {
						Config g = possible3CarPlacements4.get(l);
						
						winners.add(g);
//						List<Config> possible3CarPlacements5 = g.possible3CarPlacements();
//						for (int m = 0; m < possible3CarPlacements5.size(); m++) {
//							Config h = possible3CarPlacements5.get(m);
//							
//							winners.add(h);
//						}
						
					}
				}
				
			}
		}
		System.out.print("(" + winners.size() + ")");
		
		for (int i = 0; i < winners.size(); i++) {
			if (i % 100 == 0) {
				System.out.print(".");
			}
			Config w = winners.get(i);
			explored.putGenerating(w, null);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println("");
		
		while (true) {
			t = System.currentTimeMillis();
			
			System.out.print("exploring... ");
			List<Config> a = new ArrayList<Config>(explored.lastIteration);
			explored.lastIteration.clear();
			
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 100 == 0) {
					System.out.print(".");
				}
				Config b = a.get(i);
				explorePreviousMoves(b);
			}
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis ");
			System.out.println("");
			if (explored.lastIteration.isEmpty()) {
				break;
			}
		}
		System.out.println("reached fixpoint");
		
		System.out.print("finding longest non-BS path... ");
		System.out.print("(" + explored.allIterations.size() + ") ");
//		Config hardest = null;
		/*
		 * start at end of iterations and work backwards, looking for first non-BS config
		 */
		List<Config> solution = null;
		for (int ll = explored.allIterations.size()-1; ll >= 0; ll--) {
			Config l = explored.allIterations.get(ll);
			
			Config m = l;
			int dist = 0;
			while (true) {
				if (m == null) {
					break;
				}
				m = explored.getGenerating(m);
				dist++;
			}
			
			solution = solve(l);
			
			if (dist == solution.size()) {
				
//				hardest = l;
				break;
				
			} else {
				System.out.print("!");
			}
			
		}
		System.out.print("done");
		System.out.println("");
		
		System.out.println("hardest config:");
		for (Config s : solution) {
			System.out.println(s);
			System.out.println();
		}
		
		System.out.println("total time: " + (System.currentTimeMillis() - total) + " millis");
	}
	
	static public void explorePreviousMoves(Config c) {
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			if (!explored.allIterationsContains(m)) {
				explored.putGenerating(m, c);
			}
		}
	}
	
	static List<Config> solve(Config start) {
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null);
		
		Config winner = null;
		loop:
		while (true) {
			
			List<Config> a = new ArrayList<Config>(space.lastIteration);
			space.lastIteration.clear();
			
			for (int i = 0; i < a.size(); i++) {
				Config b = a.get(i);
				
				List<Config> moves = b.possibleNextMoves();
				
				for (Config m : moves) {
					if (!space.allIterationsContains(m)) {
						space.putSolving(m, b);
						
						if (m.isWinning()) {
							winner = m;
							break loop;
						}
						
					}
				}
				
			}
		}
		
		List<Config> solution = new ArrayList<Config>();
		
		Config l = winner;
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
