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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import org.w3c.dom.NamedNodeMap;

import uk.co.ashndave.game.Renderable;
import uk.co.ashndave.game.Updateable;

import java.lang.Object;

public class GamePanel extends JPanel implements Updateable, Renderable {
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 500;
	
	private boolean gameOver = false;
	private boolean hasStarted = false;
	private long timeStartedWaitingAtStart = -1;
	
	private Graphics dbg;
	private Image dbImage = null;
	
	// middle of the ball
	private double ballX, ballY;
	// middle of previous position of ball
	private Point2D.Double previousPositionOfBall;
	private static final int SIZE = 10;
	private static final int HALFSIZE = SIZE / 2;
	
	private java.util.ArrayList<Bomb> bombs;
	
	// Added energy per second;
	private double YFORCE = 1000;
	private double XFORCE = 0;
	private double yEnergy, xEnergy;
	private long currentTime;
	private double elapsedTimeInSeconds;
	private double minEnergyAtImpact = 60;
	
	private int lives = 3;
	
	private volatile static Object BOMBSLOCKOBJECT = new Object();
	
	private MouseListener mouseListenerHandle;
	
	private int score = 0;
	
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		ballX = 250;
		ballY = 0;
		yEnergy = 0;
		xEnergy = 0;
		previousPositionOfBall = new Point2D.Double();
		bombs = new ArrayList<Bomb>();
		bombs.add(new Bomb(275, 300));
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
						xEnergy = 0;
						yEnergy = 0;
						score = 0;
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
			}

			@Override
			public void mouseExited(MouseEvent e) {
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

	@Override
	public void gameRender() {
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
			dbg.drawString("Score: " + score, 400, 20);
			dbg.setColor(Color.red);
			if(ballY < 0) {
				// ball is too high and off the screen
				// draw a marker at top of screen showing x position
				dbg.drawString("^", (int)ballX, 10);
			}
			else {
				dbg.drawOval((int)ballX - HALFSIZE, (int)ballY - HALFSIZE, SIZE, SIZE);
				//dbg.setColor(Color.green);
				//dbg.drawOval(previousPositionOfBall.x - HALFSIZE, previousPositionOfBall.y - HALFSIZE, SIZE, SIZE);
				//dbg.setColor(Color.red);
				//dbg.drawLine(ballX, ballY, previousPositionOfBall.x, previousPositionOfBall.y);
				//dbg.drawLine(bombs.get(0).getMiddleX(), bombs.get(0).getMiddleY(), ballX, ballY);
				//dbg.drawLine(bombs.get(0).getMiddleX(), bombs.get(0).getMiddleY(), previousPositionOfBall.x, previousPositionOfBall.y);
			}
			
			dbg.setColor(Color.blue);
			synchronized (BOMBSLOCKOBJECT) {
				for(Bomb b : bombs) {
					double currentSize = b.getSize(currentTime);
					dbg.drawOval((int)b.getRenderX(currentSize), (int)b.getRenderY(currentSize), (int)currentSize, (int)currentSize);
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
		
		paintScreen();
	}

	private void gameOverMessage(Graphics dbg2) {
		dbg2.setColor(Color.BLACK);
		dbg2.drawString("Game Over", 200, 200);
		dbg2.drawString("Press 'y' to restart", 200, 210);
	}

	@Override
	public void gameUpdate() {
		if((!gameOver) && (hasStarted)) {
			elapsedTimeInSeconds = (System.nanoTime() - currentTime) / 1000000000f;
			//elapsedTimeInSeconds = 0.25f;
			currentTime = System.nanoTime();
			previousPositionOfBall.x = ballX;
			previousPositionOfBall.y = ballY;
			
			yEnergy += (YFORCE * elapsedTimeInSeconds);
			xEnergy += (XFORCE * elapsedTimeInSeconds);
			// make sure ball isn't going too fast.
			double zEnergy = Math.sqrt(Math.pow(yEnergy,2) + Math.pow(xEnergy, 2));
			if(zEnergy > 800) {
				yEnergy = (yEnergy / zEnergy) * 800;
				xEnergy = (xEnergy / zEnergy) * 800;
			}
			
			ballY += (yEnergy * elapsedTimeInSeconds);			
			ballX += (xEnergy * elapsedTimeInSeconds);
			
			if((lives == 0) || ((yEnergy == 0) && (xEnergy == 0))) {
				gameOver = true;
			}
			synchronized (BOMBSLOCKOBJECT) {
				Iterator<Bomb> i = bombs.iterator();
				while(i.hasNext()) {
					Bomb b = i.next();
					if(b.Age(currentTime) > 1000000000l) {
						i.remove();
					}
				}
			}
			
			checkImpact();
		}
	}

	private void checkImpact() {
		if(!checkImpactWall()) {
			checkImpactBombs();
		}
	}

	private void checkImpactBombs() {
		Point2D.Double ball = new Point2D.Double(ballX, ballY);
		// get sides of triangle previous, bomb, ball
		double previousToBall = previousPositionOfBall.distance(ball);
		for(Bomb bomb : bombs) {
			double previousToBomb = previousPositionOfBall.distance(bomb.getMidPoint());
			double bombToBall = ball.distance(bomb.getMidPoint());
			
			// get angle at previous using the cosine rule
			double cosPREV = (Math.pow(previousToBall, 2) + Math.pow(previousToBomb, 2) - Math.pow(bombToBall, 2)) /
					(2 * previousToBall * previousToBomb);
			double PREV = Math.acos(cosPREV);
			

			// find the height of the triangle where the line previous to ball is the base, and the top is the bomb
			// this will tell us whether the height is less than the radius of the bomb
			// lets find the angle at bomb of the right angle triangle where the two other points are at previous and bomb
			double BOMBofRightAngleTriangle = Math.toRadians(90) - PREV;
			double height = previousToBomb * Math.cos(BOMBofRightAngleTriangle);
			if(!(height <= (bomb.getSize(currentTime)) / 2)) {
				// path from previous to ball does not intersect with bomb (even if path continues to infinite)
				return;
			}
			
			// the path of the ball (if it continues in either direction to infinite) passes close enough (or through)
			// the bomb to make the ball bounce.
			// using the sine rule we want to find out the distance from previous to the point where the line
			// intersects the bomb surface.
			// we know the angle at PREV and we know the radius
			// since we also know the distance from bomb to previous, we can calculate the angle at the
			// impact point using the sine rule.
			double sinIMPACT = previousToBomb * (Math.sin(PREV) / (bomb.getSize(currentTime) / 2));
			double IMPACT = Math.asin(sinIMPACT);
			// impact should always be obtuse. For to be accute the impact is then at the opposite side of
			// the bombs circumference (along the path between previous and ball)
			if(IMPACT < Math.toRadians(90)) {
				IMPACT = Math.toRadians(180) - IMPACT;
			}
			double BOMBfromPreviousToImpactAngle = Math.toRadians(180) - (IMPACT + PREV);
			
			if(bombToBall > (bomb.getSize(currentTime) / 2)) {
				// get original bomb angle using cosine rule.
				// this is to find out if it is accute
				double cosBOMBFromPrevToBall = (Math.pow(bombToBall, 2) + Math.pow(previousToBomb, 2) - Math.pow(previousToBall, 2)) /
						(2 * bombToBall * previousToBomb);
				double BOMBFromPrevToBall = Math.acos(cosBOMBFromPrevToBall);
				if(BOMBFromPrevToBall < Math.toRadians(90)) {
					// ball hasn't reached bomb yet so return
					return;
				}
				// the ball as entirely passed through the bomb.
			}
			
			// using the cosine rule we can find out the distance from previous to impact point.
			//a2 = b2 + c2 - 2bc cos A
			double prevToImpact = Math.sqrt(Math.pow(50, 2) + Math.pow(previousToBomb, 2) - 
					((2 * 50 * previousToBomb) * Math.cos(BOMBfromPreviousToImpactAngle)));
			
			// now we have the distance from the previous to the impact point.
			double proportion = prevToImpact / previousToBall;

			ball.x = previousPositionOfBall.x + ((ball.x - previousPositionOfBall.x) * proportion);
			ball.y = previousPositionOfBall.y + ((ball.y - previousPositionOfBall.y) * proportion);
			
			reactToImpactOfBomb(ball, bomb);
			break;
		}
	}
	
	private void incrementScore() {
		score++;
	}

	private void reactToImpactOfBomb(Point2D.Double ballMiddle, Bomb bomb) {
		// the cosine rule bit
		// point 1 is the centre of the bomb
		// point 2 is the centre of the ball
		// point 3 is where the centre of the ball was.
		double ballPrevPDist = ballMiddle.distance(previousPositionOfBall);
		double bombBallDist = bomb.getMidPoint().distance(ballMiddle);
		double bombPrevPDist = bomb.getMidPoint().distance(previousPositionOfBall);
		
		double cosAngleAtImpact = (Math.pow(ballPrevPDist, 2) + Math.pow(bombBallDist, 2) - Math.pow(bombPrevPDist, 2)) / 
				(2 * ballPrevPDist * bombBallDist);
		double angleAtImpact = Math.acos(cosAngleAtImpact);
		
		int orientation = Line2D.relativeCCW(bomb.getMiddleX(), bomb.getMiddleY(), ballMiddle.x, ballMiddle.y, previousPositionOfBall.x, previousPositionOfBall.y);
		Point2D.Double newEnergy = new Point2D.Double((previousPositionOfBall.x - ballMiddle.x), (previousPositionOfBall.y - ballMiddle.y));
		double rotateAngle = (angleAtImpact * 2) + 3.142;
		double newEnergyX, newEnergyY;

		// apply rotation
		if(orientation >= 0) {
			newEnergyX = (newEnergy.x * Math.cos(rotateAngle)) + (newEnergy.y * Math.sin(rotateAngle));
			newEnergyY = (newEnergy.y * Math.cos(rotateAngle)) - (newEnergy.x * Math.sin(rotateAngle));
		} else {
			newEnergyX = (newEnergy.x * Math.cos(rotateAngle)) - (newEnergy.y * Math.sin(rotateAngle));
			newEnergyY = (newEnergy.y * Math.cos(rotateAngle)) + (newEnergy.x * Math.sin(rotateAngle));			
		}
		
		xEnergy = newEnergyX / elapsedTimeInSeconds;
		yEnergy = newEnergyY / elapsedTimeInSeconds;
	}

	private boolean checkImpactWall() {
		boolean hasBouncedOffWall = false;
		// If ball is touching the ground and
		// if energy is pointing down (It's positive)
		if((ballY >= (PHEIGHT-10)) && (yEnergy > 0)) {
			lives--;
			if(yEnergy >= minEnergyAtImpact) {
				yEnergy = ((yEnergy * 0.80f) - 80) * -1;
				hasBouncedOffWall = true;
			}
		}
		
		if(((ballX >= (PWIDTH-10)) || (ballX <= 10)) && (Math.abs(xEnergy) > 0)) {
			if(Math.abs(xEnergy) >= minEnergyAtImpact) {
				xEnergy = ((xEnergy * 0.8f) - 80) * -1;
				hasBouncedOffWall = true;
			}
		}
		return hasBouncedOffWall;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(dbImage != null) {
			g.drawImage(dbImage, 0, 0, null);
		}
	}
}
