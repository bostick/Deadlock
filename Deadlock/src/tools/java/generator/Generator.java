package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	static Map<Integer, Partition> partitions = new HashMap<Integer, Partition>();
	
//	static List<byte[][]> winners = new ArrayList<byte[][]>();
	
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
		
		System.out.println("generating winners... ");
//		add2and1(red);
//		add3and1(red);
//		add4and1(red);
//		add5and1(red);
		add5and2(red);
		System.out.println("done");
		
		System.out.println(partitions.values().size() + " partitions");
		
		List<String> hardest = null;
		int i = 0;
		for (Partition p : partitions.values()) {
			System.out.println("generating partition #" + i + " (idhash " + p.partitionIdHash + ")... ");
			p.generate();
			if (hardest == null || p.hardestSolution != null && p.hardestSolution.size() > hardest.size()) {
				hardest = p.hardestSolution;
			}
			i++;
		}
		
		if (hardest == null) {
			System.out.println("hardest is null");
		} else {
			System.out.println("hardest overall: " + hardest.size() + " moves");
			for (String s : hardest) {
				System.out.println(s);
				System.out.println();
			}
		}
		
	}
	
	public static int hash(byte[] tempPartitionId) {
		
		int h = 17;
		for (int i = 0; i < tempPartitionId.length; i++) {
			h = 37 * h + tempPartitionId[i];
		}
		
		return h;
	}

	public static void winnersAdd(byte[][] board) {
		
		byte[] partitionId = new byte[6];
		
		Config.getPartitionId(board, partitionId);
		
		int idHash = hash(partitionId);
		
		if (!partitions.containsKey(idHash)) {
			
			Partition p = new Partition();
			p.partitionIdHash = idHash;
			
			p.space.lastGeneratingIteration.add(Config.getInfoLong(board));
			
			partitions.put(idHash, p);
			
			System.out.println("added idhash " + idHash + ", partitions count: " + partitions.size() );
			
		} else {
			
			Partition p = partitions.get(idHash);
			
			p.space.lastGeneratingIteration.add(Config.getInfoLong(board));
			
//			System.out.println("partition " + idHash + " size: " + p.space.lastGeneratingIteration.size());
		}
		
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
					
					winnersAdd(c);
				}
				
			}
		}
		
				
		placementsA = new ArrayList<byte[][]>();
		Config.possible2CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible3CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, true);
				for (byte[][] c : placementsC) {
					
					winnersAdd(c);
				}
				
			}
		}
		
				
		placementsA = new ArrayList<byte[][]>();
		Config.possible3CarPlacements(red, placementsA, false);
		for (byte[][] a : placementsA) {
			
			placementsB = new ArrayList<byte[][]>();
			Config.possible2CarPlacements(a, placementsB, false);
			for (byte[][] b : placementsB) {
				
				placementsC = new ArrayList<byte[][]>();
				Config.possible2CarPlacements(b, placementsC, true);
				for (byte[][] c : placementsC) {
					
					winnersAdd(c);
				}
				
			}
		}
		
				
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
						
						winnersAdd(d);
						
					}
				}
				
			}
		}
		
				
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
						
						winnersAdd(d);
						
					}
				}
				
			}
		}
		
				
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
						
						winnersAdd(d);
						
					}
				}
				
			}
		}
		
				
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
						
						winnersAdd(d);
						
					}
				}
				
			}
		}
		
				
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
							
							winnersAdd(e);
							
						}
						
					}
				}
				
			}
		}
		
				
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
							
							winnersAdd(e);
							
						}
						
					}
				}
				
			}
		}
		
				
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
							
							winnersAdd(e);
							
						}
						
					}
				}
				
			}
		}
		
				
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
							
							winnersAdd(e);
							
						}
						
					}
				}
				
			}
		}
		
				
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
							
							winnersAdd(e);
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
								
								winnersAdd(f);
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
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
									
									winnersAdd(g);
									
								}
								
							}
							
						}
						
					}
				}
				
			}
		}
		
				
	}

}
