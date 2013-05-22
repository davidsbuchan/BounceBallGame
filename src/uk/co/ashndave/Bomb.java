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
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Bomb {
	
	public static final float INITIALENERGY = 500000000;
		
	private double x, y;
	private long born;
	private Point2D.Double midPoint;
	private double previousImpactDistance = java.lang.Double.MAX_VALUE;
	
	public Bomb(double x, double y) {
		this.x = x;
		this.y = y;
		midPoint = new Point2D.Double(x, y);
		born = System.nanoTime();
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getSize(long unifiedNowTime) {
		double currentSize = Age(unifiedNowTime) / 10000000;
		return currentSize;
	}
	
	public long Born() {
		return born;
	}
	
	public double getRenderX(double currentSize) {
		double renderX = x - (currentSize / 2);
		return renderX;
	}
	
	public double getRenderY(double currentSize) {
		double renderY = y - (currentSize / 2);
		return renderY;
	}
	
	public double getMiddleX() {
		return x;
	}
	
	public double getMiddleY() {
		return y;
	}
	
	public Point2D.Double getMidPoint() {
		return midPoint;
	}
	
	public double getPreviousImpactDistance() {
		return previousImpactDistance;
	}
	public void setPreviousImpactDistance(double previousImpactDistance) {
		this.previousImpactDistance = previousImpactDistance;
	}
	public long Age(long unifiedNowTime) {
		return unifiedNowTime - born;
	}
	
	public float CurrentEnergy(long unifiedNowTime) {
		float currentEnergy = INITIALENERGY / Age(unifiedNowTime);
		currentEnergy = (currentEnergy > 2) ? 2 : currentEnergy;
		return currentEnergy;
	}
}
