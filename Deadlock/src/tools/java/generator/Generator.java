package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;
import solver.ParentConfig;

public class Generator {
	
	public static void main(String[] args) throws Exception {
		
		generate();

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
	static StateSpace explored = new StateSpace();
	
	public static void generate() throws Exception {
		
		long total = System.currentTimeMillis();
		long t = total;
		System.out.print("winning base cases... ");
		System.out.println("");
		List<Config> winners = new ArrayList<Config>();
		
		Config.par = new ParentConfig(boardIni);
		Config.par.addCar((byte)'R');
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		Config red = new Config(Config.par.emptyBoard);
		red = red.winningConfig();
		
		List<Config> placementsA = new ArrayList<Config>();
		List<Config> placementsB = new ArrayList<Config>();
		List<Config> placementsC = new ArrayList<Config>();
		List<Config> placementsD = new ArrayList<Config>();
		List<Config> placementsE = new ArrayList<Config>();
//		List<Config> placementsF = new ArrayList<Config>();
//		List<Config> placementsG = new ArrayList<Config>();
		
		placementsA.clear();
		red.possible2CarPlacements(placementsA, false);
		for (Config a : placementsA) {
			
//			winners.add(d);
			placementsB.clear();
			a.possible2CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
//				winners.add(e);
				placementsC.clear();
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
//					winners.add(f);
					placementsD.clear();
					c.possible2CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
//						winners.add(d);
						placementsE.clear();
						d.possible2CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
//							placementsF.clear();
//							e.possible2CarPlacements(placementsF, true);
//							for (Config f : placementsF) {
//								
//								winners.add(f);
////								placementsG.clear();
////								f.possible2CarPlacements(placementsG, true);
////								for (Config g : placementsG) {
////									
////									winners.add(g);
////								}
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
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println("");
		
		List<Config> a = new ArrayList<Config>();
		while (true) {
			t = System.currentTimeMillis();
			
			System.out.print("exploring... ");
			a.addAll(explored.lastIteration);
			explored.lastIteration.clear();
			
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 1000 == 0) {
					System.out.print(".");
				}
				Config b = a.get(i);
				explorePreviousMoves(b);
			}
			a.clear();
			
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
			System.out.println("");
			if (explored.lastIteration.isEmpty()) {
				break;
			}
		}
		System.out.println("reached fixpoint");
		
		System.out.print("finding longest non-BS path... ");
		System.out.print("(" + explored.allconfigs.size() + ") ");
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
				
				break;
				
			} else {
				
				if (ll % 1000 == 0) {
					System.out.print("!");
				}
				
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
					assert explored.allconfigs.contains(d);
					return dist;
				} else {
					assert !explored.allconfigs.contains(d);
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
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			if (!explored.allConfigsContains(m)) {
				explored.putGenerating(m.clone(), c);
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
					if (!space.allConfigsContains(m)) {
						space.putSolving(m.clone(), b);
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
