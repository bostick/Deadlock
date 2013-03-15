package solver;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSolver {
	
	public static void main(String[] args) throws Exception {
		TestSolver s = new TestSolver();
		s.test1();
	}
	
	static byte[][] hangerParent = new byte[][] {
			{ '/', '-', '-', '-', 'J', '-', '-', '\\'},
			{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
			{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
			{ 'J', ' ', ' ', ' ', ' ', ' ', ' ', 'Y'},
			{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
			{ '|', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
			{ 'K', ' ', ' ', ' ', ' ', ' ', ' ', '|'},
			{'\\', '-', '-', '-', 'K', '-', '-', '/'},
		};
	
	@Test
	public void test1() throws Exception {
		
		byte[][] hanger = new byte[][] {
				{ 'A', 'B', 'C', 'C', ' ', ' '},
				{ 'A', 'B', ' ', ' ', 'D', 'D'},
				{ ' ', 'B', ' ', ' ', ' ', ' '},
				{ ' ', ' ', ' ', ' ', ' ', ' '},
				{ 'E', 'E', ' ', ' ', ' ', ' '},
				{ ' ', 'R', 'R', ' ', 'F', 'F'}
			};
		
		Config.par = new ParentConfig(hangerParent);
		Config.par.addCar((byte)'R');
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		Config.par.addCar((byte)'E');
		Config.par.addCar((byte)'F');
		
		Solver.solve(hanger);
		
	}
	
	@Test
	public void test2() throws Exception {
		
		byte[][] hanger = new byte[][] {
				{ ' ', 'A', ' ', ' ', ' ', ' '},
				{ ' ', 'A', ' ', ' ', ' ', ' '},
				{ ' ', ' ', 'B', ' ', 'C', 'D'},
				{ ' ', ' ', 'B', ' ', 'C', 'D'},
				{ ' ', ' ', ' ', 'R', ' ', ' '},
				{ ' ', ' ', ' ', 'R', ' ', ' '}
			};
		
		Config.par = new ParentConfig(hangerParent);
		Config.par.addCar((byte)'R');
		Config.par.addCar((byte)'A');
		Config.par.addCar((byte)'B');
		Config.par.addCar((byte)'C');
		Config.par.addCar((byte)'D');
		
		List<String> solution = Solver.solve(hanger);
		
		assertEquals(solution.size(), 12);
	}
	
	
}
