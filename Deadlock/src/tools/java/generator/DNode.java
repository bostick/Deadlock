package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class DNode {
	
	public byte value;
	
	List<Byte> eleaves = new ArrayList<Byte>();
	
	public void add(Config c) {
		
		byte ebyte = c.getInfoByte((byte)'E');
		boolean added = false;
		for (Byte eleaf : eleaves) {
			if (eleaf == ebyte) {
				assert false;
			}
		}
		if (!added) {
			eleaves.add(ebyte);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte ebyte = c.getInfoByte((byte)'E');
		boolean found = false;
		boolean testRes = false;
		for (Byte eleaf : eleaves) {
			if (eleaf == ebyte) {
				testRes = true;
				found = true;
				break;
			}
		}
		if (!found) {
			testRes = false;
		}
		
		return testRes;
	}
	
	public int size() {
		
		return eleaves.size();
	}
	
}
