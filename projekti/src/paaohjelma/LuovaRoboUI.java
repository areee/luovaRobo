// Pohjautuu esimerkkien LCDUI:iin, jota lähdin muokkaamaan haluamakseni

package paaohjelma;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MoveController;

public class LuovaRoboUI implements CommandListener {
	private static final int KOMENTO_TAKAISIN_PAAVALIKKOON = 1;
	private static final int KOMENTO_LOPETA_OHJELMA = 2;
	private static final int KOMENTO_TAKAISIN_PIIRTOVALIKKOON = 3;

	private static final Command TAKAISIN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PAAVALIKKOON, Command.BACK, 0);
	private static final Command LOPETA_KOMENTO = new Command(
			KOMENTO_LOPETA_OHJELMA, Command.STOP, 2);
	private static final Command TAKAISIN_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PIIRTOVALIKKOON, Command.BACK, 0);

	// Päävalikkoon liittyviä komponentteja:
	private String nimi = "LuovaRobo"; // oletusnimi
	private int valittuToiminto;
	private List paavalikko = new List("Paavalikko", Choice.IMPLICIT);
	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen " + nimi + "!");
	private Alert lopetusHalytys = new Alert("Lopeta");

	// Piirtotoimintoa varten arvojen syöttö:
	private TextBox syotaPituus = new TextBox("Anna pituus:", "", 16,
			TextField.ANY);
	private TextBox syotaKulma = new TextBox("Anna kulma:", "", 16,
			TextField.ANY);

	// // Mahdollisuus antaa oma nimi robolle (ei toimi):
	// private TextBox nimenVaihto = new TextBox("Anna uusi nimi:", nimi, 16,
	// TextField.ANY);

	// Piirtovalikko piirtämiseen:
	private List piirtovalikko = new List("Piirroksen piirtaminen",
			Choice.IMPLICIT);

	private Display naytto;

	private NXTRegulatedMotor kynamoottori = Motor.B;

	private MoveController robonPyorat = new DifferentialPilot(5.6f, 9.0f,
			Motor.A, Motor.C);

	private ArcRotateMoveController ympyranPiirtaja = new DifferentialPilot(
			5.6f, 9.0f, Motor.A, Motor.C);

	public LuovaRoboUI() {
	}

	// käynnistysääni:
	private void annaAanimerkkiA() {
		Sound.playNote(Sound.FLUTE, 523, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 1047, 500);
	}

	// lopetusääni:
	private void annaAanimerkkiB() {
		Sound.playNote(Sound.FLUTE, 1047, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 523, 500);
	}

	public void kaynnista(boolean polling) {

		// päävalikon toiminnot:
		paavalikko = new List("Valitse toiminto", Choice.IMPLICIT);
		paavalikko.append("Piirra ympyra", null);
		paavalikko.append("Piirra viiva", null);
		paavalikko.append("Piirra nelio", null);
		paavalikko.append("Piirra kolmio", null);
		// paavalikko.append("Vaihda nimi", null); // ei toimi
		paavalikko.addCommand(LOPETA_KOMENTO);
		paavalikko.setCommandListener(this);
		paavalikko.setTicker(liikkuvaTekstikentta);

		// piirtovalikon toiminnot:
		piirtovalikko = new List("Syota arvot", Choice.IMPLICIT);
		if (valittuToiminto == 0) {
			piirtovalikko.append("Syota pituus", null);
			piirtovalikko.append("Syota kulma", null);
			piirtovalikko.append("Piirra!", null);
			piirtovalikko.addCommand(TAKAISIN_KOMENTO);
			piirtovalikko.setCommandListener(this);
		}
		
		else if (valittuToiminto == 1) {
			piirtovalikko.append("Syota pituus", null);
			piirtovalikko.append("Piirra!", null);
			piirtovalikko.addCommand(TAKAISIN_KOMENTO);
			piirtovalikko.setCommandListener(this);
		}
		
		else if (valittuToiminto == 2) {
			piirtovalikko.append("Syota pituus", null);
			piirtovalikko.append("Syota kulma", null);
			piirtovalikko.append("Piirra!", null);
			piirtovalikko.addCommand(TAKAISIN_KOMENTO);
			piirtovalikko.setCommandListener(this);
		}
		
		else if (valittuToiminto == 3) {
			piirtovalikko.append("Syota pituus", null);
			piirtovalikko.append("Syota kulma", null);
			piirtovalikko.append("Piirra!", null);
			piirtovalikko.addCommand(TAKAISIN_KOMENTO);
			piirtovalikko.setCommandListener(this);
		}

		piirtovalikko.append("Syota pituus", null);
		piirtovalikko.append("Syota kulma", null);
		piirtovalikko.append("Piirra!", null);
		piirtovalikko.addCommand(TAKAISIN_KOMENTO);
		piirtovalikko.setCommandListener(this);

		// // nimen vaihto (ei toimi vielä):
		// nimenVaihto.addCommand(TAKAISIN_KOMENTO);
		// nimenVaihto.setCommandListener(this);
		// nimi = nimenVaihto.getText();

		// syötteen pituuden määrittely:
		syotaPituus.addCommand(TAKAISIN_PIIRTAMAAN_KOMENTO);
		syotaPituus.setCommandListener(this);

		// syötteen kulman määrittely:
		syotaKulma.addCommand(TAKAISIN_PIIRTAMAAN_KOMENTO);
		syotaKulma.setCommandListener(this);

		// ohjelman käynnistyksen yhteydessä tapahtuvat toimenpiteet:
		naytto = Display.getDisplay();
		naytto.setCurrent(paavalikko);

		kynamoottori.setSpeed(15);
		robonPyorat.setTravelSpeed(5);
		ympyranPiirtaja.setTravelSpeed(5);

		annaAanimerkkiA();
		naytto.show(polling);
	}

	public void commandAction(Command c, Displayable d) {
		// päävalikkoon palaaminen:
		if (c.getCommandId() == KOMENTO_TAKAISIN_PAAVALIKKOON) {
			naytto.setCurrent(paavalikko);
		}
		// piirtovalikkoon palaaminen:
		else if (c.getCommandId() == KOMENTO_TAKAISIN_PIIRTOVALIKKOON) {
			naytto.setCurrent(piirtovalikko);
		}
		// ohjelman lopettamisen varmistaminen:
		else if (c.getCommandId() == KOMENTO_LOPETA_OHJELMA) {
			lopetusHalytys.setType(Alert.ALERT_TYPE_CONFIRMATION);
			lopetusHalytys.setString("Lopetetaanko?");
			lopetusHalytys.setCommandListener(this);
			naytto.setCurrent(lopetusHalytys);
		}

		else {
			// ohjelman lopettaminen:
			if (d == lopetusHalytys) {
				if (lopetusHalytys.getConfirmation()) {
					annaAanimerkkiB();
					naytto.quit();
				} else {
					naytto.setCurrent(paavalikko);
				}
			}
			// päävalikon toimintojen käsittely:
			else if (d == paavalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					valittuToiminto = 0;
					naytto.setCurrent(piirtovalikko);
				}

				else if (list.getSelectedIndex() == 1) {
					valittuToiminto = 1;
					naytto.setCurrent(piirtovalikko);
				}

				else if (list.getSelectedIndex() == 2) {
					valittuToiminto = 2;
					naytto.setCurrent(piirtovalikko);
				}

				else if (list.getSelectedIndex() == 3) {
					valittuToiminto = 3;
					naytto.setCurrent(piirtovalikko);
				}

				// // nimen vaihto (ei toimi vielä):
				// else if (list.getSelectedIndex() == 4) {
				// naytto.setCurrent(nimenVaihto);
				// }
			}

			// ympyrän piirtovalikon toimintojen käsittely:
			else if (d == piirtovalikko) {
				List list = (List) naytto.getCurrent();
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaPituus);
				} else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syotaKulma);
				} else if (list.getSelectedIndex() == 2) {

					// ympyrän piirtotoiminto:
					kynamoottori.rotate(45);

					String pituus = syotaPituus.getText();
					double pituusLukuna = Double.parseDouble(pituus);

					String kulma = syotaKulma.getText();
					double kulmaLukuna = Double.parseDouble(kulma);

					ympyranPiirtaja.arc(pituusLukuna, kulmaLukuna);
					kynamoottori.rotate(-45);
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}
}