// Pohjautuu esimerkkien LCDUI:iin, jota lähdin muokkaamaan haluamakseni

package paaohjelma;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
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

	private List valikko = new List("Komponentit", Choice.IMPLICIT);
	private Ticker liikkuvaTekstikentta = new Ticker("Hei, olen luovaRobo!");

	private TextBox syote = new TextBox("Anna pituus:", "", 16,
			TextField.ANY);
	private Alert lopetusHalytys = new Alert("Lopeta");

	TextField tekstikentta = new TextField("Tekstikentta", "abc", 16,
			TextField.ANY);

	private Display naytto;
	
	// vielä täysin kesken näiltä osin (rivit 131-140):
	private NXTRegulatedMotor kynamoottori = Motor.B;
//	kynamoottori.setSpeed(15);

	private MoveController piirtaja = new DifferentialPilot(5.6f, 9.0f, Motor.A,
			Motor.C);
//	piirtaja.setTravelSpeed(5);

	private ArcRotateMoveController ympyranPiirtaja = new DifferentialPilot(5.6f,
			9.0f, Motor.A, Motor.C);
//	ympyranPiirtaja.setTravelSpeed(5);

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
		valikko = new List("Valitse toiminto", Choice.IMPLICIT);
		valikko.append("Piirra viiva", null);
		valikko.append("Piirra ympyra", null);
		valikko.append("Piirra nelio", null);
		valikko.append("Piirra kolmio", null);
		valikko.addCommand(LOPETA_KOMENTO);
		valikko.setCommandListener(this);
		valikko.setTicker(liikkuvaTekstikentta);
		annaAanimerkkiA();

		syote.addCommand(TAKAISIN_KOMENTO);
		syote.setCommandListener(this);

		naytto = Display.getDisplay();
		naytto.setCurrent(valikko);

		naytto.show(polling);
	}

	public void commandAction(Command c, Displayable d) {
		if (c.getCommandId() == KOMENTO_TAKAISIN__ALKUUN) {
			naytto.setCurrent(valikko);
		} else if (c.getCommandId() == KOMENTO_LOPETA_OHJELMA) {
			lopetusHalytys.setType(Alert.ALERT_TYPE_CONFIRMATION);
			lopetusHalytys.setString("Lopetetaanko?");
			lopetusHalytys.setCommandListener(this);
			naytto.setCurrent(lopetusHalytys);
		} else {

			if (d == lopetusHalytys) {
				if (lopetusHalytys.getConfirmation()) {
					annaAanimerkkiB();
					naytto.quit();
				} else {
					naytto.setCurrent(valikko);
				}
			} else if (d == valikko) {
				List list = (List) naytto.getCurrent();
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syote);
				} else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(syote);
				} else if (list.getSelectedIndex() == 2) {
					naytto.setCurrent(syote);
				} else if (list.getSelectedIndex() == 3) {
					naytto.setCurrent(syote);
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}
}
