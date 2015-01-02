// Pohjautuu esimerkkien LCDUI:iin, jota lähdin muokkaamaan haluamakseni

package paaohjelma;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
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
	private static final int KOMENTO_TAKAISIN_PIIRTOVALIKKOON = 3; // testiä

	private static final Command TAKAISIN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PAAVALIKKOON, Command.BACK, 0);
	private static final Command LOPETA_KOMENTO = new Command(
			KOMENTO_LOPETA_OHJELMA, Command.STOP, 2);
	private static final Command TAKAISIN_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PIIRTOVALIKKOON, Command.BACK, 0); // testiä

	// Päävalikkoon liittyviä komponentteja:
	private List paavalikko = new List("Paavalikko", Choice.IMPLICIT);
	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen luovaRobo!");
	private Alert lopetusHalytys = new Alert("Lopeta");

	// testiä:
	private TextBox syotaPituus = new TextBox("Anna pituus:", "", 16,
			TextField.ANY);
	private TextBox syotaKulma = new TextBox("Anna kulma:", "", 16,
			TextField.ANY);

	private Form lomake = new Form("Piirroksen tiedot"); // viivalle tms.
	private Form lomake2 = new Form("Piirroksen tiedot"); // ympyrälle tms.

	// testiä:
	private List piirtovalikko = new List("Piirroksen tiedot", Choice.IMPLICIT);

	private TextField pituus = new TextField("Anna pituus:", "0", 16,
			TextField.ANY);
	private TextField kulmanSuuruus = new TextField("Anna kulma:", "0", 16,
			TextField.ANY);

	private ChoiceGroup valinta = new ChoiceGroup("*********",
			Choice.TEXT_WRAP_DEFAULT);

	private Display naytto;

	private NXTRegulatedMotor kynamoottori = Motor.B;

	private MoveController piirtaja = new DifferentialPilot(5.6f, 9.0f,
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
		// paavalikko.append("Piirra viiva", null);
		// paavalikko.append("Piirra nelio", null);
		// paavalikko.append("Piirra kolmio", null);
		paavalikko.addCommand(LOPETA_KOMENTO);
		paavalikko.setCommandListener(this);
		paavalikko.setTicker(liikkuvaTekstikentta);

		// testiä:
		piirtovalikko = new List("Syota arvot", Choice.IMPLICIT);
		piirtovalikko.append("Liikuta kynaa", null);
		piirtovalikko.append("Syota pituus", null);
		piirtovalikko.append("Syota kulma", null);
		piirtovalikko.append("Piirra!", null);
		piirtovalikko.addCommand(TAKAISIN_KOMENTO);
		piirtovalikko.setCommandListener(this);

		// syötteen pituuden määrittely (testiä):
		syotaPituus.addCommand(TAKAISIN_PIIRTAMAAN_KOMENTO);
		syotaPituus.setCommandListener(this);

		// syötteen kulman määrittely (testiä):
		syotaKulma.addCommand(TAKAISIN_PIIRTAMAAN_KOMENTO);
		syotaKulma.setCommandListener(this);

		// lomakkeen määrittely:
		lomake.append(pituus);
		lomake.append(new Spacer(15, 10));
		lomake.append(valinta);
		lomake.addCommand(TAKAISIN_KOMENTO);
		lomake.setCommandListener(this);

		// valinnan määrittely (tänne jotain säätöä?):
		valinta.append("Kaynnista", null);
		// valinta.setItemCommandListener(new ItemCommandListener() {
		// public void commandAction(Command c, Item d) {
		//				
		//				
		// }
		// });

		// lomakkeen 2 määrittely:
		lomake2.append(pituus);
		lomake2.append(kulmanSuuruus);
		lomake2.append(new Spacer(15, 10));
		lomake2.append(valinta);
		lomake2.addCommand(TAKAISIN_KOMENTO);
		lomake2.setCommandListener(this);

		// ohjelman käynnistyksen yhteydessä tapahtuvat toimenpiteet:
		naytto = Display.getDisplay();
		naytto.setCurrent(paavalikko);

		kynamoottori.setSpeed(15);
		piirtaja.setTravelSpeed(5);
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
			// päävalikon (+ sen toimintojen) käsittely:
			else if (d == paavalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(piirtovalikko);
				}

				// else if (list.getSelectedIndex() == 1) {
				// naytto.setCurrent(lomake2);
				// }
				//
				// else if (list.getSelectedIndex() == 2) {
				// naytto.setCurrent(lomake);
				// }
				//
				// else if (list.getSelectedIndex() == 3) {
				// naytto.setCurrent(lomake);
				// }
			}

			else if (d == piirtovalikko) {
				List list = (List) naytto.getCurrent();
				if (list.getSelectedIndex() == 0) {
					kynamoottori.rotate(45);
					kynamoottori.rotate(-45);
				} else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syotaPituus);
				} else if (list.getSelectedIndex() == 2) {
					naytto.setCurrent(syotaKulma);
				} else if (list.getSelectedIndex() == 3) {
					// piirtotoiminto:
					kynamoottori.rotate(45);
					ympyranPiirtaja.arc(
							Integer.parseInt(syotaPituus.getText()), Integer
									.parseInt(syotaKulma.getText()));
					kynamoottori.rotate(-45);
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}
}