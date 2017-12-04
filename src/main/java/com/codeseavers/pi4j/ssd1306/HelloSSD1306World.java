package com.codeseavers.pi4j.ssd1306;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.wiringpi.I2C;

public class HelloSSD1306World {

	static String[] scrollText = new String[] { "Hello World!", "Heute ist Freitag", "Wie schön :-)", "Ich bin zuhause",
			"Nachher gibt's Suppe" };

	public static void main(String[] args) {
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();
		I2CBus i2c;
		Display display = null;
		try {
			i2c = I2CFactory.getInstance(I2C.CHANNEL_1);
			display = new Display(128, 32, gpio, i2c, 0x3c);
			display.begin();

			int i = 0;

			while (true) {
				display.displayString(scrollText[i], scrollText[i + 1]);
				i++;
				if (i == 4)
					i = 0;
				Thread.sleep(400);
			}
		} catch (UnsupportedBusNumberException | IOException | InterruptedException e) {

		} finally {
			display.clear();
		}

	}

}
