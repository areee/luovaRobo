package paaohjelma;

import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class Main {

	public static void main(String[] args) {

		NXTRegulatedMotor kynamoottori = Motor.B;
		kynamoottori.setSpeed(15);

		MoveController pilot = new DifferentialPilot(5.6f, 9.0f, Motor.A,
				Motor.C);
		pilot.setTravelSpeed(5);

		// NavPathController controller = new NavPathController(pilot);
		
		System.out.println("Hei, olen luova robotti!");
		Sound.playNote(Sound.XYLOPHONE, 523, 500);
		Sound.playNote(Sound.XYLOPHONE, 659, 500);
		Sound.playNote(Sound.XYLOPHONE, 784, 500);
		Sound.playNote(Sound.XYLOPHONE, 1047, 500);
		
		kynamoottori.rotate(45);
		
		System.out.println("Piirretaan...");
		
		pilot.travel(-20);
		pilot.travel(20);
		
		System.out.println("Valmis!");
		Sound.playNote(Sound.XYLOPHONE, 523, 500);
		Sound.playNote(Sound.XYLOPHONE, 659, 500);
		Sound.playNote(Sound.XYLOPHONE, 784, 500);
		Sound.playNote(Sound.XYLOPHONE, 1047, 500);
		kynamoottori.rotate(-45);

		// ArcRotateMoveController controller2 = new DifferentialPilot(5.6f,
		// 9.0f, Motor.A, Motor.C);
		// controller2.arc(5, 360);

	}
}
