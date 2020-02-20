package com.codeseavers.pi4j.ssd1306;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import com.pi4j.Pi4J;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

public class DisplayV2 {

    private final int I2C_DEVICE = 0x3C;

    private int vccState = Constants.SSD1306_SWITCHCAPVCC;
    private final int compins = 0x12;
    private byte[] buffer;
    private final int width, height, pages;
    protected BufferedImage img;
    protected Graphics2D graphics;

    private final I2CConfig config;
    private I2C i2c;

    private I2CProvider provider;

    public DisplayV2() throws Exception {
        this.width = 128;
        this.height = 64;

        this.pages = (height / 8);
        this.buffer = new byte[width * this.pages];

        var pi4j = Pi4J.newAutoContext();
        this.config = I2C.newConfigBuilder(pi4j).id("my-i2c-bus").name("My IC Bus").bus(1).device(I2C_DEVICE).build();
        this.provider = pi4j.provider("pigpio-i2c");

        /*this.config = I2C.newConfigBuilder(pi4j).id("my-i2c-bus").name("My IC Bus").bus(1).device(I2C_DEVICE).build();

        PiGpio piGpio = PiGpio.newNativeInstance();
        piGpio.initialize();
        this.provider = new PiGpioI2CProviderImpl(piGpio);*/

        this.i2c = this.provider.create(this.config);

        this.img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        this.graphics = this.img.createGraphics();
    }

    public void close() throws Exception {
        this.i2c.close();
    }

    private void init() {
        try {
            i2c.write(Constants.SSD1306_DISPLAYOFF);
            i2c.write(Constants.SSD1306_SETDISPLAYCLOCKDIV);
            i2c.write(0x80);
            i2c.write(Constants.SSD1306_SETMULTIPLEX);
            i2c.write(0x3F);
            i2c.write(Constants.SSD1306_SETDISPLAYOFFSET);
            i2c.write(0x0);
            i2c.write(Constants.SSD1306_SETSTARTLINE);
            i2c.write(Constants.SSD1306_CHARGEPUMP);

            if (this.vccState == Constants.SSD1306_EXTERNALVCC)
                i2c.write((short) 0x10);
            else
                i2c.write((short) 0x14);

            i2c.write(Constants.SSD1306_MEMORYMODE);
            i2c.write((short) 0x00);
            i2c.write((short) (Constants.SSD1306_SEGREMAP | 0x1));
            i2c.write(Constants.SSD1306_COMSCANDEC);
            i2c.write(Constants.SSD1306_SETCOMPINS);
            i2c.write((short) this.compins);
            i2c.write(Constants.SSD1306_SETCONTRAST);

            if (this.vccState == Constants.SSD1306_EXTERNALVCC)
                i2c.write((short) 0x9F);
            else
                i2c.write((short) 0xCF);

            i2c.write(Constants.SSD1306_SETPRECHARGE);

            if (this.vccState == Constants.SSD1306_EXTERNALVCC)
                i2c.write((short) 0x22);
            else
                i2c.write((short) 0xF1);

            i2c.write(Constants.SSD1306_SETVCOMDETECT);
            i2c.write((short) 0x40);
            i2c.write(Constants.SSD1306_DISPLAYALLON_RESUME);
            i2c.write(Constants.SSD1306_NORMALDISPLAY);

        } catch (final Exception e) {
            // Handle me properly
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Begin with SWITCHCAPVCC VCC mode
     * 
     * @see Constants#SSD1306_SWITCHCAPVCC
     */
    public void begin() throws IOException {
        this.begin(Constants.SSD1306_SWITCHCAPVCC);
    }

    /**
     * Begin with specified VCC mode (can be SWITCHCAPVCC or EXTERNALVCC)
     * 
     * @param vccState VCC mode
     * @see Constants#SSD1306_SWITCHCAPVCC
     * @see Constants#SSD1306_EXTERNALVCC
     */
    public void begin(final int vccState) throws IOException {
        this.vccState = vccState;
        // For now ignore the reset pin stuff
        // https://github.com/SmingHub/Sming/issues/501
        this.write(Constants.SSD1306_SWITCHCAPVCC);
        // this.reset();
        this.init();
        this.write(Constants.SSD1306_DISPLAYON);
        this.clear();
        this.display();
    }

    private void write(final int command) {
        try {
            this.i2c.write(command);
        } catch (final Exception e) {
            // Handle me
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeRegister(final I2C i2c, final int command, final int register) throws IOException {
        i2c.writeRegister(register, command);
    }

    /**
     * Clears the buffer by creating a new byte array
     */
    public void clear() {
        this.buffer = new byte[this.width * this.pages];
    }

    /**
     * Sends the buffer to the display
     */
    public synchronized void display() {
        this.write(Constants.SSD1306_COLUMNADDR);
        this.write(0);
        this.write(this.width - 1);
        this.write(Constants.SSD1306_PAGEADDR);
        this.write(0);
        this.write(this.pages - 1);

        this.data(this.buffer);
    }

    /**
     * Turns on data mode and sends data array
     * 
     * @param data Data array
     */
    private void data(final byte[] data) {
        try{
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < 16; j++) {
                    this.writeRegister(this.i2c, 0x40, data[i]);
                    i++;
                }
                i--;
            }
        } catch (final Exception e) {
            // Handle me
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clears the screen and displays the string sent in, adding new lines as needed
     * 
     * @param data
     * @param line
     */
    public void displayString(final String... data) {
        clearImage();
        for (int i = 0; i < data.length; i++) {
            graphics.drawString(data[i], 0, Constants.STRING_HEIGHT * (i + 1));
        }
        displayImage();
    }

    public void clearImage() {
        this.graphics.setBackground(new Color(0, 0, 0, 0));
        this.graphics.clearRect(0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Copies AWT image contents to buffer. Calls display()
     * 
     * @see SSD1306_I2C_Display#display()
     */
    public synchronized void displayImage() {
        final Raster r = this.img.getRaster();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.setPixel(x, y, (r.getSample(x, y, 0) > 0));
            }
        }

        this.display();
    }

    /**
     * Sets one pixel in the current buffer
     * 
     * @param x     X position
     * @param y     Y position
     * @param white White or black pixel
     * @return True if the pixel was successfully set
     */
    public boolean setPixel(final int x, final int y, final boolean white) {
        if (x < 0 || x > this.width || y < 0 || y > this.height) {
            return false;
        }

        if (white) {
            this.buffer[x + (y / 8) * this.width] |= (1 << (y & 7));
        } else {
            this.buffer[x + (y / 8) * this.width] &= ~(1 << (y & 7));
        }

        return true;
    }
}
