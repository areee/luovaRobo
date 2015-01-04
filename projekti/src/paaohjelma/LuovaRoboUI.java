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
	private static final int KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON = 3;
	private static final int KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON = 4;

	private static final Command TAKAISIN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PAAVALIKKOON, Command.BACK, 0);

	private static final Command LOPETA_KOMENTO = new Command(
			KOMENTO_LOPETA_OHJELMA, Command.STOP, 2);

	private static final Command TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON, Command.BACK, 0);

	private static final Command TAKAISIN_VIIVAA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON, Command.BACK, 0);

	// Päävalikkoon liittyviä komponentteja:
	private String nimi = "LuovaRobo"; // oletusnimi
	private List paavalikko = new List("Paavalikko", Choice.IMPLICIT);
	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen " + nimi + "!");
	private Alert lopetusHalytys = new Alert("Lopeta");

	// Piirtotoimintoja varten arvojen syötöt:
	private TextBox syotaYmpyranKaarenPituus = new TextBox("Anna pituus:", "",
			16, TextField.ANY);
	private TextBox syotaYmpyranKulma = new TextBox("Anna kulma:", "", 16,
			TextField.ANY);
	private TextBox syotaViivanPituus = new TextBox("Anna pituus:", "", 16,
			TextField.ANY);

	// Piirtovalikko ympyrän piirtämiseen:
	private List ympyranPiirtovalikko = new List("Ympyran piirtaminen",
			Choice.IMPLICIT);

	// Piirtovalikko viivojen piirtämiseen:
	private List viivanPiirtovalikko = new List("Viivan piirtaminen",
			Choice.IMPLICIT);

	private Display naytto;

	// Robotin moottoreiden alustamiset:
	private NXTRegulatedMotor kynamoottori = Motor.B;

	private MoveController piirtaja = new DifferentialPilot(5.6f, 9.0f,
			Motor.A, Motor.C);

	private ArcRotateMoveController ympyranPiirtaja = new DifferentialPilot(
			5.6f, 9.0f, Motor.A, Motor.C);

	public LuovaRoboUI() {
	}

	// Käynnistysääni:
	private void kaynnistysaanimerkki() {
		Sound.playNote(Sound.FLUTE, 523, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 1047, 500);
	}

	// Lopetusääni:
	private void lopetusaanimerkki() {
		Sound.playNote(Sound.FLUTE, 1047, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 523, 500);
	}

	// Piirtämisen yhteydessä kuultava äänimerkki:
	private void piirtamisaanimerkki() {
		Sound.playNote(Sound.PIANO, 1047, 250);
		Sound.playNote(Sound.PIANO, 784, 125);
		Sound.playNote(Sound.PIANO, 784, 125);
		Sound.playNote(Sound.PIANO, 880, 125);
		Sound.playNote(Sound.PIANO, 784, 125);
		Sound.pause(125);
		Sound.playNote(Sound.PIANO, 988, 125);
		Sound.playNote(Sound.PIANO, 1047, 250);
	}

	public void kaynnista(boolean polling) {

		// Päävalikon toimintojen asettaminen:
		paavalikko = new List("Valitse toiminto", Choice.IMPLICIT);
		paavalikko.append("Piirra ympyra", null);
		paavalikko.append("Piirra viiva", null);
		paavalikko.append("Piirra nelio", null);
		paavalikko.append("Piirra kolmio", null);
		paavalikko.addCommand(LOPETA_KOMENTO);
		paavalikko.setCommandListener(this);
		paavalikko.setTicker(liikkuvaTekstikentta);

		// Ympyrän piirtovalikon toimintojen asettaminen:
		ympyranPiirtovalikko = new List("Syota arvot", Choice.IMPLICIT);
		ympyranPiirtovalikko.append("Syota pituus", null);
		ympyranPiirtovalikko.append("Syota kulma", null);
		ympyranPiirtovalikko.append("Piirra!", null);
		ympyranPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		ympyranPiirtovalikko.setCommandListener(this);

		// Viivan piirtovalikon toimintojen asettaminen:
		viivanPiirtovalikko = new List("Syota arvot", Choice.IMPLICIT);
		viivanPiirtovalikko.append("Syota pituus", null);
		viivanPiirtovalikko.append("Piirra!", null);
		viivanPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		viivanPiirtovalikko.setCommandListener(this);

		// Ympyran kaaren pituus-syötteen määrittely:
		syotaYmpyranKaarenPituus
				.addCommand(TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO);
		syotaYmpyranKaarenPituus.setCommandListener(this);

		// Ympyrän kulma-syötteen määrittely:
		syotaYmpyranKulma.addCommand(TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO);
		syotaYmpyranKulma.setCommandListener(this);

		// Viivan pituus-syötteen määrittely:
		syotaViivanPituus.addCommand(TAKAISIN_VIIVAA_PIIRTAMAAN_KOMENTO);
		syotaViivanPituus.setCommandListener(this);

		// Ohjelman käynnistyksen yhteydessä tapahtuvat toimenpiteet:
		naytto = Display.getDisplay();
		naytto.setCurrent(paavalikko);

		kynamoottori.setSpeed(15);
		piirtaja.setTravelSpeed(5);
		ympyranPiirtaja.setTravelSpeed(5);

		kaynnistysaanimerkki();
		naytto.show(polling);
	}

	public void commandAction(Command c, Displayable d) {
		int toiminto = 0;

		// Päävalikkoon palaaminen:
		if (c.getCommandId() == KOMENTO_TAKAISIN_PAAVALIKKOON) {
			naytto.setCurrent(paavalikko);
		}
		// Ympyrän piirtovalikkoon palaaminen:
		else if (c.getCommandId() == KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON) {
			naytto.setCurrent(ympyranPiirtovalikko);
		}
		// Viivan piirtovalikkoon palaaminen:
		else if (c.getCommandId() == KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON) {
			naytto.setCurrent(viivanPiirtovalikko);
		}

		// Ohjelman lopettamisen varmistaminen:
		else if (c.getCommandId() == KOMENTO_LOPETA_OHJELMA) {
			lopetusHalytys.setType(Alert.ALERT_TYPE_CONFIRMATION);
			lopetusHalytys.setString("Lopetetaanko?");
			lopetusHalytys.setCommandListener(this);
			naytto.setCurrent(lopetusHalytys);
		}

		else {
			// Ohjelman lopettaminen:
			if (d == lopetusHalytys) {
				if (lopetusHalytys.getConfirmation()) {
					lopetusaanimerkki();
					naytto.quit();
				} else {
					naytto.setCurrent(paavalikko);
				}
			}
			// Päävalikon toimintojen käsittely:
			else if (d == paavalikko) {
				List list = (List) naytto.getCurrent();

				// Ympyrän piirto:
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(ympyranPiirtovalikko);
				}
				// Viivan piirto:
				else if (list.getSelectedIndex() == 1) {
					toiminto = 0;
					naytto.setCurrent(viivanPiirtovalikko);
				}
				// Neliön piirto:
				else if (list.getSelectedIndex() == 2) {
					toiminto = 1;
					naytto.setCurrent(viivanPiirtovalikko);
				}
				// Kolmion piirto:
				else if (list.getSelectedIndex() == 3) {
					toiminto = 2;
					naytto.setCurrent(viivanPiirtovalikko);
				}
			}

			// Ympyrän piirtovalikon toimintojen käsittely:
			else if (d == ympyranPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				// Pituus:
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaYmpyranKaarenPituus);
				}
				// Kulma:
				else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syotaYmpyranKulma);
				}
				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 2) {
					piirtamisaanimerkki();
					kynamoottori.rotate(45);

					String pituus = syotaYmpyranKaarenPituus.getText();
					double pituusLukuna = Double.parseDouble(pituus);

					String kulma = syotaYmpyranKulma.getText();
					double kulmaLukuna = Double.parseDouble(kulma);

					ympyranPiirtaja.arc(pituusLukuna, kulmaLukuna);
					kynamoottori.rotate(-45);
					piirtamisaanimerkki();
				}
			}
			// Viivan piirtovalikon toimintojen käsittely:
			else if (d == viivanPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				// Pituus:
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaViivanPituus);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 1) {
					piirtamisaanimerkki();
					kynamoottori.rotate(45);

					String pituus = syotaViivanPituus.getText();
					double pituusLukuna = Double.parseDouble(pituus);

					// Viiva:
					if (toiminto == 0) {
						piirtaja.travel(pituusLukuna);
						kynamoottori.rotate(-45);
						piirtamisaanimerkki();
					}
					// Neliö:
					else if (toiminto == 1) {
						kynamoottori.rotate(-45);
						piirtamisaanimerkki();
					}
					// Kolmio:
					else if (toiminto == 2) {
						kynamoottori.rotate(-45);
						piirtamisaanimerkki();
						piirtamisaanimerkki();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}
}