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

import javax.swing.JFrame;

public class Game {

	private static final String BriefLicence1 = "Copyright (C) 2013 David Strachan Buchan.";
	private static final String BriefLicence2 = "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.";
	private static final String BriefLicence3 = "This is free software: you are free to change and redistribute it.";
	private static final String BriefLicence4 = "There is NO WARRANTY, to the extent permitted by law.";
	private static final String BriefLicence5 = "Written by David Strachan Buchan.";
	
	private static final String[] BriefLicence = new String[]{BriefLicence1, BriefLicence2,BriefLicence3,BriefLicence4,BriefLicence5};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		printLicence();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createAndShowGUI();
			}
		});
	}

	private static void printLicence() {
		for(String licence : BriefLicence) {
			System.out.println(licence);
		}
	}

	protected static void createAndShowGUI() {
		JFrame frame = new JFrame("Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new GamePanel());
		frame.pack();
		frame.setVisible(true);
	}

}
