package main;
// Vanha, käytöstä poistettu main-luokka. Tästä koko projekti lähti...



import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class VanhaMain {

	public static void main(String[] args) {

		NXTRegulatedMotor kynamoottori = Motor.B;
		kynamoottori.setSpeed(15);

		MoveController piirtaja = new DifferentialPilot(5.6f, 9.0f, Motor.A,
				Motor.C);
		piirtaja.setTravelSpeed(5);

		ArcRotateMoveController ympyranPiirtaja = new DifferentialPilot(5.6f,
				9.0f, Motor.A, Motor.C);
		ympyranPiirtaja.setTravelSpeed(5);

		// NavPathController controller = new NavPathController(pilot);

		System.out.println("Hei, olen luova robotti!");
		annaAanimerkkiA();
		kynamoottori.rotate(45);

		while (true) {
			System.out.println("Mita haluat minun piirtavan?");

			String komento = "";

			System.out.println("Piirretaan...");

			if (komento.equals("viiva")) {
				piirraViiva(20, piirtaja);
			}

			else if (komento.equals("ympyra")) {
				piirraYmpyra(20, ympyranPiirtaja);
			}

			else if (komento.equals("nelio")) {
				piirraNelio(20, piirtaja);
			}

			else if (komento.equals("kolmio")) {
				piirraKolmio(20, piirtaja);
			}

			else if (komento.equals("lopeta")) {
				kynamoottori.rotate(-45);
				annaAanimerkkiB();
				break;
			}

			System.out.println("Valmis!");
		}
	}

	private static void annaAanimerkkiA() {
		Sound.playNote(Sound.XYLOPHONE, 523, 500);
		Sound.playNote(Sound.XYLOPHONE, 659, 500);
		Sound.playNote(Sound.XYLOPHONE, 784, 500);
		Sound.playNote(Sound.XYLOPHONE, 1047, 1000);
	}

	private static void annaAanimerkkiB() {
		Sound.playNote(Sound.XYLOPHONE, 1047, 500);
		Sound.playNote(Sound.XYLOPHONE, 784, 500);
		Sound.playNote(Sound.XYLOPHONE, 659, 500);
		Sound.playNote(Sound.XYLOPHONE, 523, 1000);
	}

	private static void piirraViiva(int koko, MoveController piirtaja) {
		piirtaja.travel(-koko);
		piirtaja.travel(koko);
	}

	private static void piirraYmpyra(int sade,
			ArcRotateMoveController ympyranPiirtaja) {
		ympyranPiirtaja.arc(sade, 360); // säde, kulma
	}

	private static void piirraNelio(int koko, MoveController piirtaja) {

	}

	private static void piirraKolmio(int koko, MoveController piirtaja) {

	}

}
