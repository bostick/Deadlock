package com.brentonbostick.bypass.android.level;

import static com.brentonbostick.bypass.BypassApplication.BYPASSAPP;
import static com.brentonbostick.capsloc.CapslocApplication.APP;

import java.lang.reflect.Constructor;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.brentonbostick.bypass.android.ActivityState;
import com.brentonbostick.bypass.android.BypassActivity;
import com.brentonbostick.bypass.android.BypassAndroidPlatform;
import com.brentonbostick.bypass.android.BypassView;
import com.brentonbostick.bypass.android.R;
import com.brentonbostick.bypass.level.BypassCar;
import com.brentonbostick.bypass.level.BypassWorld;
import com.brentonbostick.bypass.level.LevelDB;
import com.brentonbostick.bypass.level.WinnerMenu;
import com.brentonbostick.bypass.menu.LevelMenu;
import com.brentonbostick.capsloc.SimulationRunnable;
import com.brentonbostick.capsloc.geom.Geom;
import com.brentonbostick.capsloc.geom.MutableOBB;
import com.brentonbostick.capsloc.geom.MutableSweptOBB;
import com.brentonbostick.capsloc.math.Point;
import com.brentonbostick.capsloc.world.WorldCamera;
import com.brentonbostick.capsloc.world.cars.Car;
import com.brentonbostick.capsloc.world.cars.CarStateEnum;
import com.brentonbostick.capsloc.world.graph.GraphPosition;
import com.brentonbostick.capsloc.world.graph.GraphPositionPathPosition;

public class BypassWorldActivity extends BypassActivity {
	
	{
		name = "bypassworld";
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bypassworld);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (BYPASSAPP == null) {
			try {
				
				String platformImplName = (String)getIntent().getExtras().get("com.brentonbostick.bypass.android.PlatformImplClassName");
				
				Class<?> cArg = Class.forName(platformImplName);
				Constructor<?> platformCtor = cArg.getConstructor(Resources.class);
				
				BypassAndroidPlatform platform = (BypassAndroidPlatform)platformCtor.newInstance(getResources());
				try {
					platform.createApplication();
				} catch (Exception e) {
					Log.e("bypass", e.getMessage(), e);
				}
				
			} catch (Exception e) {
				Log.e("bypass", e.getMessage(), e);
			}
		}
		
		v = (BypassView)findViewById(R.id.view_bypassworld);
		
		v.ctxt = APP.platform.createRenderingContext();
		v.activity = this;
		
		int ii = (Integer)getIntent().getExtras().get("com.brentonbostick.bypass.level.Index");
		
		if (savedInstanceState != null) {
			String name = savedInstanceState.getString("com.brentonbostick.bypass.menu.LevelDBName");
			LevelMenu.levelDBName = name;
		}
		
		LevelDB levelDB = BYPASSAPP.bypassPlatform.levelDB(LevelMenu.levelDBName);
		
		BypassWorld.create(levelDB, ii);
		
