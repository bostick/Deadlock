package com.brentonbostick.bypass.level;

import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.brentonbostick.capsloc.Resource;

public class LevelDB {
	
	public final String resourceName;
	public String title;
	
	public int firstUnwon = 0;
	
	public double percentage;
	
	private Map<Integer, Level> levelMap = new HashMap<Integer, Level>();
	
	Resource res;
	public final int levelCount;
	
	public LevelDB(Resource res) throws Exception {
		
		this.res = res;
		
		this.resourceName = APP.platform.resourceName(res);
		
		InputStream is = APP.platform.openResourceInputStream(res);
		ZipInputStream zis = new ZipInputStream(is);
		
		while (true) {
			
			ZipEntry entry = zis.getNextEntry();
			assert entry != null;
			
			if (entry.getName().equals("metadata.txt")) {
				
				int count;
				byte tmp[] = new byte[100];
				
				count = zis.read(tmp, 0, 100);
				assert count != -1;
				
				byte[] data = new byte[count];
				System.arraycopy(tmp, 0, data, 0, count);
				
				levelCount = Integer.parseInt(new String(data));
				
				zis.close();
				is.close();
				break;
			}
		}
		
		is = APP.platform.openResourceInputStream(res);
		zis = new ZipInputStream(is);
		
		int count;
		byte tmp[] = new byte[100];
		
		for (int index = 0; index < levelCount; index++) {
			
			ZipEntry entry = zis.getNextEntry();
			if (entry.getName().equals("metadata.txt")) {
				zis.getNextEntry();
			}
			
			count = zis.read(tmp, 0, 100);
			assert count != -1;
			
			byte[] data = new byte[count];
			System.arraycopy(tmp, 0, data, 0, count);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			
			Level level = new Level();
			char[][] board;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(bais), count);
			
			int rows = 0;
			int cols = 0;
			
			StringBuilder builder = new StringBuilder();
			String inputLine = in.readLine();
			
			if (inputLine.matches("moves: .*")) {
				String rest = inputLine.substring(7);
				level.requiredMoves = Integer.parseInt(rest);
			} else {
				assert false;
			}
			
			while ((inputLine = in.readLine()) != null) {
				builder.append(inputLine);
				cols = inputLine.length();
				rows++;
			}
			in.close();
			String s = builder.toString();
			
			board = new char[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					board[i][j] = s.charAt(i * cols + j);
				}
			}
			
			level.index = index;
			level.ini = board;
			
			levelMap.put(index, level);
			
		}
		zis.close();
		is.close();
		
	}
	
	public void setFirstUnwon() {
		
		firstUnwon = -1;
		for (int i = 0; i < levelCount; i++) {
			if (levelMap.get(i).isWon) {
				
			} else {
				firstUnwon = i;
				break;
			}
		}
		
	}
	
	public void computePercentageComplete() {
		
		percentage = 0.0;
		for (int i = 0; i < levelCount; i++) {
			Level l = getLevel(i);
			if (l.grade == null) {
				
			} else {
				percentage = percentage + Math.min((double)l.requiredMoves / (double)l.userMoves, 1.0);
			}
		}
		
		percentage = percentage / levelCount;
		
	}
	
	public Level getLevel(int i) {
		return levelMap.get(i);
	}
	
	public void clearLevels() {
		
		for (Entry<Integer, Level> e : levelMap.entrySet()) {
			Level l = e.getValue();
			l.isWon = false;
			l.grade = null;
			l.userMoves = 0;
			l.userStartTime = 0;
			l.userTime = 0;
		}
		
		firstUnwon = 0;
		percentage = 0;
		
	}
}
