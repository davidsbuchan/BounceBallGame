package uk.co.ashndave.bounceballtest;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.junit.Test;

import uk.co.ashndave.GamePanel;

public class AngleTest {
		
	@Test
	public void TestBallDirections() throws NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		float[] energies = new float[]{0,10,-10};
		float[] expected = new float[]{90,-90,0,45,-45,-0.0f,-45,45};
		
		Method method = GamePanel.class.getDeclaredMethod("getBallTravellingDirectionBeforeImpact");
		method.setAccessible(true);
		Field xEnergy = GamePanel.class.getDeclaredField("xEnergy");
		xEnergy.setAccessible(true);
		Field yEnergy = GamePanel.class.getDeclaredField("yEnergy");
		yEnergy.setAccessible(true);
		GamePanel gp = new GamePanel();
		int expectedCount = 0;
		for(int x=0; x<energies.length; x++) {
			for(int y=0; y<energies.length; y++) {
				if((energies[x] == 0) && (energies[y] == 0)) {
					continue;
				}
				xEnergy.setFloat(gp, energies[x]);
				yEnergy.setFloat(gp, energies[y]);
				Object result = method.invoke(gp, (Object[])null);
				
				assertEquals(expected[expectedCount], result);
				expectedCount++;
				
			}
		}
	}

	@Test
	public void TestAngleOfLineBetweenCentres() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int[] ballXs = new int[]{15, 20, 25, 20, 15, 10, 5, 10};
		int[] ballYs = new int[]{5, 10, 15, 20, 25, 20, 15, 10};
		float[] expected = new float[]{0,45,90,45,0,45,90,45};
		
		int bombX = 15;
		int bombY = 15;
		
		int opposite, adjacent;
		
		Class[] params = new Class[2];
		params[0] = int.class;
		params[1] = int.class;
		Method method = GamePanel.class.getDeclaredMethod("getAngleOfLineBetweenCentres", params);
		method.setAccessible(true);
		GamePanel gp = new GamePanel();
		int expCounter=0;
		for(int x=0; x<ballXs.length; x++) {
			opposite = Math.abs(ballXs[x] - bombX);
			adjacent = Math.abs(ballYs[x] - bombY);
			Object result = method.invoke(gp, opposite, adjacent);
			
			assertEquals(expected[expCounter], result);
			expCounter++;
		}
	}
	
	
	
}
