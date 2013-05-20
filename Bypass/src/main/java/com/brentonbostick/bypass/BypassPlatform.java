package com.brentonbostick.bypass;

import com.brentonbostick.bypass.level.Level;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.capsloc.Platform;
import com.brentonbostick.capsloc.Resource;

public interface BypassPlatform extends Platform {
	
	void createApplication() throws Exception;
	
	Resource levelDBResource(String name);
	
	LevelDB levelDB(String name);
	
	void loadScores(LevelDB levelDB) throws Exception;
	
	void saveScore(LevelDB levelDB, Level l);
	
	void clearScores(LevelDB levelDB);
	
}
