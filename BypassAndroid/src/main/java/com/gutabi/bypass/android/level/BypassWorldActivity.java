package com.gutabi.bypass.android.level;

import static com.gutabi.bypass.BypassApplication.BYPASSAPP;
import static com.gutabi.capsloc.CapslocApplication.APP;

import java.lang.reflect.Constructor;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gutabi.bypass.BypassApplication;
import com.gutabi.bypass.android.ActivityState;
import com.gutabi.bypass.android.BypassActivity;
import com.gutabi.bypass.android.BypassAndroidPlatform;
import com.gutabi.bypass.android.BypassView;
import com.gutabi.bypass.android.R;
import com.gutabi.bypass.level.BypassCar;
import com.gutabi.bypass.level.BypassWorld;
import com.gutabi.bypass.level.WinnerMenu;
import com.gutabi.bypass.menu.LevelMenu;
import com.gutabi.capsloc.SimulationRunnable;
import com.gutabi.capsloc.geom.Geom;
import com.gutabi.capsloc.geom.MutableOBB;
import com.gutabi.capsloc.geom.MutableSweptOBB;
import com.gutabi.capsloc.math.Point;
import com.gutabi.capsloc.ui.MenuTool;
import com.gutabi.capsloc.world.WorldCamera;
import com.gutabi.capsloc.world.cars.Car;
import com.gutabi.capsloc.world.cars.CarStateEnum;
import com.gutabi.capsloc.world.graph.GraphPosition;
import com.gutabi.capsloc.world.graph.GraphPositionPathPosition;

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
				
				String platformImplName = (String)getIntent().getExtras().get("com.gutabi.bypass.android.PlatformImplClassName");
				
				Class<?> cArg = Class.forName(platformImplName);
				Constructor<?> platformCtor = cArg.getConstructor(Resources.class);
				
				BypassAndroidPlatform platform = (BypassAndroidPlatform)platformCtor.newInstance(getResources());
				try {
					BypassApplication.create(platform);
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
		
		int ii = (Integer)getIntent().getExtras().get("com.gutabi.bypass.level.Index");
		
		if (savedInstanceState != null) {
			String name = savedInstanceState.getString("com.gutabi.bypass.menu.LevelDB");
			if (name.equals("tutorial")) {
				LevelMenu.levelDB = BYPASSAPP.tutorialLevelDB;
			} else if (name.equals("episode1")) {
				LevelMenu.levelDB = BYPASSAPP.episode1LevelDB;
			} else if (name.equals("episode2")) {
				LevelMenu.levelDB = BYPASSAPP.episode2LevelDB;
			} else {
				throw new AssertionError();
			}
		}
		
		BypassWorld.create(LevelMenu.levelDB, ii);
		
		if (savedInstanceState != null) {
			
			APP.DEBUG_DRAW = savedInstanceState.getBoolean("com.gutabi.bypass.DebugDraw");
			
			BypassWorld.BYPASSWORLD.curLevel.userMoves = savedInstanceState.getInt("com.gutabi.bypass.level.UserMoves");
			BypassWorld.BYPASSWORLD.curLevel.userStartTime = savedInstanceState.getLong("com.gutabi.bypass.level.UserStartTime");
			BypassWorld.BYPASSWORLD.curLevel.isWon = savedInstanceState.getBoolean("com.gutabi.bypass.level.IsWon");
			
			BypassWorld.BYPASSWORLD.worldCamera = (WorldCamera)savedInstanceState.getSerializable("com.gutabi.bypass.level.WorldCamera");
			
			for (Car cc : BypassWorld.BYPASSWORLD.carMap.cars) {
				BypassCar c = (BypassCar)cc;
				
				int id = c.id;
				
				c.state = (CarStateEnum)savedInstanceState.getSerializable("com.gutabi.bypass.level.Car"+id+".State");
				c.center = (Point)savedInstanceState.getSerializable("com.gutabi.bypass.level.Car"+id+".Center");
				c.angle = savedInstanceState.getDouble("com.gutabi.bypass.level.Car"+id+".Angle");
				Geom.localToWorld(c.localAABB, c.angle, c.center, c.shape);
				c.setPhysicsTransform();
				c.driver.overallPos = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.gutabi.bypass.level.Car"+id+".OverallPos");
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
					c.driver.toolCoastingGoal = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.gutabi.bypass.level.Car"+id+".CoastingGoal");
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
					
					
					c.driver.toolOrigExitingVertexPos = (GraphPositionPathPosition)savedInstanceState.getSerializable("com.gutabi.bypass.level.Car"+id+".OrigExitingVertexPos");
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
					
					
					c.coastingVel = savedInstanceState.getDouble("com.gutabi.bypass.level.Car"+id+".CoastingVel");
				}
				
			}
			
			if (BypassWorld.BYPASSWORLD.curLevel.isWon) {
				BypassWorld.BYPASSWORLD.curLevel.userTime = savedInstanceState.getLong("com.gutabi.bypass.level.UserTime");
				BypassWorld.BYPASSWORLD.curLevel.grade = savedInstanceState.getString("com.gutabi.bypass.level.Grade");
				
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
				
				BypassWorld.BYPASSWORLD.render_worldPanel();
				BypassWorld.BYPASSWORLD.panelPostDisplay((int)BypassWorld.BYPASSWORLD.worldCamera.panelAABB.width, (int)BypassWorld.BYPASSWORLD.worldCamera.panelAABB.height);
				
				APP.tool = new MenuTool();
				
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
		
		if (state == ActivityState.PAUSE) {
			/*
			 * locking the screen causes surface change after pause
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
		
		
		outState.putBoolean("com.gutabi.bypass.DebugDraw", APP.DEBUG_DRAW);
		
		outState.putString("com.gutabi.bypass.menu.LevelDB", LevelMenu.levelDB.name);
		
		outState.putInt("com.gutabi.bypass.level.UserMoves", BypassWorld.BYPASSWORLD.curLevel.userMoves);
		outState.putLong("com.gutabi.bypass.level.UserStartTime", BypassWorld.BYPASSWORLD.curLevel.userStartTime);
		outState.putBoolean("com.gutabi.bypass.level.IsWon", BypassWorld.BYPASSWORLD.curLevel.isWon);
		
		if (BypassWorld.BYPASSWORLD.curLevel.isWon) {
			outState.putLong("com.gutabi.bypass.level.UserTime", BypassWorld.BYPASSWORLD.curLevel.userTime);
			outState.putString("com.gutabi.bypass.level.Grade", BypassWorld.BYPASSWORLD.curLevel.grade);
		}
		
		outState.putSerializable("com.gutabi.bypass.level.WorldCamera", BypassWorld.BYPASSWORLD.worldCamera);
		
		for (Car cc : BypassWorld.BYPASSWORLD.carMap.cars) {
			BypassCar c = (BypassCar)cc;
			
			int id = c.id;
			
			outState.putSerializable("com.gutabi.bypass.level.Car"+id+".Center", c.center);
			outState.putDouble("com.gutabi.bypass.level.Car"+id+".Angle", c.angle);
			outState.putSerializable("com.gutabi.bypass.level.Car"+id+".OverallPos", c.driver.overallPos);
			
			outState.putSerializable("com.gutabi.bypass.level.Car"+id+".State", c.state);
			if (c.state != CarStateEnum.IDLE) {
				outState.putSerializable("com.gutabi.bypass.level.Car"+id+".CoastingGoal", c.driver.toolCoastingGoal);
				outState.putSerializable("com.gutabi.bypass.level.Car"+id+".OrigExitingVertexPos", c.driver.toolOrigExitingVertexPos);
				outState.putDouble("com.gutabi.bypass.level.Car"+id+".CoastingVel", c.coastingVel);
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
	    if (item.getItemId() == R.id.btn_reset_level) {
			BypassWorld.BYPASSWORLD.reset();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
}
