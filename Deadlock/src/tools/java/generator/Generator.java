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
		
		Config c = new Config(par, boardIni);
		c = c.redCarWinningConfig();
//		CarInfo info = c.carMapGet((byte)'R');
		int side = par.side(par.exit);
		int[] unavailable = new int[2];
		switch (side) {
		case 0:
			unavailable[0] = 1;
			unavailable[1] = par.exit[1];
			break;
		case 1:
			unavailable[0] = par.exit[0];
			unavailable[1] = par.colCount-2;
			break;
		case 2:
			unavailable[0] = par.rowCount-2;
			unavailable[1] = par.exit[1];
			break;
		case 3:
			unavailable[0] = par.exit[0];
			unavailable[1] = 1;
			break;
		}
		
		List<Config> placements0 = c.possible3CarPlacements(unavailable);
		for (int i = 0; i < placements0.size(); i++) {
			Config d = placements0.get(i);
			
			List<Config> placements1 = d.possible3CarPlacements(unavailable);
			for (int j = 0; j < placements1.size(); j++) {
				Config e = placements1.get(j);
				
//				winners.add(e);
				List<Config> possible3CarPlacements3 = e.possible3CarPlacements(unavailable);
				for (int k = 0; k < possible3CarPlacements3.size(); k++) {
					Config f = possible3CarPlacements3.get(k);
					
//					winners.add(f);
					List<Config> possible3CarPlacements4 = f.possible3CarPlacements(unavailable);
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
			explored.put(w, null);
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
		Config hardest = null;
		/*
		 * start at end of iterations and work backwards, looking for first non-BS config
		 */
		for (int ll = explored.allIterations.size()-1; ll >= 0; ll--) {
			Config l = explored.allIterations.get(ll);
			
			Config m = l;
			int dist = 0;
			while (true) {
				if (m == null) {
					break;
				}
				m = explored.get(m);
				dist++;
			}
			
			List<Config> solution = solve(l);
			
			if (dist == solution.size()) {
				
				hardest = l;
				break;
				
			} else {
				System.out.print("!");
			}
			
		}
		System.out.print("done");
		System.out.println("");
		
		System.out.println("hardest config:");
		System.out.println(hardest);
		System.out.println();
		Config l = hardest;
		while (true) {
			if (l == null) {
				break;
			}
			l = explored.get(l);
			System.out.println(l);
			System.out.println();
		}
		
		System.out.println("total time: " + (System.currentTimeMillis() - total) + " millis");
	}
	
	static public void explorePreviousMoves(Config c) {
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			if (!explored.allIterations.contains(m)) {
				explored.put(m, c);
			}
		}
	}
	
	static List<Config> solve(Config start) {
		
		StateSpace space = new StateSpace();
		space.put(start, null);
		
		Config winner = null;
		loop:
		while (true) {
			
			List<Config> a = new ArrayList<Config>(space.lastIteration);
			space.lastIteration.clear();
			
			for (int i = 0; i < a.size(); i++) {
				Config b = a.get(i);
				
				List<Config> moves = b.possibleNextMoves();
				
				for (Config m : moves) {
					if (!space.allIterations.contains(m)) {
						space.put(m, b);
						
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
			l = space.get(l);
			if (l == null) {
				break;
			}
			solution.add(0, l);
		}
		
		return solution;
	}

}
