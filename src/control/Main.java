package control;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import gui.Controls;
import wiiremotej.WiiDevice;
import wiiremotej.WiiRemoteJ;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
			System.setProperty("jinput.plugins",
					"net.java.games.input.DirectInputEnvironmentPlugin");
			Controls s = new Controls();
			WiiRemoteJ.findRemotes(s, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
