package generator;

import java.util.Arrays;

import solver.Config;

public class ConfigTrie {
	
//	HashSet<Config> set = new HashSet<Config>();
	
	int actualArrLength = 0;
	long[] arr = new long[10];
	
	public void add(Config c) {
		
//		boolean res = set.add(c);
//		assert res;
		
		boolean testRes = add(c.getInfoLong());
				
//		assert testRes == res;
		
	}
	
	public boolean contains(Config c) {
		
//		boolean res = set.contains(c);
		
		long l = c.getInfoLong();
		boolean testRes = contains(l);
		
//		assert testRes == res;
		
//		return res;
		return testRes;
	}
	
	public int size() {
		
//		int ret = set.size();
		
		int testRet = actualArrLength;
		
//		assert testRet == ret;
		
//		return ret;
		return testRet;
	}
	
	
	
	
	
	boolean add(long key) {
		
		int index = Arrays.binarySearch(arr, 0, actualArrLength, key);
		if (index >= 0) {
			/*
			 * key found
			 */
			return false;
		} else {
			/*
			 * key not found
			 */
			int insertionPoint = -(index+1);
			
			if (arr.length == actualArrLength) {
				
				long[] newArr = new long[actualArrLength * 2];
				
				/*
				 * copy from start to insertion point
				 */
				int copyLength = insertionPoint;
				arrayCopy(
						arr,
                        0,
                        newArr,
                        0,
                        copyLength);
				
				/*
				 * add insertion point
				 */
				newArr[insertionPoint] = key;
				
				/*
				 * copy from insertion point to end
				 */
				copyLength = actualArrLength - insertionPoint;
				arrayCopy(
						arr,
						insertionPoint,
                        newArr,
                        insertionPoint+1,
                        copyLength);
				
				arr = newArr;
				actualArrLength++;
				return true;
				
			} else {
				
				/*
				 * copy from insertion point to end
				 */
				int copyLength = actualArrLength - insertionPoint;
				arrayCopy(
						arr,
                        insertionPoint,
                        arr,
                        insertionPoint+1,
                        copyLength);
				
				/*
				 * add insertion point
				 */
				arr[insertionPoint] = key;
				
				actualArrLength++;
				return true;
			}
			
		}
		
	}
	
	
	boolean contains(long key) {
		
		int index = Arrays.binarySearch(arr, 0, actualArrLength, key);
		if (index >= 0) {
			/*
			 * key found
			 */
			return true;
		} else {
			/*
			 * key not found
			 */
			return false;
		}
	}
	
	public static void arrayCopy(long[] src, int srcPos, long[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

}
