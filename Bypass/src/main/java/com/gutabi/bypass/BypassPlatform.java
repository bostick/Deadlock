package com.gutabi.bypass;

import com.gutabi.bypass.level.Level;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.Platform;

public interface BypassPlatform extends Platform {
	
	void loadScores(LevelDB levelDB) throws Exception;
	
	void saveScore(LevelDB levelDB, Level l);
	
	void clearScores(LevelDB levelDB);
	
}
