package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.gutabi.deadlock.menu.MainMenu;

public class MainActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		
		MainView v = (MainView)findViewById(R.id.deadlock);
		
		APP.MENUPANEL_WIDTH = 480;
		APP.MENUPANEL_HEIGHT = 800;
		
		APP.QUADRANTEDITORPANEL_WIDTH = 1584;
		APP.QUADRANTEDITORPANEL_HEIGHT = 822;
		
		APP.CONTROLPANEL_WIDTH = 200;
		APP.CONTROLPANEL_HEIGHT = 822;
		
		APP.WORLDPANEL_WIDTH = 1384;
		APP.WORLDPANEL_HEIGHT = 822;
		
		
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
		
		platform.setupScreen(s.contentPane.cp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
		
	}
	
}
