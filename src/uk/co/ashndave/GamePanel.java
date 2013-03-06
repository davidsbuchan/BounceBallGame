package uk.co.ashndave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 400;
	
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;
	
	private Graphics dbg;
	private Image dbImage = null;
	
	private int ballX, ballY;
	private float yMovement;
	private static final int SIZE = 10;
	
	// Added energy per second;
	private static final float YFORCE = 600;
	private float yEnergy;
	private long currentTime;
	private float elapsedTimeInSeconds;
	private float minEnergyAtImpact = 60;
	
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		ballX = 250;
		ballY = 0;
		yMovement = 0;
		yEnergy = 0;
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
			
			//System.out.println(currentTime + ", " + yEnergy + ", " + ballY);
			
			checkImpact();
		}
	}

	private void checkImpact() {
		// If ball is touching the ground and
		// if energy is pointing down (It's positive)
		if((ballY >= (PHEIGHT-10)) && (yEnergy > 0)) {
			if(yEnergy >= minEnergyAtImpact) {
				//System.out.println(yEnergy);
				yEnergy = ((yEnergy * 0.80f) - 80) * -1;
				//System.out.println(yEnergy);
			}
			else {
				yEnergy = 0;
				gameOver = true;
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
