package paaohjelma;

import lejos.nxt.*;

public class Main {

	public static void main(String[] args) {

		System.out.println("Hei maailma! \nT. Arttu");

		if (Button.LEFT.isPressed()) {
			System.out.println("Vasenta näppäintä painettu.");
		} else if (Button.RIGHT.isPressed()) {
			System.out.println();
		}
		Button.waitForPress();
	}
}
