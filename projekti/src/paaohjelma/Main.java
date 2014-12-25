package paaohjelma;

import lejos.addon.gps.SimpleGPS;
import lejos.nxt.*;
import lejos.util.PilotProps;
import lejos.robotics.navigation.*;

public class Main {

	public static void main(String[] args) {

		// Moottoreiden toiminnan testailua:
		NXTRegulatedMotor vasenPyora = Motor.A;
		vasenPyora.setSpeed(150);

		NXTRegulatedMotor oikeaPyora = Motor.C;
		oikeaPyora.setSpeed(150);

		NXTRegulatedMotor kynamoottori = Motor.B;
		kynamoottori.setSpeed(15);

//		vasenPyora.rotateTo(-150);
//
//		oikeaPyora.rotateTo(-150);

//		kynamoottori.rotate(90);
//		kynamoottori.rotate(-90);

		// Ajoneuvon luominen (testailua myöskin):
		// Kesken...
		MoveController pilot = new DifferentialPilot(5.6f, 9.0f, Motor.A, Motor.C);

		NavPathController controller = new NavPathController(pilot);
		
		pilot.travel(-50);
		
		kynamoottori.rotate(90);
		kynamoottori.rotate(-90);
		
		pilot.travel(50);

	}
}
