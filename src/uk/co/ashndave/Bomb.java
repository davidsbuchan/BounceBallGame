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

import java.awt.Point;

public class Bomb {
	
	public static final float INITIALENERGY = 500000000;
		
	private int x, y;
	private long born;
	private Point midPoint;
	
	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;
		midPoint = new Point(x, y);
		born = System.nanoTime();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getSize() {
		int currentSize = (int)Age() / 10000000;
		return currentSize;
	}
	
	public long Born() {
		return born;
	}
	
	public int getRenderX(int currentSize) {
		int renderX = x - (currentSize / 2);
		return renderX;
	}
	
	public int getRenderY(int currentSize) {
		int renderY = y - (currentSize / 2);
		return renderY;
	}
	
	public int getMiddleX() {
		return x;
	}
	
	public int getMiddleY() {
		return y;
	}
	
	public Point getMidPoint() {
		return midPoint;
	}
	
	public long Age() {
		return System.nanoTime() - born;
	}
	
	public float CurrentEnergy() {
		float currentEnergy = INITIALENERGY / Age();
		return currentEnergy;
	}
}
