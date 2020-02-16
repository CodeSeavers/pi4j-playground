package com.codeseavers.pi4j.ssd1306;

public class HelloSSD1306World {

	static String[] scrollText = new String[] { "Hello World!", "Heute ist Freitag", "Wie schön :-)", "Ich bin zuhause",
			"Nachher gibt's Suppe" };

	public static void main(String[] args) {

		DisplayV2 display = null;
		try {
			display = new DisplayV2();
			display.begin();

			int i = 0;

			while (true) {
				display.displayString(scrollText[i], scrollText[i + 1]);
				i++;
				if (i == 4)
					i = 0;
				Thread.sleep(400);
			}
		} catch (Exception e) {

		} finally {
			display.clear();
			try {
				display.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
