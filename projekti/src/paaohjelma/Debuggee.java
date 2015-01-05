package paaohjelma;

import lejos.nxt.comm.RConsole;

public class Debuggee {
	public static void main(String[] args) {
		// aloita USB-yhteyden kuuntelu
		RConsole.open();

		// … varsinainen koodi …
		RConsole.print("Tämä viesti näkyy jo etäkonsolissa.");

		/* KOODIA */

		// sulje yhteys
		RConsole.close();
	}
}
