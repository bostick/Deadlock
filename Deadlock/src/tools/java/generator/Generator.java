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
//		winningConfigCounter();
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
		
//		Thread.sleep(20000);
		
		long total = System.currentTimeMillis();
		long t = total;
		long configs;
		System.out.print("winning base cases... ");
		System.out.println("");
		List<Config> winners = new ArrayList<Config>();
		
		par = new ParentConfig(boardIni);
		par.carMapPresent((byte)'R');
		par.carMapPresent((byte)'A');
		par.carMapPresent((byte)'B');
		par.carMapPresent((byte)'C');
		par.carMapPresent((byte)'D');
//		par.carMapPresent((byte)'E');
		
		Config c = par.newConfig();
		c = c.redCarWinningConfig();
		
		List<Config> placements0 = new ArrayList<Config>();
		List<Config> placements1 = new ArrayList<Config>();
		List<Config> placements2 = new ArrayList<Config>();
		List<Config> placements3 = new ArrayList<Config>();
//		List<Config> placements4 = new ArrayList<Config>();
		
		placements0.clear();
		c.possible3CarPlacements(placements0);
		for (int i = 0; i < placements0.size(); i++) {
			Config d = placements0.get(i);
			
//			winners.add(d);
			placements1.clear();
			d.possible3CarPlacements(placements1);
			for (int j = 0; j < placements1.size(); j++) {
				Config e = placements1.get(j);
				
//				winners.add(e);
				placements2.clear();
				e.possible2CarPlacements(placements2);
				for (int k = 0; k < placements2.size(); k++) {
					Config f = placements2.get(k);
					
//					winners.add(f);
					placements3.clear();
					f.possible3CarPlacements(placements3);
					for (int l = 0; l < placements3.size(); l++) {
						Config g = placements3.get(l);
						
						winners.add(g);
//						placements4.clear();
//						g.possible3CarPlacements(placements4);
//						for (int m = 0; m < placements4.size(); m++) {
//							Config h = placements4.get(m);
//							
//							winners.add(h);
//						}
						
					}
				}
				
			}
		}
		System.out.println("size: " + winners.size() + "");
		
		for (Config w : winners) {
			explored.putGenerating(w, null);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis, ");
		System.out.print(" " + (Config.configCounter) + " configs");
		System.out.println("");
		
		List<Config> a = new ArrayList<Config>();
		while (true) {
			t = System.currentTimeMillis();
			configs = Config.configCounter;
			
			System.out.print("exploring... ");
			a.addAll(explored.lastIteration);
			explored.lastIteration.clear();
			explored.lastIterationMoves = 0;
			
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 1000 == 0) {
					System.out.print(".");
				}
				Config b = a.get(i);
				explorePreviousMoves(b);
			}
			a.clear();
			
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis, ");
			System.out.print(100 * (((float)(Config.configCounter - configs)) / ((float)(explored.lastIterationMoves))) + "% of explored were unique");
			System.out.println("");
			if (explored.lastIteration.isEmpty()) {
				break;
			}
		}
		System.out.println("reached fixpoint");
		
		System.out.print("finding longest non-BS path... ");
		System.out.print("(" + explored.set.size() + ") ");
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
		
		System.out.println("hardest config is " + solution.size() + " moves\n");
		for (Config s : solution) {
			System.out.println(s);
			System.out.println();
		}
		
		System.out.println("total time: " + (System.currentTimeMillis() - total) + " millis");
	}
	
//	public static void winningConfigCounter() throws Exception {
//		
//		System.out.print("winning base cases... ");
//		
//		par = new ParentConfig(boardIni);
//		par.carMapPresent((byte)'R');
//		par.carMapPresent((byte)'A');
//		par.carMapPresent((byte)'B');
//		par.carMapPresent((byte)'C');
//		par.carMapPresent((byte)'D');
//		par.carMapPresent((byte)'E');
//		
//		byte[][] board = Config.newBoard(boardIni.length-2, boardIni[0].length-2);
//		for (int i = 1; i < boardIni.length-1; i++) {
//			for (int j = 1; j < boardIni[0].length-1; j++) {
//				Config.boardSet(board, i-1, j-1, boardIni[i][j]);
//			}
//		}
//		Config c = new Config(par, board);
//		c = c.redCarWinningConfig();
//		
//		int winners = 0;
//		
//		List<Config> placements0 = c.possible3CarPlacements(Integer.MAX_VALUE);
//		for (int i = 0; i < placements0.size(); i++) {
//			Config d = placements0.get(i);
//			
//			List<Config> placements1 = d.possible3CarPlacements(Integer.MAX_VALUE);
//			for (int j = 0; j < placements1.size(); j++) {
//				Config e = placements1.get(j);
//				
////				winners.add(e);
//				List<Config> possible3CarPlacements3 = e.possible3CarPlacements(Integer.MAX_VALUE);
//				for (int k = 0; k < possible3CarPlacements3.size(); k++) {
//					Config f = possible3CarPlacements3.get(k);
//					
////					winners.add(f);
//					List<Config> possible3CarPlacements4 = f.possible3CarPlacements(Integer.MAX_VALUE);
//					for (int l = 0; l < possible3CarPlacements4.size(); l++) {
//						Config g = possible3CarPlacements4.get(l);
//						
////						winners.add(g); 
//						
//						winners += g.possible3CarPlacementsCount(Integer.MAX_VALUE);
//						
//					}
//				}
//				
//			}
//		}
//		
//		System.out.print("(" + winners + ")");
//	}
	
	static public void explorePreviousMoves(Config c) {
		
		List<Config> moves = c.possiblePreviousMoves();
		
		explored.lastIterationMoves += moves.size();
		
		for (Config m : moves) {
			if (!explored.allIterationsContains(m)) {
				explored.putGenerating(m.copy(), c);
			} else {
//				System.out.print('@');
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
				
				if (b.isWinning()) {
					winner = b;
					break loop;
				}
				
				List<Config> moves = b.possibleNextMoves();
				
				for (Config m : moves) {
					if (!space.allIterationsContains(m)) {
						space.putSolving(m.copy(), b);
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
