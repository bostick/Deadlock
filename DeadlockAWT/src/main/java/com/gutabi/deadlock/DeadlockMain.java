package com.gutabi.deadlock;

import static com.gutabi.deadlock.DeadlockApplication.APP;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.gutabi.deadlock.menu.MainMenuScreen;
import com.gutabi.deadlock.ui.WindowInfo;

public class DeadlockMain  {
	
	static Logger logger = Logger.getLogger("deadlock");
	
	static void createAndShowGUI() throws Exception {
		
		APP.WINDOW_WIDTH = 480;
		APP.WINDOW_HEIGHT = 854;
		
		APP.MENUPANEL_WIDTH = APP.WINDOW_WIDTH;
		APP.MENUPANEL_HEIGHT = APP.WINDOW_HEIGHT;
		
		APP.QUADRANTEDITORPANEL_WIDTH = APP.WINDOW_WIDTH;
		APP.QUADRANTEDITORPANEL_HEIGHT = APP.WINDOW_HEIGHT;
		
		APP.CONTROLPANEL_WIDTH = 200;
		APP.CONTROLPANEL_HEIGHT = 854;
		
		APP.WORLDPANEL_WIDTH = APP.WINDOW_WIDTH;
		APP.WORLDPANEL_HEIGHT = APP.WINDOW_HEIGHT;
		
		
		PlatformImpl platform = new PlatformImpl();
		APP.platform = platform;
		APP.init();
		
		JFrame newFrame;
		newFrame = new JFrame("Deadlock Viewer");
		newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		newFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        APP.exit();
		    }
		});
		
		newFrame.setLocation((int)WindowInfo.windowDim().width/2 - APP.WINDOW_WIDTH/2, 0);
		
		platform.appContainer = newFrame;
		
		
		
		
		newFrame = new JFrame("Debug Control Panel");
		newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		newFrame.setLocation((int)WindowInfo.windowDim().width/2 + APP.WINDOW_WIDTH/2 + 100, 0);
		
		platform.debuggerContainer = newFrame;
		
		
		MainMenuScreen s = new MainMenuScreen();
		APP.appScreen = s;
		
		APP.platform.setupAppScreen(s.contentPane.pcp);
		
		s.postDisplay();
		s.contentPane.panel.render();
		s.contentPane.repaint();
		
		APP.platform.showAppScreen();
		
	}
	
	public void setupFrame() {
		
		
	}
	
	public static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable t) {
			logger.error("Error in thread " + thread.getName() + ": " + t.getMessage(), t);
		}
	};
	
	public static void main(final String[] args) throws UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		
	}
	
}
