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
	
	static List<Config> winners = new ArrayList<Config>();
	
	public static void generate() throws Exception {
		
		long total = System.currentTimeMillis();
		long t = total;
		System.out.print("winning base cases... ");
		System.out.println("");
		
		Config.par = new ParentConfig(boardIni);
		Config.par.addCar((byte)'R');
		
		Config red = new Config(Config.par.emptyBoard);
		red = red.winningConfig();
		
//		add3and1(red);
		add4and1(red);
		
		System.out.println("size: " + winners.size() + "");
		
		for (Config w : winners) {
			explored.putGenerating(w, null, 0);
		}
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println("");
		
		List<Config> a = new ArrayList<Config>();
		int iteration = 0;
		while (true) {
			t = System.currentTimeMillis();
			
			System.out.print("exploring... ");
			a.addAll(explored.lastGeneratingIteration);
			explored.lastGeneratingIteration.clear();
			
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
			
			if (iteration == 0) {
				/*
				 * after the winners have been explored, they can be removed from collection, and a simple isWinning() check can be done instead
				 */
				
				for (Config w : winners) {
					explored.allGeneratingConfigsRemove(w);
				}
				
				winners = null;
				System.gc();
			}
			
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
			System.out.println("");
			if (explored.lastGeneratingIteration.isEmpty()) {
				break;
			}
			
			iteration++;
		}
		System.out.println("reached fixpoint");
		
//		System.out.print("finding longest non-BS path... ");
//		System.out.print("(" + explored.allGeneratingConfigsSize() + ") ");
		
		Config longest = explored.lastGeneratingConfig;
		
		explored = null;
		System.gc();
		
		/*
		 * start at end of iterations and work backwards, looking for first non-BS config
		 */
		
//		Config m = longest;
//		int dist = 0;
//		while (true) {
//			if (m == null) {
//				break;
//			}
//			m = explored.getGenerating(m);
//			dist++;
//		}
		
		List<Config> solution = solve(longest);
		
//		if (dist == solution.size()) {
//			
//			
//			
//		} else {
//			assert solution.size() < dist;
//			
//			m = longest;
//			System.out.println(m);
//			while (true) {
//				if (m == null) {
//					break;
//				}
//				m = explored.getGenerating(m);
//				System.out.println(m);
//			}
//			
//			assert false;
//		}
		
		System.out.println("hardest config is " + solution.size() + " moves\n");
		for (Config s : solution) {
			System.out.println(s);
			System.out.println();
		}
		
		System.out.println("total time: " + (System.currentTimeMillis() - total) + " millis");
	}
	
//	static int distToGeneratedWinner(Config c) {
//		
//		Config d = c;
//		int dist = 0;
//		while (true) {
//			if (d.generatingVal == null) {
//				if (d.isWinning()) {
//					assert explored.allGeneratingConfigsContains(d);
//					return dist;
//				} else {
//					assert !explored.allGeneratingConfigsContains(d);
//					assert false;
//					return -1;
//				}
//			} else {
//				d = d.generatingVal;
//				dist++;
//			}
//		}
//		
//	}
	
	static public void explorePreviousMoves(Config c) {
		
		assert explored.allGeneratingConfigsContains(c);
		int cDist = explored.allGeneratingConfigsGet(c);
		
		List<Config> moves = c.possiblePreviousMoves();
		
		for (Config m : moves) {
			if (!explored.allGeneratingConfigsContains(m)) {
				explored.putGenerating(m.clone(), c, cDist + 1);
			} else {
				
				int mDist = explored.allGeneratingConfigsGet(m);
				if (mDist < cDist + 1) {
					/*
					 * move is the same or actually closer
					 * so cDist may be wrong
					 */
//					List<Config> cSolution = solve(c);
//					assert cDist == cSolution.size()-1; 
				}
			}
		}
	}
	
	static List<Config> solve(Config start) {
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null);
		
		Config winner = null;
		loop:
		while (true) {
			
			List<Config> a = new ArrayList<Config>(space.lastSolvingIteration);
			space.lastSolvingIteration.clear();
			
			for (int i = 0; i < a.size(); i++) {
				Config b = a.get(i);
				
				if (b.isWinning()) {
					winner = b;
					break loop;
				}
				
				List<Config> moves = b.possibleNextMoves();
				
				for (Config m : moves) {
					if (!space.allSolvingConfigsContains(m)) {
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
	
	public static void add4and1(Config red) {
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
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
			
			placementsB.clear();
			a.possible2CarPlacements(placementsB, false);
			for (Config b : placementsB) {
				
				placementsC.clear();
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible2CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
						placementsE.clear();
						d.possible3CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
							
						}
						
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
				b.possible2CarPlacements(placementsC, false);
				for (Config c : placementsC) {
					
					placementsD.clear();
					c.possible3CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
						placementsE.clear();
						d.possible2CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
							
						}
						
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
					c.possible2CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
						placementsE.clear();
						d.possible2CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
							
						}
						
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
					c.possible2CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
						placementsE.clear();
						d.possible2CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
							
						}
						
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
					c.possible2CarPlacements(placementsD, false);
					for (Config d : placementsD) {
						
						placementsE.clear();
						d.possible2CarPlacements(placementsE, true);
						for (Config e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		
	}
	
	
	
	public static void add3and1(Config red) {
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
//		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
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
		
		
	}

}
