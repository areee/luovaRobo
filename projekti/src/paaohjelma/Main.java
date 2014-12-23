package paaohjelma;

import javax.microedition.lcdui.Screen;

import lejos.nxt.*;

public class Main {

	public static void main(String[] args) {
		// Testiohjelma, jolla tutustun NXT:n ja Eclipsen "vuorovaikutukseen"
		// Teen tähän pohjaan myös lopullisen työni

		System.out.println("Paina jotakin" + "\nrobon painikkeista.");

		while (true) {
			int aika = Button.waitForPress(5000);
			System.out.println(aika);
			if (aika == 2) {
				System.out.println("Vasenta nappainta painettu.");
			} else if (aika == 4) {
				System.out.println("Oikeaa nappainta painettu.");
			} else if (aika == 1) {
				System.out.println("Enteria painettu.");
			} else if (aika == 8) {
				System.out.println("Escapea painettu.");
			} else if (aika == 0) {
				System.out.println(aika);
				break;
			}
		}

	}
}
