package generator;

import java.util.ArrayList;
import java.util.List;

import solver.Config;

public class RNode {
	
	public byte value;
	
	List<ANode> anodes = new ArrayList<ANode>();
	
	public void add(Config c) {
		
		byte abyte = c.getInfoByte((byte)'A');
		boolean added = false;
		for (ANode anode : anodes) {
			if (anode.value == abyte) {
				anode.add(c);
				added = true;
				break;
			}
		}
		if (!added) {
			ANode anode = new ANode();
			anode.value = abyte;
			anode.add(c);
			anodes.add(anode);
		}
		
	}
	
	public boolean contains(Config c) {
		
		byte abyte = c.getInfoByte((byte)'A');
		boolean found = false;
		boolean testRes = false;
		for (ANode anode : anodes) {
			if (anode.value == abyte) {
				testRes = anode.contains(c);
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
		
		int testRet = 0;
		for (ANode anode : anodes) {
			testRet += anode.size();
		}
		
		return testRet;
	}
	
}
