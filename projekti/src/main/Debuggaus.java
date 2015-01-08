package main;
// Tämä luokka on mukana projektissa siltä varalta, jos sattuisi tarvitsemaan debuggausta...
// Koodin lähde: http://www.cs.helsinki.fi/node/61430



import lejos.nxt.comm.RConsole;

public class Debuggaus {
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
