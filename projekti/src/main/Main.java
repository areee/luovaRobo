package main;

// Pohjautuu esimerkkien LCDUI:iin, jota lähdin muokkaamaan haluamakseni

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

public class Main implements CommandListener {
	// Komentojen tunnisteet:
	private static final int KOMENTO_TAKAISIN_PAAVALIKKOON = 1;
	private static final int KOMENTO_LOPETA_OHJELMA = 2;
	private static final int KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON = 3;
	private static final int KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON = 4;
	private static final int KOMENTO_TAKAISIN_NELION_PIIRTOVALIKKOON = 5;
	private static final int KOMENTO_TAKAISIN_KOLMION_PIIRTOVALIKKOON = 6;
	private static final int KOMENTO_TAKAISIN_MONIKULMION_PIIRTOVALIKKOON = 7;

	// Komennot:
	private static final Command TAKAISIN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_PAAVALIKKOON, Command.BACK, 0);

	private static final Command LOPETA_KOMENTO = new Command(
			KOMENTO_LOPETA_OHJELMA, Command.STOP, 2);

	private static final Command TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON, Command.BACK, 0);

	private static final Command TAKAISIN_VIIVAA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON, Command.BACK, 0);

	private static final Command TAKAISIN_NELIOTA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_NELION_PIIRTOVALIKKOON, Command.BACK, 0);

	private static final Command TAKAISIN_KOLMIOTA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_KOLMION_PIIRTOVALIKKOON, Command.BACK, 0);

	private static final Command TAKAISIN_MONIKULMIOTA_PIIRTAMAAN_KOMENTO = new Command(
			KOMENTO_TAKAISIN_MONIKULMION_PIIRTOVALIKKOON, Command.BACK, 0);

	// Päävalikkoon liittyviä komponentteja:
	private String nimi = "LuovaRobo"; // oletusnimi
	private List paavalikko = new List("Paavalikko", Choice.IMPLICIT);
	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen " + nimi
			+ "! Mita haluat piirtaa?");
	private Alert lopetusHalytys = new Alert("Lopeta");

	// Piirtotoimintoja varten arvojen syötöt:
	private TextBox syotaYmpyranKaarenPituus = new TextBox("Kaaren pituus?",
			"", 3, TextField.ANY);

	private TextBox syotaYmpyranKulma = new TextBox("Kulman suuruus?", "", 3,
			TextField.ANY);

	private TextBox syotaViivanPituus = new TextBox("Viivan pituus?", "", 3,
			TextField.ANY);

	private TextBox syotaNelionSivunPituus = new TextBox("Sivun pituus?", "",
			3, TextField.ANY);

	private TextBox syotaKolmionSivunPituus = new TextBox("Sivun pituus?", "",
			3, TextField.ANY);

	private TextBox syotaMonikulmionSivunPituus = new TextBox("Sivun pituus?",
			"", 3, TextField.ANY);

	private TextBox syotaMontakoKulmaaMonikulmiossa = new TextBox(
			"Montako kulmaa?", "", 2, TextField.ANY);

	// Piirtovalikot piirtämistoimintoihin:
	private List ympyranPiirtovalikko = new List("Ympyran piirtaminen",
			Choice.IMPLICIT);
	private List viivanPiirtovalikko = new List("Viivan piirtaminen",
			Choice.IMPLICIT);
	private List nelionPiirtovalikko = new List("Nelion piirtaminen",
			Choice.IMPLICIT);
	private List kolmionPiirtovalikko = new List("Kolmion piirtaminen",
			Choice.IMPLICIT);
	private List monikulmionPiirtovalikko = new List("Monikulmion piirtaminen",
			Choice.IMPLICIT);

	// Poikkeustilanteelle valikko:
	private List poikkeustilavalikko = new List("Virhe arvo(i)ssa!",
			Choice.IMPLICIT);

	private Display naytto;

	// Robotin moottoreiden alustamiset:
	private NXTRegulatedMotor kynamoottori = Motor.B;

	private ArcRotateMoveController piirtaja = new DifferentialPilot(5.6f,
			12.3f, Motor.A, Motor.C);

	public Main() {
	}

	// Käynnistysääni:
	private void kaynnistysaani() {
		Sound.playNote(Sound.FLUTE, 523, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 1047, 500);
	}

	// Lopetusääni:
	private void lopetusaani() {
		Sound.playNote(Sound.FLUTE, 1047, 125);
		Sound.playNote(Sound.FLUTE, 784, 125);
		Sound.playNote(Sound.FLUTE, 659, 125);
		Sound.playNote(Sound.FLUTE, 523, 500);
	}

	// Piirtämisen valmistuttua kuultava äänimerkki:
	private void piirtaminenValmisAani() { // 500 = 1/4-nuotti
		Sound.playNote(Sound.PIANO, 1047, 250);// 1/8
		Sound.playNote(Sound.PIANO, 784, 125);// 1/16
		Sound.playNote(Sound.PIANO, 784, 125);// 1/16
		Sound.playNote(Sound.PIANO, 880, 250);// 1/8
		Sound.playNote(Sound.PIANO, 784, 250);// 1/8
		Sound.pause(250);// 1/8
		Sound.playNote(Sound.PIANO, 988, 250);// 1/8
		Sound.playNote(Sound.PIANO, 1047, 500);// 1/4
	}

	// Piirtämisen alkamisen äänimerkki:
	private void piirtaminenAlkaaAani() {
		Sound.playNote(Sound.FLUTE, 523, 500);
		Sound.pause(500);
		Sound.playNote(Sound.FLUTE, 523, 500);
		Sound.pause(500);
		Sound.playNote(Sound.FLUTE, 523, 500);
		Sound.pause(500);
		Sound.playNote(Sound.FLUTE, 784, 1000);
	}

	public void kaynnista(boolean polling) {

		// Päävalikon toimintojen asettaminen:
		paavalikko = new List("Valitse toiminto", Choice.IMPLICIT);
		paavalikko.append("Piirra ympyra", null);
		paavalikko.append("Piirra viiva", null);
		paavalikko.append("Piirra nelio", null);
		paavalikko.append("Piirra kolmio", null);
		paavalikko.append("Piirra kulmio", null);
		paavalikko.addCommand(LOPETA_KOMENTO);
		paavalikko.setCommandListener(this);
		paavalikko.setTicker(liikkuvaTekstikentta);

		// Piirtovalikoiden toimintojen asettaminen:
		ympyranPiirtovalikko = new List("Ympyra", Choice.IMPLICIT);
		ympyranPiirtovalikko.append("Syota pituus", null);
		ympyranPiirtovalikko.append("Syota kulma", null);
		ympyranPiirtovalikko.append("Piirra!", null);
		ympyranPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		ympyranPiirtovalikko.setCommandListener(this);

		viivanPiirtovalikko = new List("Viiva", Choice.IMPLICIT);
		viivanPiirtovalikko.append("Syota pituus", null);
		viivanPiirtovalikko.append("Piirra!", null);
		viivanPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		viivanPiirtovalikko.setCommandListener(this);

		nelionPiirtovalikko = new List("Nelio", Choice.IMPLICIT);
		nelionPiirtovalikko.append("Syota pituus", null);
		nelionPiirtovalikko.append("Piirra!", null);
		nelionPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		nelionPiirtovalikko.setCommandListener(this);

		kolmionPiirtovalikko = new List("Kolmio", Choice.IMPLICIT);
		kolmionPiirtovalikko.append("Syota pituus", null);
		kolmionPiirtovalikko.append("Piirra!", null);
		kolmionPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		kolmionPiirtovalikko.setCommandListener(this);

		monikulmionPiirtovalikko = new List("Monikulmio", Choice.IMPLICIT);
		monikulmionPiirtovalikko.append("Syota pituus", null);
		monikulmionPiirtovalikko.append("Kulmien lkm?", null);
		monikulmionPiirtovalikko.append("Piirra!", null);
		monikulmionPiirtovalikko.addCommand(TAKAISIN_KOMENTO);
		monikulmionPiirtovalikko.setCommandListener(this);

		// Poikkeusvalikon toiminnot:
		poikkeustilavalikko = new List("Virhe arvo(i)ssa!", Choice.IMPLICIT);
		poikkeustilavalikko
				.setTicker(new Ticker(
						"Asetit vaarat arvot. Valitse toiminto uudelleen "
								+ "ja syota ainoastaan numeroita. Loydat numerot valitsemalla"
								+ " nappaimiston vasemmasta alanurkasta 123-kohdan."));
		poikkeustilavalikko.append("Paavalikkoon", null);
		poikkeustilavalikko.addCommand(TAKAISIN_KOMENTO);
		poikkeustilavalikko.setCommandListener(this);

		// Syötteiden määrittely:
		syotaYmpyranKaarenPituus
				.addCommand(TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO);
		syotaYmpyranKaarenPituus.setCommandListener(this);

		syotaYmpyranKulma.addCommand(TAKAISIN_YMPYRAA_PIIRTAMAAN_KOMENTO);
		syotaYmpyranKulma.setCommandListener(this);

		syotaViivanPituus.addCommand(TAKAISIN_VIIVAA_PIIRTAMAAN_KOMENTO);
		syotaViivanPituus.setCommandListener(this);

		syotaNelionSivunPituus.addCommand(TAKAISIN_NELIOTA_PIIRTAMAAN_KOMENTO);
		syotaNelionSivunPituus.setCommandListener(this);

		syotaKolmionSivunPituus
				.addCommand(TAKAISIN_KOLMIOTA_PIIRTAMAAN_KOMENTO);
		syotaKolmionSivunPituus.setCommandListener(this);

		syotaMonikulmionSivunPituus
				.addCommand(TAKAISIN_MONIKULMIOTA_PIIRTAMAAN_KOMENTO);
		syotaMonikulmionSivunPituus.setCommandListener(this);

		syotaMontakoKulmaaMonikulmiossa
				.addCommand(TAKAISIN_MONIKULMIOTA_PIIRTAMAAN_KOMENTO);
		syotaMontakoKulmaaMonikulmiossa.setCommandListener(this);

		// Ohjelman käynnistyksen yhteydessä tapahtuvat toimenpiteet:
		naytto = Display.getDisplay();
		naytto.setCurrent(paavalikko);

		kynamoottori.setSpeed(15);
		piirtaja.setTravelSpeed(5);
		piirtaja.setRotateSpeed(15);

		kaynnistysaani();
		naytto.show(polling);
	}

	public void commandAction(Command c, Displayable d) {

		// Edelliseen valikkoon palaaminen:
		if (c.getCommandId() == KOMENTO_TAKAISIN_PAAVALIKKOON) {
			naytto.setCurrent(paavalikko);
		}

		else if (c.getCommandId() == KOMENTO_TAKAISIN_YMPYRAN_PIIRTOVALIKKOON) {
			naytto.setCurrent(ympyranPiirtovalikko);
		}

		else if (c.getCommandId() == KOMENTO_TAKAISIN_VIIVAN_PIIRTOVALIKKOON) {
			naytto.setCurrent(viivanPiirtovalikko);
		}

		else if (c.getCommandId() == KOMENTO_TAKAISIN_NELION_PIIRTOVALIKKOON) {
			naytto.setCurrent(nelionPiirtovalikko);
		}

		else if (c.getCommandId() == KOMENTO_TAKAISIN_KOLMION_PIIRTOVALIKKOON) {
			naytto.setCurrent(kolmionPiirtovalikko);
		}

		else if (c.getCommandId() == KOMENTO_TAKAISIN_MONIKULMION_PIIRTOVALIKKOON) {
			naytto.setCurrent(monikulmionPiirtovalikko);
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
					lopetusaani();
					naytto.quit();
				} else {
					naytto.setCurrent(paavalikko);
				}
			}

			// Päävalikon toimintojen käsittely:
			else if (d == paavalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(ympyranPiirtovalikko);
				}

				else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(viivanPiirtovalikko);
				}

				else if (list.getSelectedIndex() == 2) {
					naytto.setCurrent(nelionPiirtovalikko);
				}

				else if (list.getSelectedIndex() == 3) {
					naytto.setCurrent(kolmionPiirtovalikko);
				} else if (list.getSelectedIndex() == 4) {
					naytto.setCurrent(monikulmionPiirtovalikko);
				}
			}

			// Ympyrän piirtovalikon toimintojen käsittely:
			else if (d == ympyranPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaYmpyranKaarenPituus);
				}

				else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syotaYmpyranKulma);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 2) {
					String pituus = syotaYmpyranKaarenPituus.getText();
					String kulma = syotaYmpyranKulma.getText();
					try {
						double pituusLukuna = Double.parseDouble("-" + pituus);
						double kulmaLukuna = Double.parseDouble(kulma);

						piirtaminenAlkaaAani();
						laskeKyna();
						piirtaja.arc(pituusLukuna, kulmaLukuna);
						nostaKyna();
						naytto.setCurrent(paavalikko);
						piirtaminenValmisAani();
					} catch (Exception e) {
						naytto.setCurrent(poikkeustilavalikko);
					}

				}
			}

			// Viivan piirtovalikon toimintojen käsittely:
			else if (d == viivanPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaViivanPituus);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 1) {
					try {
						String pituus = syotaViivanPituus.getText();
						double pituusLukuna = Double.parseDouble("-" + pituus);

						piirtaminenAlkaaAani();
						laskeKyna();
						piirtaja.travel(pituusLukuna);
						nostaKyna();
						naytto.setCurrent(paavalikko);
						piirtaminenValmisAani();
					} catch (Exception e) {
						naytto.setCurrent(poikkeustilavalikko);
					}
				}
			}

			// Neliön piirtovalikon toimintojen käsittely:
			else if (d == nelionPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaNelionSivunPituus);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 1) {
					try {
						String pituus = syotaNelionSivunPituus.getText();
						double pituusLukuna = Double.parseDouble("-" + pituus);
						monikulmionPiirtaminen(pituusLukuna, 4, 90);
					} catch (Exception e) {
						naytto.setCurrent(poikkeustilavalikko);
					}
				}
			}

			// Tasasivuisen kolmion piirtovalikon toimintojen käsittely:
			else if (d == kolmionPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaKolmionSivunPituus);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 1) {
					try {
						String pituus = syotaKolmionSivunPituus.getText();
						double pituusLukuna = Double.parseDouble("-" + pituus);
						// kulma: 90°+30° (60°:n vieruskulma)
						monikulmionPiirtaminen(pituusLukuna, 3, 120);
					} catch (Exception e) {
						naytto.setCurrent(poikkeustilavalikko);
					}
				}
			}

			// Säännöllisen monikulmion piirtovalikon toimintojen käsittely
			// (HUOM! tukee myös kolmion ja neliön piirtämistä):
			else if (d == monikulmionPiirtovalikko) {
				List list = (List) naytto.getCurrent();

				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syotaMonikulmionSivunPituus);
				}

				else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syotaMontakoKulmaaMonikulmiossa);
				}

				// Piirtotoiminnon käynnistys:
				else if (list.getSelectedIndex() == 2) {
					try {
						String pituus = syotaMonikulmionSivunPituus.getText();
						double pituusLukuna = Double.parseDouble("-" + pituus);

						String kulmia = syotaMontakoKulmaaMonikulmiossa
								.getText();
						int kulmatLukuna = Integer.parseInt(kulmia);

						int kulma = (kulmatLukuna - 2) * 180 / kulmatLukuna;

						int piirroksenKaannos = 0;

						if (kulma < 90) {
							piirroksenKaannos = 90 + kulma;
							monikulmionPiirtaminen(pituusLukuna, kulmatLukuna,
									piirroksenKaannos);
						}

						else if (kulma == 90) {
							piirroksenKaannos = 90;
							monikulmionPiirtaminen(pituusLukuna, kulmatLukuna,
									piirroksenKaannos);
						}

						else if (kulma > 90) {
							piirroksenKaannos = 180 - kulma;
							// esim. 5-kulmio: kulma 108, piirroksenKaannos 72
							monikulmionPiirtaminen(pituusLukuna, kulmatLukuna,
									piirroksenKaannos);
						}
					} catch (Exception e) {
						naytto.setCurrent(poikkeustilavalikko);
					}
				}
			} else if (d == poikkeustilavalikko) {
				List list = (List) naytto.getCurrent();
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(paavalikko);
				}
			}
		}
	}

	private void monikulmionPiirtaminen(double pituusLukuna, int montakoKulmaa,
			int kulma) {
		piirtaminenAlkaaAani();
		for (int i = 0; i < montakoKulmaa; i++) {

			// kynän ja takapyörien etäisyys:
			double etaisyys = 18;

			laskeKyna();
			piirtaja.travel(pituusLukuna);
			nostaKyna();
			piirtaja.travel(etaisyys);
			piirtaja.rotate(kulma);
			piirtaja.travel(-etaisyys);
		}
		naytto.setCurrent(paavalikko);
		piirtaminenValmisAani();
	}

	private void nostaKyna() {
		kynamoottori.rotate(-60);
	}

	private void laskeKyna() {
		kynamoottori.rotate(60);
	}

	public static void main(String[] args) {
		new Main().kaynnista(true);
	}
}