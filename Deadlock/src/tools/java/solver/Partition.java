package solver;

import gnu.trove.list.array.TLongArrayList;

import java.util.List;


public class Partition {
	
	int partitionIdHash;
	
	byte[] partitionId;
	
	StateSpace space = new StateSpace();
	
	int totalBoardCount;
	int candidateBoardCount;
	
//	List<String> hardestSolution;
	
	public void generate() throws Exception {
		
		long t = System.currentTimeMillis();
		
		Board temp = new Board(Board.par.emptyBoard);
		
		int iteration = 0;
		while (true) {
			System.out.print(".");
			
			space.penGeneratingIteration = space.lastGeneratingIteration;
			space.lastGeneratingIteration = new TLongArrayList();
			
//			System.out.println(Config.toString(a));
			
			for (int i = 0; i < space.penGeneratingIteration.size(); i++) {
				long info = space.penGeneratingIteration.get(i);
				Board.par.emptyBoard.copyTo(temp);
				Board.toBoard(info, temp);
				explorePreviousMoves(temp);
			}
			totalBoardCount += space.penGeneratingIteration.size();
			if (iteration >= 20) {
				candidateBoardCount += space.penGeneratingIteration.size();
			}
			
			if (space.lastGeneratingIteration.isEmpty()) {
				break;
			}
			
			iteration++;
		}
		System.out.print("reached fixpoint... ");
		
		long longest = space.lastGeneratingConfig;
		assert longest != 0 : "fix me";
		
		space = null;
		
		if (longest != -1) {
			Board start = new Board(Board.par.emptyBoard);
			Board.toBoard(longest, start);
			List<String> hardestSolution = Solver.solve(start);
			System.out.println("hardest is " + hardestSolution.size() + " moves, total " + (totalBoardCount) + " boards, candidate " + candidateBoardCount + " boards, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
			System.out.println(start);
			System.out.println();
		} else {
			System.out.println("hardest is null, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		}
		
	}
	
	public void explorePreviousMoves(Board c) {
		
		List<Board> moves = c.possiblePreviousMoves();
		
		for (Board m : moves) {
			space.tryPutGenerating(m);
		}
	}

}
