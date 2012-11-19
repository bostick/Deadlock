package com.gutabi.deadlock.controller;

import static com.gutabi.deadlock.controller.DeadlockController.CONTROLLER;
import static com.gutabi.deadlock.view.DeadlockView.VIEW;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class KeyboardController implements KeyListener {
	
	public void init() {
		
		VIEW.canvas.addKeyListener(this);
		
	}
	
	@SuppressWarnings("serial")
	public Action deleteKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.deleteKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			CONTROLLER.renderAndPaint();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action insertKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.insertKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			CONTROLLER.renderAndPaint();
			
		}
	};
	
	@SuppressWarnings("serial")
	public Action qKeyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent blah) {
			
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.qKey();
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_INSERT) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.insertKey();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.deleteKey();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.escKey();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_1) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.d1Key();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.d2Key();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			try {
				CONTROLLER.queueAndWait(new Runnable() {
					public void run() {
						CONTROLLER.d3Key();
					}
				});
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
//	@SuppressWarnings("serial")
//	public Action tabKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.tabKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
//	
//	@SuppressWarnings("serial")
//	public Action undoKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.undoKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
//	
//	@SuppressWarnings("serial")
//	public Action redoKeyAction = new AbstractAction() {
//		@Override
//		public void actionPerformed(ActionEvent blah) {
//			
//			CONTROLLER.queue(new Runnable() {
//				public void run() {
//					CONTROLLER.redoKey();
//					MODEL.world.renderBackground();
//					VIEW.repaint();
//				}
//			});
//			
//		}
//	};
	
}
