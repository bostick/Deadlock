package generator;

import java.util.HashSet;

import solver.Config;

public class ConfigTrie {
	
	HashSet<Config> set = new HashSet<Config>();
	
	RootNode root = new RootNode();
	
	public void add(Config c) {
		
		boolean res = set.add(c);
		assert res;
		
		root.add(c);
	}
	
	public boolean contains(Config c) {
		
		boolean res = set.contains(c);
		
		boolean testRes = root.contains(c);
		
		assert testRes == res;
		
		return res;
	}
	
	public int size() {
		
		int ret = set.size();
		
		int testRet = root.size();
		
		assert testRet == ret;
		
		return ret;
	}
	
}
