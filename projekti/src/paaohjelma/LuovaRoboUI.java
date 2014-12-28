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
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.Font;
import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.LCD;

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

	private TextBox syote = new TextBox("Lisaa tekstia:", "", 16, TextField.ANY);
	private List valinnat = new List("Valitse kohteet",
			Choice.MULTIPLE);
	private Alert aaniHalytys = new Alert("Aanihalytys");
	private Form lomake1 = new Form("Testilomake");
	private Form lomake2 = new Form("Lomake eri asioille");
	private Alert lopetusHalytys = new Alert("Lopeta");

	private Gauge halytysMittari = new Gauge(null, false, 20, 0);
	private Timer mittariAjastin = new Timer(100, new TimerListener() {
		public void timedOut() {
			int curValue = halytysMittari.getValue();
			if (curValue >= halytysMittari.getMaxValue()) {
				mittariAjastin.stop();
				halytysMittari.setValue(0);
			} else {
				halytysMittari.setValue(curValue + 1);
			}
			aaniHalytys.repaint();
		}
	});

	private ChoiceGroup valintaryhma1 = new ChoiceGroup("Ponnahdusikkuna 1",
			Choice.POPUP);
	private ChoiceGroup valintaryhma2 = new ChoiceGroup("Ponnahdusikkuna 2",
			Choice.POPUP);
	private ChoiceGroup radionappulat = new ChoiceGroup(null, Choice.EXCLUSIVE);
	private Image kuva = new Image(32, 32, new byte[] { (byte) 0xff,
			(byte) 0x03, (byte) 0x05, (byte) 0x09, (byte) 0x11, (byte) 0x21,
			(byte) 0x41, (byte) 0x81, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x81, (byte) 0x41,
			(byte) 0x21, (byte) 0x11, (byte) 0x09, (byte) 0x05, (byte) 0x03,
			(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
			(byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20,
			(byte) 0x40, (byte) 0x80, (byte) 0x80, (byte) 0x40, (byte) 0x20,
			(byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10,
			(byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x01,
			(byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20,
			(byte) 0x40, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff,
			(byte) 0xff, (byte) 0xc0, (byte) 0xa0, (byte) 0x90, (byte) 0x88,
			(byte) 0x84, (byte) 0x82, (byte) 0x81, (byte) 0x80, (byte) 0x80,
			(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
			(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
			(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x81,
			(byte) 0x82, (byte) 0x84, (byte) 0x88, (byte) 0x90, (byte) 0xa0,
			(byte) 0xc0, (byte) 0xff });
//	private Image loiske = new Image(26, 32, new byte[] { (byte) 0x00,
//			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x3F, (byte) 0x3F,
//			(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0xFF,
//			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xFC,
//			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xA0,
//			(byte) 0x40, (byte) 0xA0, (byte) 0x40, (byte) 0xA0, (byte) 0x40,
//			(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//			(byte) 0xFF, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0,
//			(byte) 0xF0, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0x0A, (byte) 0x05, (byte) 0x0A, (byte) 0x05, (byte) 0x0A,
//			(byte) 0x05, (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//			(byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//			(byte) 0xFF, (byte) 0xFF, (byte) 0x0F, (byte) 0x1F, (byte) 0x3F,
//			(byte) 0x3F, (byte) 0x7F, (byte) 0x7F, (byte) 0xFC, (byte) 0xF8,
//			(byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0,
//			(byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0,
//			(byte) 0xF8, (byte) 0xFC, (byte) 0x7F, (byte) 0x7F, (byte) 0x3F,
//			(byte) 0x3F, (byte) 0x1F, (byte) 0x0F, });
//	private Splasher logoKaynnistys = new Splasher(loiske, "luovaRobo", true);

	private Gauge volyymimittari = new Gauge("Aanenvoimakkuus: ", true, 8, 6);
	private Gauge mittari = new Gauge("Edistymispalkki", true, 20, 9);
	TextField tekstikentta = new TextField("Tekstikentta", "abc", 16,
			TextField.ANY);

	private Display naytto;

	public LuovaRoboUI() {
	}

	public void kaynnista(boolean polling) {
		valikko = new List("Valitse toiminto", Choice.IMPLICIT);
		valikko.append("Tekstiboksi", null);
		valikko.append("Lista", null);
		valikko.append("Halytys", null);
		valikko.append("Lomake 1", null);
		valikko.append("Lomake 2", null);
		valikko.addCommand(LOPETA_KOMENTO);
		valikko.setCommandListener(this);
		valikko.setTicker(liikkuvaTekstikentta);

		syote.addCommand(TAKAISIN_KOMENTO);
		syote.setCommandListener(this);

		valinnat.addCommand(TAKAISIN_KOMENTO);
		valinnat.setCommandListener(this);
		valinnat.append("Kohde 1", null);
		valinnat.append("Kohde 2", null);
		valinnat.append("Kohde 3", null);
		valinnat.append("Kohde 4", null);
		valinnat.append("Kohde 5", null);
		valinnat.append("Kohde 6", null);
		valinnat.append("Kohde 7", null);
		valinnat.append("Kohde 8", null);
		valinnat.append("Kohde 9", null);
		valinnat.append("Kohde 10", null);

		aaniHalytys.setType(Alert.ALERT_TYPE_ERROR);
		aaniHalytys.setTimeout(5000);
		aaniHalytys.setString("** HALYTYS **");
		aaniHalytys.setIndicator(halytysMittari);

		lomake1.append(valintaryhma1);
		lomake1.append(valintaryhma2);
		lomake1.append("Vasen");
		lomake1.append(kuva);
		lomake1.append(new Spacer(8, 8));
		lomake1.append("Oikea");
		lomake1.append(radionappulat);
		lomake1.addCommand(TAKAISIN_KOMENTO);
		lomake1.setCommandListener(this);

		valintaryhma1.append("Valikko 1", null);
		valintaryhma1.append("Valikko 2", null);
		valintaryhma1.append("Valikko 3", null);
		valintaryhma1.append("Valikko 4", null);
		valintaryhma1.append("Valikko 5", null);
		valintaryhma1.append("Valikko 6", null);
		valintaryhma1.append("Valikko 7", null);
		valintaryhma1.append("Valikko 8", null);
		valintaryhma1.append("Valikko 9", null);
		valintaryhma1.append("Valikko 10", null);

		valintaryhma2.append("Vaihtoehto 1", null);
		valintaryhma2.append("Vaihtoehto 2", null);
		valintaryhma2.append("Vaihtoehto 3", null);
		valintaryhma2.append("Vaihtoehto 4", null);
		valintaryhma2.setScrollWrap(false);
		valintaryhma2.setItemCommandListener(new ItemCommandListener() {
			public void commandAction(Command c, Item d) {
				radionappulat.setSelectedIndex(
						valintaryhma2.getSelectedIndex() % 2, true);
			}
		});
		valintaryhma2.addCommand(new Command(1, Command.SCREEN, 0));

		radionappulat.append("Valinta 1", null);
		radionappulat.append("Valinta 2", null);
		radionappulat.setSelectedIndex(0, true);
		radionappulat.setPreferredSize(Display.SCREEN_WIDTH,
				2 * Display.CHAR_HEIGHT);

		lomake2.append(volyymimittari);
		lomake2.append(mittari);
		lomake2.append(tekstikentta);
		lomake2.addCommand(TAKAISIN_KOMENTO);
		lomake2.setCommandListener(this);

		naytto = Display.getDisplay();
		naytto.setCurrent(valikko);

//		logoKaynnistys.setTimeout(4000);
//		naytto.setCurrent(logoKaynnistys);
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
//					logoKaynnistys.setStart(false);
//					naytto.setCurrent(logoKaynnistys);
					naytto.quit();
				} else {
					naytto.setCurrent(valikko);
				}
			} else if (d == valikko) {
				List list = (List) naytto.getCurrent();
				if (list.getSelectedIndex() == 0) {
					naytto.setCurrent(syote);
				} else if (list.getSelectedIndex() == 1) {
					naytto.setCurrent(valinnat);
				} else if (list.getSelectedIndex() == 2) {
					naytto.setCurrent(aaniHalytys);
					halytysMittari.setValue(0);
					mittariAjastin.start();
				} else if (list.getSelectedIndex() == 3) {
					naytto.setCurrent(lomake1);
				} else if (list.getSelectedIndex() == 4) {
					naytto.setCurrent(lomake2);
				}
			}
		}
	}

	public static void main(String[] args) {
		new LuovaRoboUI().kaynnista(true);
	}

}
