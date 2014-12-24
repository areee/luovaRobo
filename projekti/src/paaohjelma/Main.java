package paaohjelma;

import lejos.addon.gps.SimpleGPS;
import lejos.nxt.*;
import lejos.util.PilotProps;
import lejos.robotics.navigation.*;

public class Main {

	public static void main(String[] args) {
		
		// Moottoreiden toiminnan testailua:
		NXTRegulatedMotor motorA = Motor.A;

		motorA.setSpeed(150);

		motorA.rotate(360);

		NXTRegulatedMotor motorC = Motor.C;

		motorC.setSpeed(150);

		motorC.rotate(360);

		motorA.rotateTo(-1150);

		motorC.rotateTo(-1150);
		
		// Ajoneuvon luominen (testailua myöskin):
		// Kesken...
		MoveController pilot = new DifferentialPilot(5.6f, 9.5f, motorA, motorC);
		
		NavPathController controller = new NavPathController(pilot);

	}
}
