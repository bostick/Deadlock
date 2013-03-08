package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;
import solver.ParentConfig;

public class Generator {
	
	public static void main(String[] args) throws Exception {
		
		generate();
//		winningConfigCounter();
	}
	
	static byte[][] boardIni = new byte[][] {
		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', 'Y'},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
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
		par.carMapPresent((byte)'E');
//		par.carMapPresent((byte)'F');
		
		Config red = par.newConfig();
		red = red.redCarWinningConfig();
		
		List<Config> placementsA = new ArrayList<Config>();
		List<Config> placementsB = new ArrayList<Config>();
		List<Config> placementsC = new ArrayList<Config>();
		List<Config> placementsD = new ArrayList<Config>();
		List<Config> placementsE = new ArrayList<Config>();
//		List<Config> placementsF = new ArrayList<Config>();
		
		placementsA.clear();
		red.possible2CarPlacements(placementsA);
		for (Config a : placementsA) {
			
//			winners.add(d);
			placementsB.clear();
			a.possible2CarPlacements(placementsB);
			for (Config b : placementsB) {
				
//				winners.add(e);
				placementsC.clear();
				b.possible2CarPlacements(placementsC);
				for (Config c : placementsC) {
					
//					winners.add(f);
					placementsD.clear();
					c.possible2CarPlacements(placementsD);
					for (Config d : placementsD) {
						
//						winners.add(d);
						placementsE.clear();
						d.possible2CarPlacements(placementsE);
						for (Config e : placementsE) {
							
							winners.add(e);
//							placementsF.clear();
//							e.possible2CarPlacements(placementsF);
//							for (Config f : placementsF) {
//								
//								winners.add(f);
//							}
							
						}
						
					}
				}
				
			}
		}
		System.out.println("size: " + winners.size() + "");
		
		for (Config w : winners) {
			explored.putGenerating(w, null);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis, ");
		System.out.print(100 * (((float)(Config.configCounter - 0)) / ((float)(winners.size()))) + "% of explored were unique and added");
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
			System.out.print(100 * (((float)(Config.configCounter - configs)) / ((float)(explored.lastIterationMoves))) + "% of explored were unique and added");
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
		int ll = 0;
		for (Config l = explored.lastConfig; l != null; l=l.previousGeneratingConfig) {
			
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
				
				if (ll % 1000 == 0) {
					System.out.print("!");
				}
				
				/*
				 * l is BS
				 */
//				System.out.println("!");
//				Config bs = l;
//				while (true) {
//					if (bs == null) {
//						break;
//					}
//					System.out.print(distToGeneratedWinner(bs) + " ");
//					bs = explored.generatingMap.get(bs);
//				}
//				
//				System.out.println();
//				
//				for (Config s : solution) {
//					System.out.print(distToGeneratedWinner(s) + " ");
//				}
//				
//				String.class.getName();
				
			}
			
			ll++;
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
	
	static int distToGeneratedWinner(Config c) {
		
		Config d = c;
		int dist = 0;
		while (true) {
			if (d.generatingVal == null) {
				if (d.isWinning()) {
					assert explored.allIterations.contains(d);
					return dist;
				} else {
					assert !explored.allIterations.contains(d);
					assert false;
					return -1;
				}
			} else {
				d = d.generatingVal;
				dist++;
			}
		}
		
	}
	
	static public void explorePreviousMoves(Config c) {
		
//		if (c.bs) {
//			return;
//		}
		
		List<Config> moves = c.possiblePreviousMoves();
		
		explored.lastIterationMoves += moves.size();
		
		for (Config m : moves) {
			if (!explored.allIterationsContains(m)) {
				explored.putGenerating(m.copy(), c);
			} else {
				
//				c.vGen
				
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
