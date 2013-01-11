package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.gutabi.deadlock.math.Point;
import com.gutabi.deadlock.menu.MainMenu;
import com.gutabi.deadlock.ui.InputEvent;
import com.gutabi.deadlock.ui.KeyListener;
import com.gutabi.deadlock.ui.MotionListener;

public class MainActivity extends Activity implements OnTouchListener, OnGestureListener {
	
	private GestureDetector gDetector; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
//		this.set
		gDetector = new GestureDetector(this);
		
		MainView v = (MainView)findViewById(R.id.deadlock);
		
		v.setOnTouchListener(this);
		
		APP.MENUPANEL_WIDTH = 480;
		APP.MENUPANEL_HEIGHT = 800;
		
		APP.QUADRANTEDITORPANEL_WIDTH = 480;
		APP.QUADRANTEDITORPANEL_HEIGHT = 800;
		
		APP.WORLDPANEL_WIDTH = 380;
		APP.WORLDPANEL_HEIGHT = 800;
		
		APP.CONTROLPANEL_WIDTH = 100;
		APP.CONTROLPANEL_HEIGHT = 800;
		
		APP.MENU_WIDTH = 480;
		APP.MENU_HEIGHT = 800;
		
		APP.TITLE_CENTER_Y = 200;
		APP.MENU_CENTER_Y = 450;
		APP.COPYRIGHT_CENTER_Y = 668;
		
		
		PlatformImpl platform = new PlatformImpl(getResources(), v);
		APP.platform = platform;
		try {
			APP.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainMenu s = new MainMenu();
		
		platform.container = v;
		
		kl = s;
		ml = s.contentPane;
		platform.setupScreen(s.contentPane.cp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
		
	}
	
	
	KeyListener kl;
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	kl.downKey();
	            break;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	kl.upKey();
	            break;
	        case KeyEvent.KEYCODE_DPAD_CENTER:
	        	kl.enterKey();
	            break;
	    }
	    return true;
	}
	
	
	public boolean onTouchEvent(MotionEvent e) {
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			
			InputEvent ev = new InputEvent(new Point(e.getX(), e.getY()));
			
			ml.pressed(ev);
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			
			InputEvent ev = new InputEvent(new Point(e.getX(), e.getY()));
			
			ml.dragged(ev);
			break;
		}
		case MotionEvent.ACTION_UP: {
	
			InputEvent ev = new InputEvent(new Point(e.getX(), e.getY()));
			
			ml.released(ev);
			break;
		}
		}
		
		return gDetector.onTouchEvent(e);
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	MotionListener ml;

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		InputEvent ev = new InputEvent(new Point(e.getX(), e.getY()));
		
		ml.clicked(ev);
		
		return true;
	}
	
}
