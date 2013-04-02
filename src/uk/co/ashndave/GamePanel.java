/* Copyright David Strachan Buchan 2013
 * This file is part of BounceBallGame.

   BounceBallGame is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   BounceBallGame is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BounceBallGame.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.ashndave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import java.lang.Object;

public class GamePanel extends JPanel implements Runnable {
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 500;
	
	private Thread animator;
	private boolean running = false;
	private boolean gameOver = false;
	private boolean hasStarted = false;
	private long timeStartedWaitingAtStart = -1;
	
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
	
	private int lives = 3;
	
	private volatile static Object BOMBSLOCKOBJECT = new Object();
	
	private MouseListener mouseListenerHandle;
	
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		ballX = 250;
		ballY = 0;
		yEnergy = 0;
		xEnergy = 0;
		
		bombs = new ArrayList<Bomb>();
		this.setFocusable(true);
		this.requestFocusInWindow();
		SetupPanelMouseListener();
		SetupPanelKeyboardListener();
	}
	
	private void SetupPanelKeyboardListener() {
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(gameOver) {
					if(arg0.getKeyChar() == 'y') {
						gameOver = false;
						hasStarted = false;
						lives = 3;
						ballX = 250;
						ballY = 0;
						timeStartedWaitingAtStart = -1;
					}
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}
		});
	}

	private void SetupPanelMouseListener() {
		mouseListenerHandle = new MouseListener() {
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
		};
		this.addMouseListener(mouseListenerHandle);
	}
	
	public void LayBomb(MouseEvent e) {
		Bomb b = new Bomb(e.getX(), e.getY());
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
		
		if(hasStarted)
		{
			dbg.setColor(Color.darkGray);
			dbg.drawString("Lives: " + lives, 20, 20);
			dbg.setColor(Color.red);
			dbg.drawOval(ballX, ballY, SIZE, SIZE);
			
			dbg.setColor(Color.blue);
			synchronized (BOMBSLOCKOBJECT) {
				for(Bomb b : bombs) {
					int currentSize = b.getSize();
					dbg.drawOval(b.getRenderX(currentSize), b.getRenderY(currentSize), currentSize, currentSize);
				}
			}
			
			if(gameOver) {
				gameOverMessage(dbg);
			}
		} else {
			if(timeStartedWaitingAtStart == -1) {
				// we've just started waiting.
				timeStartedWaitingAtStart = System.nanoTime();
			}
			long length = System.nanoTime() - timeStartedWaitingAtStart;
			if(length > 3000000000l) {
				hasStarted = true;
				currentTime = System.nanoTime();
			}
			dbg.setColor(Color.red);
			double rnd = Math.random();
			int x = 200;
			int y = 200;
			if((rnd >=0) && (rnd<0.25)) {
				x = 201;
			}else if((rnd >=0.25) && (rnd<0.5)) {
				x = 199;
			} else if((rnd >=0.5) && (rnd<0.75)) {
				y = 201;
			} else {
				y = 199;
			}
			dbg.drawString("Let's get ready to RUMBLE.", x, y);
			long left = 3 - (length / 1000000000);
			dbg.drawString(left + "", 200, 220);
		}
	}

	private void gameOverMessage(Graphics dbg2) {
		dbg2.setColor(Color.BLACK);
		dbg2.drawString("Game Over", 200, 200);
		dbg2.drawString("Press 'y' to restart", 200, 210);
	}

	private void gameUpdate() {
		if((!gameOver) && (hasStarted)) {
			elapsedTimeInSeconds = (System.nanoTime() - currentTime) / 1000000000f;
			currentTime = System.nanoTime();
			
			yEnergy += (YFORCE * elapsedTimeInSeconds);
			ballY += (yEnergy * elapsedTimeInSeconds);
			
			xEnergy += (XFORCE * elapsedTimeInSeconds);
			ballX += (xEnergy * elapsedTimeInSeconds);
			
			if((lives == 0) || ((yEnergy == 0) && (xEnergy == 0))) {
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
		Point ballMiddle = new Point(ballX + HALFSIZE, ballY + HALFSIZE);
		synchronized (BOMBSLOCKOBJECT) {
			for(Bomb b : bombs) {
				Point bMid = b.getMidPoint();
				float minDistance = (b.getSize() / 2) + 5;
				//Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2))
				float distance = (float) ballMiddle.distance(bMid.getX(), bMid.getY());
				if(distance <= minDistance) {
					
					reactToImpactOfBomb(ballMiddle, b);
					
				}
			}
		}
	}

	private void reactToImpactOfBomb(Point ballMiddle, Bomb bomb) {
		// travelling direction of ball
		float ballTravellingDirectionBeforeImpact = getBallTravellingDirectionBeforeImpact();
		float directionEnergy = (float) Math.sqrt(Math.pow(yEnergy, 2) + Math.pow(xEnergy, 2));
		
		// perpendicular of "line from centre of bomb to centre of ball"
		// opposite
		int opposite = Math.abs(ballMiddle.y - bomb.getMiddleY());
		int adjacent = Math.abs(ballMiddle.x - bomb.getMiddleX());
		float angleOfLineBetweenCentres = getAngleOfLineBetweenCentres(
				opposite, adjacent);
		float perpendicular = (angleOfLineBetweenCentres + 90) % 360;
		
		float angleOfImpact = Math.abs(perpendicular - ballTravellingDirectionBeforeImpact);
		float change = 180 - (angleOfImpact * 2);
		float result = ballTravellingDirectionBeforeImpact + change;
		
		// Reduce directionEnergy at this point
		directionEnergy = directionEnergy * bomb.CurrentEnergy();
		
		yEnergy = (float) (Math.sin(result) * directionEnergy);
		xEnergy = (float) (Math.cos(result) * directionEnergy);
	}

	private float getAngleOfLineBetweenCentres(int opposite, int adjacent) {
		if(adjacent == 0) {
			adjacent = 1;
		}
		float angleOfLineBetweenCentres = (float) Math.toDegrees(Math.atan(opposite / adjacent));
		return angleOfLineBetweenCentres;
	}

	private float getBallTravellingDirectionBeforeImpact() {
		float ballTravellingDirectionBeforeImpact = (float) Math.toDegrees(Math.atan(yEnergy / xEnergy));
		return ballTravellingDirectionBeforeImpact;
	}

	private void checkImpactWall() {
		// If ball is touching the ground and
		// if energy is pointing down (It's positive)
		if((ballY >= (PHEIGHT-10)) && (yEnergy > 0)) {
			lives--;
			if(yEnergy >= minEnergyAtImpact) {
				yEnergy = ((yEnergy * 0.80f) - 80) * -1;
			}
		}
		
		if(((ballX >= (PWIDTH-10)) || (ballX <= 10)) && (Math.abs(xEnergy) > 0)) {
			if(Math.abs(xEnergy) >= minEnergyAtImpact) {
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
