// Pohjautuu esimerkkien LCDUI:iin, jota lähdin muokkaamaan haluamakseni

package paaohjelma;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.Font;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MoveController;

class Splasher extends Alert {
	Image kuva;
	boolean kaynnistys;
	String teksti;

	Splasher(Image img, String text, boolean start) {
		super("");
		this.kuva = img;
		this.teksti = text;
		setStart(start);
	}

	public void setStart(boolean start) {
		kaynnistys = start;
	}

	protected void showNotify() {
		repaint();
	}

	public void paint(Graphics g) {
		g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
		int iw = kuva.getWidth();
		int ih = kuva.getHeight();
		int dx = (Display.SCREEN_WIDTH - iw) / 2;
		int dy = (Display.SCREEN_HEIGHT - ih) / 2 - 10;
		int tdy = dy + ih + 5;
		int tw = g.getFont().stringWidth(teksti);
		int tdx = (Display.SCREEN_WIDTH - tw) / 2;
		int th = g.getFont().getHeight();
		if (kaynnistys) {
			for (int i = 32; i >= 0; i--) {
				LCD.clear();
				g.drawRegionRop(kuva, 0, 0, iw, ih, dx + i, dy, 0, 0xaaffaa00);
				g.drawRegionRop(kuva, 0, 0, iw, ih, dx - i, dy, 0, 0x55ff5500);
				LCD.refresh();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}
			int old = g.getColor();
			g.setColor(Graphics.WHITE);
			for (int i = (Display.SCREEN_HEIGHT - tdy); i >= 0; i--) {
				g.fillRect(tdx, tdy + i + th + 1, tw, th);
				g.drawString(teksti, tdx, tdy + i, 0, true);
				LCD.refresh();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}
			g.setColor(old);
		} else {
			int old = g.getColor();
			g.setColor(Graphics.WHITE);
			g.drawRegionRop(kuva, 0, 0, iw, ih, dx, dy, 0, LCD.ROP_COPY);
			for (int i = 0; i < (Display.SCREEN_HEIGHT - tdy); i++) {
				g.fillRect(tdx, tdy + i - 1, tw, th);
				g.drawString(teksti, tdx, tdy + i, 0, true);
				LCD.refresh();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}
			g.setColor(old);
			for (int i = 0; i <= iw / 2; i++) {
				LCD.clear();
				g.drawRegionRop(kuva, i, i, iw - 2 * i, ih - 2 * i, dx + i, dy
						+ i, 0, LCD.ROP_COPY);
				LCD.refresh();
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}
		}
		g.setFont(Font.getDefaultFont());
	}

}

public class LuovaRoboUI implements CommandListener {
	private static final int KOMENTO_TAKAISIN__ALKUUN = 1;
	private static final int KOMENTO_LOPETA_OHJELMA = 2;

	private static final Command TAKAISIN_KOMENTO = new Command(
			KOMENTO_TAKAISIN__ALKUUN, Command.BACK, 0);
	private static final Command LOPETA_KOMENTO = new Command(
			KOMENTO_LOPETA_OHJELMA, Command.STOP, 2);

	private List paavalikko = new List("Paavalikko", Choice.IMPLICIT);

	private List piirtovalikko = new List("Piirroksen tiedot", Choice.IMPLICIT);

	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen luovaRobo!");

	// private TextBox syote = new TextBox("Anna pituus:", "", 16,
	// TextField.ANY);

	private Alert lopetusHalytys = new Alert("Lopeta");

	private Form lomake = new Form("Piirroksen tiedot"); // viivalle tms.
	private Form lomake2 = new Form("Piirroksen tiedot"); // ympyrälle tms.

	TextField pituus = new TextField("Anna pituus:", "0", 16, TextField.ANY);
	TextField kulmanSuuruus = new TextField("Anna kulma:", "0", 16,
			TextField.ANY);

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
		paavalikko.append("Piirra viiva", null);
		paavalikko.append("Piirra ympyra", null);
		paavalikko.append("Piirra nelio", null);
		paavalikko.append("Piirra kolmio", null);
		paavalikko.addCommand(LOPETA_KOMENTO);
		paavalikko.setCommandListener(this);
		paavalikko.setTicker(liikkuvaTekstikentta);

//		// syötteen määrittely:
//		syote.addCommand(TAKAISIN_KOMENTO);
//		syote.setCommandListener(this);

		// lomakkeen määrittely:
		lomake.append(pituus);
		lomake.addCommand(TAKAISIN_KOMENTO);
		lomake.setCommandListener(this);

		// lomakkeen 2 määrittely:
		lomake2.append(pituus);
		lomake2.append(kulmanSuuruus);
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
		if (c.getCommandId() == KOMENTO_TAKAISIN__ALKUUN) {
			naytto.setCurrent(paavalikko);
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
					naytto.setCurrent(lomake);
				}

				else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(lomake2);
				}

				else if (list.getSelectedIndex() == 2) {
					naytto.setCurrent(lomake);
				}

				else if (list.getSelectedIndex() == 3) {
					naytto.setCurrent(lomake);
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}
}
