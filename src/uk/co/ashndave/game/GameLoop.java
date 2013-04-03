package uk.co.ashndave.game;

public class GameLoop implements Runnable {
	
	private Updateable game;
	private Renderable renderer;
	
	private boolean running = false;
	
	public GameLoop(Updateable game, Renderable renderer) {
		this.game = game;
		this.renderer = renderer;
	}
	
	@Override
	public void run() {
		long beforeTime, timeDiff, sleepTime;
		beforeTime = System.nanoTime();
		long period = 10000000;
		
		running = true;
		while(running) {
			game.gameUpdate();
			renderer.gameRender();

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

}
