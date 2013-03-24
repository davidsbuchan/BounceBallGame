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

	private int x, y, size;
	private long born;
	private int renderX, renderY;
	
	private int middleX, middleY; 
	private Point midPoint;
	
	public Bomb(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		born = System.nanoTime();
		int halfSize = size / 2;
		renderX = x - halfSize;
		renderY = y - halfSize;
		
		middleX = renderX + halfSize;
		middleY = renderY + halfSize;
		midPoint = new Point(middleX, middleY);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getSize() {
		return size;
	}
	
	public long Born() {
		return born;
	}
	
	public int getRenderX() {
		return renderX;
	}
	
	public int getRenderY() {
		return renderY;
	}
	
	public int getMiddleX() {
		return middleX;
	}
	
	public int getMiddleY() {
		return middleY;
	}
	
	public Point getMidPoint() {
		return midPoint;
	}
	
	public long Age() {
		return System.nanoTime() - born;
	}
}
