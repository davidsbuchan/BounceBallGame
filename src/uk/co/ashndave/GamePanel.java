package uk.co.ashndave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import java.lang.Object;

public class GamePanel extends JPanel implements Runnable {
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 500;
	
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;
	
	private Graphics dbg;
	private Image dbImage = null;
	
	private int ballX, ballY;
	private static final int SIZE = 10;
	private static final int HALFSIZE = SIZE / 2;
	
	private java.util.ArrayList<Bomb> bombs;
	
	// Added energy per second;
	private float YFORCE = 600;
	private float XFORCE = 0;
	private float yEnergy, xEnergy;
	private long currentTime;
	private float elapsedTimeInSeconds;
	private float minEnergyAtImpact = 60;
	
	private volatile static Object BOMBSLOCKOBJECT = new Object();
	
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		ballX = 250;
		ballY = 0;
		yEnergy = 0;
		xEnergy = 0;
		
		bombs = new ArrayList<Bomb>();
		
		SetupPanelMouseListener();
		
		
	}
	
	private void SetupPanelMouseListener() {
		
		this.addMouseListener(new MouseListener() {
				
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				LayBomb(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
	}
	
	public void LayBomb(MouseEvent e) {
		Bomb b = new Bomb(e.getX(), e.getY(), 100);
		synchronized (BOMBSLOCKOBJECT) {
			bombs.add(b);
		}
	}
	

	@Override
	public void addNotify() {
		// TODO Auto-generated method stub
		super.addNotify();
		startGame();
	}

	private void startGame() {
		
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}
	
	@Override
	public void run() {
		long beforeTime, timeDiff, sleepTime;
		beforeTime = System.nanoTime();
		long period = 10000000;
		
		running = true;
		currentTime = System.nanoTime();
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen();

			timeDiff = System.nanoTime() - beforeTime;
			sleepTime = period - timeDiff;
			if(sleepTime <= 0) {
				sleepTime = 0;
			}
			
			try {
				Thread.sleep(sleepTime / 1000000);
			}catch(InterruptedException ex){}
			beforeTime = System.nanoTime();
		}
		System.exit(0);
	}

	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if((g != null) && (dbImage != null)) {
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}catch(Exception ex) {
			System.out.println("Failed on paintScreen: " + ex);
		}
	}

	private void gameRender() {
		if(dbImage == null) {
			dbImage = createImage(PWIDTH, PHEIGHT);
			if(dbImage == null){
				System.out.println("dbImage is still null");
				return;
			}
			else {
				dbg = dbImage.getGraphics();
			}
		}
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		
		dbg.setColor(Color.red);
		dbg.drawOval(ballX, ballY, SIZE, SIZE);
		
		dbg.setColor(Color.blue);
		synchronized (BOMBSLOCKOBJECT) {
			for(Bomb b : bombs) {
				dbg.drawOval(b.getRenderX(), b.getRenderY(), b.getSize(), b.getSize());
			}			
		}
		
		if(gameOver) {
			gameOverMessage(dbg);
		}
	}

	private void gameOverMessage(Graphics dbg2) {
		dbg2.drawString("Game Over", 0, 0);
	}

	private void gameUpdate() {
		if(!gameOver) {
			elapsedTimeInSeconds = (System.nanoTime() - currentTime) / 1000000000f;
			currentTime = System.nanoTime();
			
			yEnergy += (YFORCE * elapsedTimeInSeconds);
			ballY += (yEnergy * elapsedTimeInSeconds);
			
			xEnergy += (XFORCE * elapsedTimeInSeconds);
			ballX += (xEnergy * elapsedTimeInSeconds);
			
			if((yEnergy == 0) && (xEnergy == 0)) {
				gameOver = true;
			}
			synchronized (BOMBSLOCKOBJECT) {
				Iterator<Bomb> i = bombs.iterator();
				while(i.hasNext()) {
					Bomb b = i.next();
					if(b.Age() > 1000000000l) {
						i.remove();
					}
				}
			}
			
			checkImpact();
		}
	}

	private void checkImpact() {
		checkImpactWall();
		checkImpactBombs();
	}

	private void checkImpactBombs() {
		synchronized (BOMBSLOCKOBJECT) {
			Point ballMiddle = new Point(ballX + HALFSIZE, ballY + HALFSIZE);
			for(Bomb b : bombs) {
				
			}
		}
	}

	private void checkImpactWall() {
		// If ball is touching the ground and
		// if energy is pointing down (It's positive)
		if((ballY >= (PHEIGHT-10)) && (yEnergy > 0)) {
			if(yEnergy >= minEnergyAtImpact) {
				yEnergy = ((yEnergy * 0.80f) - 80) * -1;
			}
		}
		if((ballX >= (PWIDTH-10)) && (xEnergy > 0)) {
			if(xEnergy >= minEnergyAtImpact) {
				xEnergy = ((xEnergy * 0.8f) - 80) * -1;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		if(dbImage != null) {
			g.drawImage(dbImage, 0, 0, null);
		}
	}
}
