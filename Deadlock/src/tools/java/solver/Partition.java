package solver;

import gnu.trove.list.array.TLongArrayList;

import java.util.List;


public class Partition {
	
	int partitionIdHash;
	
	byte[] partitionId;
	
	StateSpace space = new StateSpace();
	
	int totalBoardCount;
	
	List<String> hardestSolution;
	
	public void generate() throws Exception {
		
		long t = System.currentTimeMillis();
		
		byte[][] temp = Config.newConfig(Config.par.emptyBoard);
		
		while (true) {
			System.out.print(".");
			
			space.penGeneratingIteration = space.lastGeneratingIteration;
			space.lastGeneratingIteration = new TLongArrayList();
			
			for (int i = 0; i < space.penGeneratingIteration.size(); i++) {
				long info = space.penGeneratingIteration.get(i);
				Config.copyTo(Config.par.emptyBoard, temp);
				Config.toBoard(info, temp);
				explorePreviousMoves(temp);
			}
			totalBoardCount += space.penGeneratingIteration.size();
			
			if (space.lastGeneratingIteration.isEmpty()) {
				break;
			}
			
		}
		System.out.print("reached fixpoint... ");
		
		long longest = space.lastGeneratingConfig;
		assert longest != 0 : "fix me";
		
		space = null;
		
		if (longest != -1) {
			byte[][] start = Config.newConfig(Config.par.emptyBoard);
			Config.toBoard(longest, start);
			hardestSolution = Solver.solve(start);
			System.out.println("hardest is " + hardestSolution.size() + " moves, total " + (totalBoardCount) + " boards, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		} else {
			System.out.println("hardest is null, time: " + ((System.currentTimeMillis() - t) / 1000) + "s");
		}
		
	}
	
	public void explorePreviousMoves(byte[][] c) {
		
		List<byte[][]> moves = Config.possiblePreviousMoves(c);
		
		for (byte[][] m : moves) {
			space.tryPutGenerating(m);
		}
	}

}
