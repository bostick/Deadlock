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
		{'J', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', 'X', 'X', 'X', 'X', 'X', 'X', ' '},
		{' ', ' ', 'J', ' ', ' ', ' ', ' ', ' '},
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
//		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		Config red = new Config(Config.par.emptyBoard);
		red = red.winningConfig();
		
		List<Config> placementsA = new ArrayList<Config>();
		List<Config> placementsB = new ArrayList<Config>();
		List<Config> placementsC = new ArrayList<Config>();
		List<Config> placementsD = new ArrayList<Config>();
//		List<Config> placementsE = new ArrayList<Config>();
//		List<Config> placementsF = new ArrayList<Config>();
//		List<Config> placementsG = new ArrayList<Config>();
		
		placementsA.clear();
		red.possible2CarPlacements(placementsA, false);
		for (Config a : placementsA) {
			
			placementsB.clear();
			a.possible2CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
				placementsC.clear();
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible3CarPlacements(placementsD, true);
					for (Config d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		placementsA.clear();
		red.possible2CarPlacements(placementsA, false);
		for (Config a : placementsA) {
			
			placementsB.clear();
			a.possible2CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
				placementsC.clear();
				b.possible3CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible2CarPlacements(placementsD, true);
					for (Config d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		placementsA.clear();
		red.possible2CarPlacements(placementsA, false);
		for (Config a : placementsA) {
			
			placementsB.clear();
			a.possible3CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
				placementsC.clear();
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible2CarPlacements(placementsD, true);
					for (Config d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		placementsA.clear();
		red.possible3CarPlacements(placementsA, false);
		for (Config a : placementsA) {
			
			placementsB.clear();
			a.possible2CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
				placementsC.clear();
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible2CarPlacements(placementsD, true);
					for (Config d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		System.out.println("size: " + winners.size() + "");
		
		for (Config w : winners) {
			explored.putGenerating(w, null, 0);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println("");
		
		List<Config> a = new ArrayList<Config>();
//		int iteration = 0;
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
//				if (!b.bs) {
				explorePreviousMoves(b);
//				}
			}
			a.clear();
			
//			for (Config c : explored.lastIteration) {
//				
//				c.possibleNextMoves();
//				
//			}
			
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
			System.out.println("");
			if (explored.lastIteration.isEmpty()) {
				break;
			}
			
//			iteration++;
		}
		System.out.println("reached fixpoint");
		
		System.out.print("finding longest non-BS path... ");
		System.out.print("(" + explored.allConfigsSize() + ") ");
		
		explored.clearAllConfigs();
		
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
				assert solution.size() < dist;
				
				m = l;
				System.out.println(m);
				while (true) {
					if (m == null) {
						break;
					}
					m = explored.getGenerating(m);
					System.out.println(m);
				}
				
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
					assert explored.allConfigsContains(d);
					return dist;
				} else {
					assert !explored.allConfigsContains(d);
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
		
		assert explored.allConfigsContains(c);
		int cDist = explored.allConfigsGet(c);
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			if (!explored.allConfigsContains(m)) {
				explored.putGenerating(m.clone(), c, cDist + 1);
			} else {
				
				int mDist = explored.allConfigsGet(m);
				if (mDist < cDist + 1) {
					if (mDist == cDist) {
						/*
						 * some change that does not affect winning distance between the two configs
						 */
//						assert false;
					} else {
						/*
						 * move is actually closer
						 * so cDist may be wrong
						 */
//						List<Config> cSolution = solve(c);
//						assert cDist == cSolution.size()-1; 
					}
				} else if (mDist == cDist + 1) {
					// everything ok
//					assert false;
				} else {
//					never happens
					assert false;
				}
				
			}
		}
	}
	
	static List<Config> solve(Config start) {
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null, 0);
		
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
						space.putSolving(m.clone(), b, 0);
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
