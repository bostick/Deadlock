package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {
	
	/*
	 * how many 2cars and 3cars per board
	 */
//	static int TWOCARS = 5;
//	static int THREECARS = 1;
	
	/*
	 * how many of the rows and cols to count for the partitions.
	 * for a normal 6x6 board, you could specify PARTITIONID_ROWCOUNT = 6 and
	 * PARTITIONID_COLCOUNT = 6, and that would ensure that all boards are perfectly partitioned
	 * The downside is that this may create way too many partitions to process, taking up way more memory AND time
	 * 
	 * with enough memory, we would not need any partitioning. partitioning exists solely to be able to fit a given amount of work
	 * in memory as we work. the same number boards have to be processed eventually, no matter how they are partitioned
	 */
	static int PARTITIONID_ROWCOUNT = 6;
	static int PARTITIONID_COLCOUNT = 0;
	
	
	
	public static void main(String[] args) throws Exception {
		
		generate(args);

	}
	
	static Map<Integer, Partition> partitions = new HashMap<Integer, Partition>();
	
	static long t;
	static long m;
	
	public static void generate(String[] args) throws Exception {
		
		int TWOCARS = Integer.parseInt(args[0]);
		int THREECARS = Integer.parseInt(args[1]);
		
		t = System.currentTimeMillis();
		m = Runtime.getRuntime().totalMemory();
		
		int boardNumber = Integer.parseInt(args[2]);
		byte[][] board = null;
		if (boardNumber == 0) {
			board = Boards.board0;
		} else if (boardNumber == 1) {
			board = Boards.board1;
		} else if (boardNumber == 2) {
			board = Boards.board2;
		}
		
		Board.par = new Parent(board);
		Board.par.addCar((byte)'R');
		
		Board red = new Board(Board.par.emptyBoard);
		red = red.winningConfig();
		
		System.out.println("generating boards");
		
		System.out.println("board:");
		System.out.println(red);
		
		System.out.println("2cars: " + TWOCARS + ", 3cars: " + THREECARS);
		System.out.println("partition ids: " + PARTITIONID_ROWCOUNT + "+" + PARTITIONID_COLCOUNT);
		
		generateWinners(TWOCARS, THREECARS, red);
		
		int totalBoards = 0;
		int candidateBoards = 0;
//		List<String> hardest = null;
		int i = 0;
		for (Partition p : partitions.values()) {
			System.out.println("generating partition #" + i);
			System.out.print("idhash " + p.partitionIdHash + ", starting with " + p.space.lastGeneratingIteration.size() + " winners (" + (100 * p.space.lastGeneratingIteration.size() / winnersCount) + "%)... ");
			
//			if (p.partitionIdHash != 739147418) {
//				continue;
//			}
			
			p.generate();
			if (Runtime.getRuntime().totalMemory() > m) {
				m = Runtime.getRuntime().totalMemory();
				System.out.println("max total memory: " + (m / 1024 / 1024) + "MB");
			}
			totalBoards += p.totalBoardCount;
			candidateBoards += p.candidateBoardCount;
//			if (hardest == null || p.hardestSolution != null && p.hardestSolution.size() > hardest.size()) {
//				hardest = p.hardestSolution;
//			}
			i++;
		}
		System.out.println();
		
//		if (hardest == null) {
//			System.out.println("hardest is null");
//		} else {
//			System.out.println("hardest overall: " + hardest.size() + " moves, total " + totalBoards + "  boards, candidate " + candidateBoards + " boards");
//			for (String s : hardest) {
//				System.out.println(s);
//				System.out.println();
//			}
//		}
		
		System.out.println("total time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		System.out.println("max total memory: " + (m / 1024 / 1024) + "MB");
	}
	
	public static void generateWinners(int twoCars, int threeCars, Board base) {
		
		System.out.println("generating winners... ");
		
		for (int i = 0; i < (twoCars + threeCars); i++) {
			Board.par.addCar((byte)('A' + i));
		}
		
		generateWinnersRecur(twoCars, threeCars, base);
		
		System.out.println("done");
		
		System.out.println(partitions.values().size() + " partitions, " + (winnersCount) + " winners, with avg. " + (winnersCount / partitions.values().size()) + " winners / partition");
		System.out.println();
	}
	
	public static void generateWinnersRecur(int twoCars, int threeCars, Board base) {
		
		List<Board> placements = new ArrayList<Board>();
		
		if (twoCars == 1 && threeCars == 0) {
			placements = new ArrayList<Board>();
			base.possibleCarPlacements(2, placements, true);
			for (Board board : placements) {
				addWinnerBoard(board);
			}
		} else if (twoCars > 0) {
			placements = new ArrayList<Board>();
			base.possibleCarPlacements(2, placements, false);
			for (Board board : placements) {
				generateWinnersRecur(twoCars-1, threeCars, board);
			}
		}
		
		if (threeCars == 1 && twoCars == 0) {
			placements = new ArrayList<Board>();
			base.possibleCarPlacements(3, placements, true);
			for (Board board : placements) {
				addWinnerBoard(board);
			}
		} else if (threeCars > 0) {
			placements = new ArrayList<Board>();
			base.possibleCarPlacements(3, placements, false);
			for (Board board : placements) {
				generateWinnersRecur(twoCars, threeCars-1, board);
			}
		}
		
	}
	
	static int winnersCount = 0;
	
	public static void addWinnerBoard(Board board) {
		
		winnersCount++;
		
		byte[] partitionId = new byte[PARTITIONID_ROWCOUNT + PARTITIONID_COLCOUNT];
		
		board.getPartitionId(PARTITIONID_ROWCOUNT, PARTITIONID_COLCOUNT, partitionId);
		
		int idHash = hashPartitionId(partitionId);
		
		if (!partitions.containsKey(idHash)) {
			
			Partition p = new Partition();
			p.partitionIdHash = idHash;
			p.partitionId = partitionId;
			
			p.space.lastGeneratingIteration.add(board.getInfoLong());
			
			partitions.put(idHash, p);
			
			System.out.println("added idhash " + idHash + ", partitions count: " + partitions.size() );
			
		} else {
			
			Partition p = partitions.get(idHash);
			
			p.space.lastGeneratingIteration.add(board.getInfoLong());
			
		}
		
		
		if (Runtime.getRuntime().totalMemory() > m) {
			m = Runtime.getRuntime().totalMemory();
			System.out.println("max total memory: " + (m / 1024 / 1024) + "MB");
		}
		
	}
	
	public static int hashPartitionId(byte[] tempPartitionId) {
		
		int h = 17;
		for (int i = 0; i < tempPartitionId.length; i++) {
			h = 37 * h + tempPartitionId[i];
		}
		
		return h;
	}

}
