package com.gutabi.bypass;

import com.gutabi.bypass.level.Level;
import com.gutabi.bypass.level.LevelDB;
import com.gutabi.capsloc.Platform;
import com.gutabi.capsloc.Resource;

public interface BypassPlatform extends Platform {
	
	void createApplication() throws Exception;
	
	Resource levelDBResource(String name);
	
	LevelDB levelDB(String name);
	
	void loadScores(LevelDB levelDB) throws Exception;
	
	void saveScore(LevelDB levelDB, Level l);
	
	void clearScores(LevelDB levelDB);
	
}
