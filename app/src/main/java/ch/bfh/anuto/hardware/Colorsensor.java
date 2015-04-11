package ch.bfh.anuto.hardware;

import android.graphics.Color;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Colorsensor {

    /*
    ------ Members ------
    */

    private static final char MCP9800_TEMP   = 0x00;      /* Ambient Temperature Register */
    private static final char MCP9800_CONFIG = 0x01;      /* Sensor Configuration Register */
    private static final char MCP9800_12_BIT = 0x60;
    private static final char MCP9800_I2C_ADDR = 0x48;
    private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

    // Color stuff
    I2C i2c_color = new I2C();
    int[] i2cCommBuffer_color = new int[16];
    int fileHandle_color;
    int max = 1;

    // Temperature stuff
    I2C i2c_temp = new I2C();
    int[] i2cCommBuffer_temp = new int[16];
    int fileHandle_temp;
    double TempC;
    int Temperature;

    // Internal variables
    private double TEMPERATURE = 0;
    private int COLOR = 0;

    // Timer
    Timer timer;
    MyTimerTask myTimerTask;

    /*
    ------ Methods ------
    */

    public int getColor(){
        return COLOR;
    }

    public double getTemperature(){
        return TEMPERATURE;
    }

    public void initI2C(){
        fileHandle_color = i2c_color.open(MCP9800_FILE_NAME);
        i2c_color.SetSlaveAddress(fileHandle_color, (char)0x39);

        fileHandle_temp = i2c_temp.open(MCP9800_FILE_NAME);
        int status = i2c_temp.SetSlaveAddress(fileHandle_temp, MCP9800_I2C_ADDR);

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        timer.schedule(myTimerTask, 0,200);
    }

    public int updateColor() {
        i2cCommBuffer_color[0] = (char)0x80; //bytewise + control
        int status =  i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);

        i2cCommBuffer_color[0] = 0x03; //adc on
        status =  i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1); //should read back 0x03

        i2cCommBuffer_color[0] = 0x80 | 0x10; //bytewise + r0
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        int r = i2cCommBuffer_color[0];

        i2cCommBuffer_color[0] = 0x80 | 0x11; //bytewise + r1
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        r |= i2cCommBuffer_color[0]<<8;

        i2cCommBuffer_color[0] = 0x80 | 0x12; //bytewise + g0
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        int g = i2cCommBuffer_color[0];

        i2cCommBuffer_color[0] = 0x80 | 0x13; //bytewise + g1
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        g |= i2cCommBuffer_color[0]<<8;

        i2cCommBuffer_color[0] = 0x80 | 0x14; //bytewise + b0
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        int b = i2cCommBuffer_color[0];

        i2cCommBuffer_color[0] = 0x80 | 0x15; //bytewise + b1
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        b |= i2cCommBuffer_color[0]<<8;

        i2cCommBuffer_color[0] = 0x80 | 0x16; //bytewise + c0
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        int c = i2cCommBuffer_color[0];

        i2cCommBuffer_color[0] = 0x80 | 0x17; //bytewise + c1
        status = i2c_color.write(fileHandle_color, i2cCommBuffer_color, 1);
        status = i2c_color.read(fileHandle_color, i2cCommBuffer_color, 1);
        c |= i2cCommBuffer_color[0]<<8;

       /*  double red = r/1.97;
         double green =g/1.6;
         double blue = b;

         double max = 1;
         if(red>max) max=red;
         if(green>max) max=green;
         if(blue>max) max=blue;

         r = (int)(red*255/max);
         g = (int)(green*255/max);
         b = (int)(blue*255/max);*/

        r/=1.97;
        g/=1.6;

        if(c==0) c=1;
        r=r*255/c;
        g=g*255/c;
        b=b*255/c;
        Log.d("COLORS", "r: " + r + " g: " + g + " b: " + b);

        return Color.rgb(r, g, b);
    }

    public double updateTemp() {
        /* Setup i2c buffer for the configuration register */
        i2cCommBuffer_temp[0] = MCP9800_CONFIG;
        i2cCommBuffer_temp[1] = MCP9800_12_BIT;
        int status = i2c_temp.write(fileHandle_temp, i2cCommBuffer_temp, 2);

        /* Setup mcp9800 register to read the temperature */
        i2cCommBuffer_temp[0] =MCP9800_TEMP;
        i2c_temp.write(fileHandle_temp, i2cCommBuffer_temp, 1);

        /* Read the current temperature from the mcp9800 device */
        i2c_temp.read(fileHandle_temp, i2cCommBuffer_temp, 2);

        /* Assemble the temperature values */
        Temperature = ((i2cCommBuffer_temp[0] << 8) | i2cCommBuffer_temp[1]);
        Temperature = Temperature >> 4;

        /* Convert current temperature to float */
        TempC = 1.0 * Temperature * 0.0625;

        /* Display actual temperature */
        return TempC;
    }

    protected void StopI2C()
    {
        i2c_color.close(fileHandle_color);
        i2c_temp.close(fileHandle_temp);
    }

    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            new Runnable() {
                @Override
                public void run() {
                    // TODO: Update color & temp here
                    //TEMPERATURE = updateTemp();
                    //COLOR = updateColor();
                }
            };
        }
    }
}
