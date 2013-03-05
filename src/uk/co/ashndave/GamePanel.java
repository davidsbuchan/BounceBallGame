package uk.co.ashndave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

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
	private static final int SIZE = 10;
	
	// Added energy per second;
	private static final float YFORCE = 400;
	private float yEnergy;
	private int yDirection;
	private long currentTime;
	private float elapsedTimeInSeconds;
	private float minEnergyAtImpact = 10;
	
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		ballX = 250;
		ballY = 0;
		yEnergy = 0;
		yDirection = 1;
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
		running = true;
		currentTime = System.nanoTime();
		while(running) {
			gameUpdate();
			gameRender();
			repaint();
			try {
				Thread.sleep(20);
			}catch(InterruptedException ex){}
		}
		System.exit(0);
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
			
			checkImpact();
		}
	}

	private void checkImpact() {
		if(ballY >= (PHEIGHT-10)) {
			if(yEnergy >= minEnergyAtImpact) {
				yEnergy = yEnergy * 0.80f;
				yEnergy = yEnergy * -1;
			}
			else {
				yEnergy = 0;
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