		if (savedInstanceState != null) {
			
			APP.DEBUG_DRAW = savedInstanceState.getBoolean("com.brentonbostick.bypass.DebugDraw");
			
			BypassWorld.BYPASSWORLD.curLevel.userMoves = savedInstanceState.getInt("com.brentonbostick.bypass.level.UserMoves");
			BypassWorld.BYPASSWORLD.curLevel.userStartTime = savedInstanceState.getLong("com.brentonbostick.bypass.level.UserStartTime");
			BypassWorld.BYPASSWORLD.curLevel.isWon = savedInstanceState.getBoolean("com.brentonbostick.bypass.level.IsWon");
			
			BypassWorld.BYPASSWORLD.worldCamera = (WorldCamera)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.WorldCamera");
			
			for (Car cc : BypassWorld.BYPASSWORLD.carMap.cars) {
				BypassCar c = (BypassCar)cc;
				
				int id = c.id;
				
				c.state = (CarStateEnum)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.Car"+id+".State");
				c.center = (Point)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.Car"+id+".Center");
				c.angle = savedInstanceState.getDouble("com.brentonbostick.bypass.level.Car"+id+".Angle");
				Geom.localToWorld(c.localAABB, c.angle, c.center, c.shape);
				c.setPhysicsTransform();
				c.driver.overallPos = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.Car"+id+".OverallPos");
				c.driver.overallPos.path = c.driver.overallPath;
				if (c.driver.overallPos.bound) {
					c.driver.overallPos.gp = c.driver.overallPos.path.get(c.driver.overallPos.index);
					c.driver.overallPos.p = c.driver.overallPos.gp.p;
				} else {
					GraphPosition p1 = c.driver.overallPos.path.get(c.driver.overallPos.index);
					GraphPosition p2 = c.driver.overallPos.path.get(c.driver.overallPos.index+1);			
					double dist = Point.distance(p1.p, p2.p);
					c.driver.overallPos.gp = p1.approachNeighbor(p2, dist * c.driver.overallPos.param);
					c.driver.overallPos.p = c.driver.overallPos.gp.p;
				}
				c.driver.overallPos.mao = new MutableOBB();
				c.driver.overallPos.mbo = new MutableOBB();
				c.driver.overallPos.swept = new MutableSweptOBB();
				
				
				if (c.state != CarStateEnum.IDLE) {
					c.driver.toolCoastingGoal = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.Car"+id+".CoastingGoal");
					c.driver.toolCoastingGoal.path = c.driver.overallPath;
					if (c.driver.toolCoastingGoal.bound) {
						c.driver.toolCoastingGoal.gp = c.driver.toolCoastingGoal.path.get(c.driver.toolCoastingGoal.index);
						c.driver.toolCoastingGoal.p = c.driver.toolCoastingGoal.gp.p;
					} else {
						GraphPosition p1 = c.driver.toolCoastingGoal.path.get(c.driver.toolCoastingGoal.index);
						GraphPosition p2 = c.driver.toolCoastingGoal.path.get(c.driver.toolCoastingGoal.index+1);			
						double dist = Point.distance(p1.p, p2.p);
						c.driver.toolCoastingGoal.gp = p1.approachNeighbor(p2, dist * c.driver.toolCoastingGoal.param);
						c.driver.toolCoastingGoal.p = c.driver.toolCoastingGoal.gp.p;
					}
					c.driver.toolCoastingGoal.mao = new MutableOBB();
					c.driver.toolCoastingGoal.mbo = new MutableOBB();
					c.driver.toolCoastingGoal.swept = new MutableSweptOBB();
					
					
					c.driver.toolOrigExitingVertexPos = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.brentonbostick.bypass.level.Car"+id+".OrigExitingVertexPos");
					if (c.driver.toolOrigExitingVertexPos != null) {
						c.driver.toolOrigExitingVertexPos.path = c.driver.overallPath;
						if (c.driver.toolOrigExitingVertexPos.bound) {
							c.driver.toolOrigExitingVertexPos.gp = c.driver.toolOrigExitingVertexPos.path.get(c.driver.toolOrigExitingVertexPos.index);
							c.driver.toolOrigExitingVertexPos.p = c.driver.toolOrigExitingVertexPos.gp.p;
						} else {
							GraphPosition p1 = c.driver.toolOrigExitingVertexPos.path.get(c.driver.toolOrigExitingVertexPos.index);
							GraphPosition p2 = c.driver.toolOrigExitingVertexPos.path.get(c.driver.toolOrigExitingVertexPos.index+1);			
							double dist = Point.distance(p1.p, p2.p);
							c.driver.toolOrigExitingVertexPos.gp = p1.approachNeighbor(p2, dist * c.driver.toolOrigExitingVertexPos.param);
							c.driver.toolOrigExitingVertexPos.p = c.driver.toolOrigExitingVertexPos.gp.p;
						}
						c.driver.toolOrigExitingVertexPos.mao = new MutableOBB();
						c.driver.toolOrigExitingVertexPos.mbo = new MutableOBB();
						c.driver.toolOrigExitingVertexPos.swept = new MutableSweptOBB();
					}
					
					c.coastingVel = savedInstanceState.getDouble("com.brentonbostick.bypass.level.Car"+id+".CoastingVel");
				}
				
			}
			
			if (BypassWorld.BYPASSWORLD.curLevel.isWon) {
				BypassWorld.BYPASSWORLD.curLevel.userTime = savedInstanceState.getLong("com.brentonbostick.bypass.level.UserTime");
				BypassWorld.BYPASSWORLD.curLevel.grade = savedInstanceState.getString("com.brentonbostick.bypass.level.Grade");
				
				String excl;
				char letter = BypassWorld.BYPASSWORLD.curLevel.grade.charAt(0);
				if (letter == 'A') {
					excl = "Excellent!";
				} else if (letter == 'B') {
					excl = "Good!";
				} else if (letter == 'C') {
					excl = "OK!";
				} else if (letter == 'D') {
					excl = "Adequate!";
				} else {
					excl = "Effort!";
				}
				
				BypassWorld.BYPASSWORLD.winnerMenu = new WinnerMenu(BypassWorld.BYPASSWORLD, excl, "Grade: " + BypassWorld.BYPASSWORLD.curLevel.grade);
				
				BypassWorld.BYPASSWORLD.lock.lock();
				try {
					
					BypassWorld.BYPASSWORLD.postDisplay((int)BypassWorld.BYPASSWORLD.worldCamera.panelAABB.width, (int)BypassWorld.BYPASSWORLD.worldCamera.panelAABB.height);
					BypassWorld.BYPASSWORLD.render();
					
				} finally {
					BypassWorld.BYPASSWORLD.lock.unlock();
				}
			}
		}
	}
	
	protected void onStart() {
    	super.onStart();
    	
		BypassWorld.start();
    }
	
	protected void onStop() {
		super.onStop();
		
		BypassWorld.stop();
	}
	
	protected void onResume() {
    	super.onResume();
    	
		BypassWorld.resume();
    }
	
	protected void onSurfaceChanged(int width, int height) {
		
		Log.d("bypassactivity", name + " surfaceChanged");
		
		if (state != ActivityState.RESUME) {
			/*
			 * locking the screen causes surface change after pause, so ignore that
			 */
			return;
		}
		
		BypassWorld.surfaceChanged(width, height);
	}
	
	protected void onPause() {
		super.onPause();
		
		BypassWorld.pause();
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		BypassWorld.BYPASSWORLD.simThreadTrigger.set(false);
		
		try {
			
			BypassWorld.BYPASSWORLD.simThread.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		outState.putBoolean("com.brentonbostick.bypass.DebugDraw", APP.DEBUG_DRAW);
		
		outState.putString("com.brentonbostick.bypass.menu.LevelDBName", LevelMenu.levelDBName);
		
		outState.putInt("com.brentonbostick.bypass.level.UserMoves", BypassWorld.BYPASSWORLD.curLevel.userMoves);
		outState.putLong("com.brentonbostick.bypass.level.UserStartTime", BypassWorld.BYPASSWORLD.curLevel.userStartTime);
		outState.putBoolean("com.brentonbostick.bypass.level.IsWon", BypassWorld.BYPASSWORLD.curLevel.isWon);
		
		if (BypassWorld.BYPASSWORLD.curLevel.isWon) {
			outState.putLong("com.brentonbostick.bypass.level.UserTime", BypassWorld.BYPASSWORLD.curLevel.userTime);
			outState.putString("com.brentonbostick.bypass.level.Grade", BypassWorld.BYPASSWORLD.curLevel.grade);
		}
		
		outState.putSerializable("com.brentonbostick.bypass.level.WorldCamera", BypassWorld.BYPASSWORLD.worldCamera);
		
		for (Car cc : BypassWorld.BYPASSWORLD.carMap.cars) {
			BypassCar c = (BypassCar)cc;
			
			int id = c.id;
			
			outState.putSerializable("com.brentonbostick.bypass.level.Car"+id+".Center", c.center);
			outState.putDouble("com.brentonbostick.bypass.level.Car"+id+".Angle", c.angle);
			outState.putSerializable("com.brentonbostick.bypass.level.Car"+id+".OverallPos", c.driver.overallPos);
			
			outState.putSerializable("com.brentonbostick.bypass.level.Car"+id+".State", c.state);
			if (c.state != CarStateEnum.IDLE) {
				outState.putSerializable("com.brentonbostick.bypass.level.Car"+id+".CoastingGoal", c.driver.toolCoastingGoal);
				outState.putSerializable("com.brentonbostick.bypass.level.Car"+id+".OrigExitingVertexPos", c.driver.toolOrigExitingVertexPos);
				outState.putDouble("com.brentonbostick.bypass.level.Car"+id+".CoastingVel", c.coastingVel);
			}
		}
		
		BypassWorld.BYPASSWORLD.simThreadTrigger.set(true);
		
		BypassWorld.BYPASSWORLD.simThread = new Thread(new SimulationRunnable(BypassWorld.BYPASSWORLD.simThreadTrigger));
		BypassWorld.BYPASSWORLD.simThread.start();
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.world_context_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.btn_world_resetLevel) {
			BypassWorld.BYPASSWORLD.reset();
			return true;
		} else if (item.getItemId() == R.id.btn_world_toggleInfo) {
			BypassWorld.showInfo = !BypassWorld.showInfo;
			BypassWorld.BYPASSWORLD.lock.lock();
			try {
				
				BypassWorld.BYPASSWORLD.render();
				
			} finally {
				BypassWorld.BYPASSWORLD.lock.unlock();
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
}
