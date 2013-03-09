package uk.co.ashndave;

import java.awt.Point;

public class Bomb {

	private int x, y, size;
	private long born;
	private int renderX, renderY;
	
	private int middleX, middleY; 
	
	public Bomb(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		born = System.nanoTime();
		int halfSize = size / 2;
		renderX = x - halfSize;
		renderY = y - halfSize;
		
		middleX = x + halfSize;
		middleY = y + halfSize;
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
	
	public long Age() {
		return System.nanoTime() - born;
	}
}
