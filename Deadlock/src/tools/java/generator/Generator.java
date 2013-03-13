package generator;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.List;

import solver.Config;
import solver.ParentConfig;

public class Generator {
	
	public static void main(String[] args) throws Exception {
		
		generate();

	}
	
	static byte[][] boardIni = new byte[][] {
		{ '/', '-', '-', '-', 'J', '-', '-', '\\'},
		{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
		{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
		{ 'J', ' ', ' ', ' ', ' ', ' ', ' ', 'Y'},
		{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
		{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
		{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
		{'\\', '-', '-', '-', '-', '-', '-', '/'},
	};
	static StateSpace explored = new StateSpace();
	
	static List<byte[][]> winners = new ArrayList<byte[][]>();
	
	static long total = System.currentTimeMillis();
	static long t = total;
	
	public static void generate() throws Exception {
		
		System.out.print("winning base cases... ");
		System.out.println("");
		
		Config.par = new ParentConfig(boardIni);
		Config.par.addCar((byte)'R');
		
		byte[][] red = Config.newConfig(Config.par.emptyBoard);
		red = Config.winningConfig(red);
		
		System.out.println("board:");
		System.out.println(Config.toString(red));
		
//		add2and1(red);
		add3and1(red);
//		add4and1(red);
//		add5and1(red);
//		add5and2(red);
		
		byte[][] temp = Config.newConfig(Config.par.emptyBoard);
		TLongArrayList a = new TLongArrayList();
		while (true) {
			t = System.currentTimeMillis();
			
			System.out.print("exploring... ");
			a.addAll(explored.lastGeneratingIteration);
			explored.lastGeneratingIteration = new TLongArrayList();
			
			System.out.print("(" + a.size() + ") ");
			for (int i = 0; i < a.size(); i++) {
				if (i % 1000 == 0) {
					System.out.print(".");
				}
				long info = a.get(i);
				Config.copyTo(Config.par.emptyBoard, temp);
				Config.toBoard(info, temp);
				explorePreviousMoves(temp);
			}
			a = new TLongArrayList();
			
			System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
			System.out.println("");
			System.out.print("explored size:  " + explored.allGeneratingConfigsSize());
			System.out.println("");
			if (explored.lastGeneratingIteration.isEmpty()) {
				break;
			}
			
		}
		System.out.println("reached fixpoint");
		
//		System.out.print("finding longest non-BS path... ");
//		System.out.println("explored has " + explored.allGeneratingConfigsSize() + " configs");
		
		long longest = explored.lastGeneratingConfig;
		
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
		
		List<String> solution = solve(longest);
		
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
		for (String s : solution) {
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
	
	static public void explorePreviousMoves(byte[][] c) {
		
		assert explored.allGeneratingConfigsContains(c);
//		int cDist = explored.allGeneratingConfigsGet(c);
		
		List<byte[][]> moves = Config.possiblePreviousMoves(c);
		
		for (byte[][] m : moves) {
			explored.tryPutGenerating(m);
//			else {
//				
////				int mDist = explored.allGeneratingConfigsGet(m);
////				if (mDist < cDist + 1) {
//					/*
//					 * move is the same or actually closer
//					 * so cDist may be wrong
//					 */
////					List<byte[][]> cSolution = solve(c);
////					assert cDist == cSolution.size()-1; 
////				}
//			}
		}
	}
	
	static List<String> solve(long info) {
		
		byte[][] start = Config.newConfig(Config.par.emptyBoard);
		Config.toBoard(info, start);
		
		StateSpace space = new StateSpace();
		space.putSolving(start, null);
		
		byte[][] winner = null;
		loop:
		while (true) {
			
			List<byte[][]> a = new ArrayList<byte[][]>(space.lastSolvingIteration);
			space.lastSolvingIteration = new ArrayList<byte[][]>();
			
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
	
	static void explorePartialWinners() {
		
//		for (byte[][] w : winners) {
//			explored.putGenerating(w, null, 0);
//		}
		
		List<byte[][]> a = new ArrayList<byte[][]>();
		
		t = System.currentTimeMillis();
		
		System.out.print("exploring partial... ");
		a.addAll(winners);
//		explored.lastGeneratingIteration.clear();
		
		System.out.print("(" + a.size() + ") ");
		for (int i = 0; i < a.size(); i++) {
			if (i % 1000 == 0) {
				System.out.print(".");
			}
			byte[][] b = a.get(i);
			explorePreviousMoves(b);
		}
		a = new ArrayList<byte[][]>();
		
		/*
		 * after the winners have been explored, they can be removed from collection, and a simple isWinning() check can be done instead
		 */
//		for (byte[][] w : winners) {
//			explored.allGeneratingConfigsRemove(w);
//		}
		
//		for (byte[][] w : winners) {
//			explored.putGenerating(w);
//		}
		
		winners = new ArrayList<byte[][]>();
//		explored.lastGeneratingIteration.clear();
		System.gc();
		
		System.out.print(" " + (System.currentTimeMillis() - t) + " millis");
		System.out.println("");
		System.out.print("explored size:  " + explored.allGeneratingConfigsSize());
		System.out.println("");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static void add2and1(byte[][] red) {
		
		System.out.println("exploring 2 2cars and 1 3car");
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
//		Config.par.addCar((byte)'D');
//		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		List<byte[][]> placementsA = new ArrayList<byte[][]>();
		List<byte[][]> placementsB = new ArrayList<byte[][]>();
		List<byte[][]> placementsC = new ArrayList<byte[][]>();
//		List<byte[][]> placementsD = new ArrayList<byte[][]>();
//		List<byte[][]> placementsE = new ArrayList<byte[][]>();
//		List<byte[][]> placementsF = new ArrayList<byte[][]>();
//		List<byte[][]> placementsG = new ArrayList<byte[][]>();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, true);
				for (byte[][] c : placementsC) {
					
					winners.add(c);
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, true);
				for (byte[][] c : placementsC) {
					
					winners.add(c);
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, true);
				for (byte[][] c : placementsC) {
					
					winners.add(c);
				}
				
			}
		}
		
		explorePartialWinners();
		
	}

	public static void add3and1(byte[][] red) {
		
		System.out.println("exploring 3 2cars and 1 3car");
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
//		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		List<byte[][]> placementsA = new ArrayList<byte[][]>();
		List<byte[][]> placementsB = new ArrayList<byte[][]>();
		List<byte[][]> placementsC = new ArrayList<byte[][]>();
		List<byte[][]> placementsD = new ArrayList<byte[][]>();
//		List<byte[][]> placementsE = new ArrayList<byte[][]>();
//		List<byte[][]> placementsF = new ArrayList<byte[][]>();
//		List<byte[][]> placementsG = new ArrayList<byte[][]>();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, true);
					for (byte[][] d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, true);
					for (byte[][] d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, true);
					for (byte[][] d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, true);
					for (byte[][] d : placementsD) {
						
						winners.add(d);
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
	}
	
	public static void add4and1(byte[][] red) {
		
		System.out.println("exploring 4 2cars and 1 3car");
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
//		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		List<byte[][]> placementsA = new ArrayList<byte[][]>();
		List<byte[][]> placementsB = new ArrayList<byte[][]>();
		List<byte[][]> placementsC = new ArrayList<byte[][]>();
		List<byte[][]> placementsD = new ArrayList<byte[][]>();
		List<byte[][]> placementsE = new ArrayList<byte[][]>();
//		List<byte[][]> placementsF = new ArrayList<byte[][]>();
//		List<byte[][]> placementsG = new ArrayList<byte[][]>();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, true);
						for (byte[][] e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, true);
						for (byte[][] e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, true);
						for (byte[][] e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, true);
						for (byte[][] e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, true);
						for (byte[][] e : placementsE) {
							
							winners.add(e);
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
	}

	


	
	
	
	
	
	
	
	
	
	public static void add5and1(byte[][] red) {
		
		System.out.println("exploring 5 2cars and 1 3car");
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
		Config.par.addCar((byte)'F');
//		Config.par.addCar((byte)'G');
		
		List<byte[][]> placementsA = new ArrayList<byte[][]>();
		List<byte[][]> placementsB = new ArrayList<byte[][]>();
		List<byte[][]> placementsC = new ArrayList<byte[][]>();
		List<byte[][]> placementsD = new ArrayList<byte[][]>();
		List<byte[][]> placementsE = new ArrayList<byte[][]>();
		List<byte[][]> placementsF = new ArrayList<byte[][]>();
//		List<byte[][]> placementsG = new ArrayList<byte[][]>();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, true);
							for (byte[][] f : placementsF) {
								
								winners.add(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void add5and2(byte[][] red) {
		
		System.out.println("exploring 5 2cars and 2 3cars");
		
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
		Config.par.addCar((byte)'F');
		Config.par.addCar((byte)'G');
		
		List<byte[][]> placementsA = new ArrayList<byte[][]>();
		List<byte[][]> placementsB = new ArrayList<byte[][]>();
		List<byte[][]> placementsC = new ArrayList<byte[][]>();
		List<byte[][]> placementsD = new ArrayList<byte[][]>();
		List<byte[][]> placementsE = new ArrayList<byte[][]>();
		List<byte[][]> placementsF = new ArrayList<byte[][]>();
		List<byte[][]> placementsG = new ArrayList<byte[][]>();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible3CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible3CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible3CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible3CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible3CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, false);
				for (byte[][] c : placementsC) {
					
					placementsD = new ArrayList<byte[][]>();
					Config.possible2CarPlacements(c, placementsD, false);
					for (byte[][] d : placementsD) {
						
						placementsE = new ArrayList<byte[][]>();
						Config.possible2CarPlacements(d, placementsE, false);
						for (byte[][] e : placementsE) {
							
							placementsF = new ArrayList<byte[][]>();
							Config.possible2CarPlacements(e, placementsF, false);
							for (byte[][] f : placementsF) {
								
								placementsG = new ArrayList<byte[][]>();
								Config.possible2CarPlacements(f, placementsG, true);
								for (byte[][] g : placementsG) {
									
									winners.add(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
		explorePartialWinners();
		
	}

}
