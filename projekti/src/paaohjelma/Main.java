package paaohjelma;

import lejos.nxt.*;

public class Main {

	public static void main(String[] args) {
		//Testiohjelma, jolla tutustun NXT:n ja Eclipsen "vuorovaikutukseen"
		//Teen tähän pohjaan myös lopullisen työni
		
		System.out.println("Hei maailma! \nT. Arttu");

		if (Button.LEFT.isPressed()) {
			System.out.println("Vasenta näppäintä painettu.");
		} else if (Button.RIGHT.isPressed()) {
			System.out.println("Oikeaa näppäintä painettu.");
		} else if (Button.ENTER.isPressed()) {
			System.out.println("Enteriä painettu.");
		} else {
			Button.waitForPress(); //tarkoitus olisi, että returnia painamalla lopettaa, ei toimi vielä oikein
		}
	}
}
