package ch.bfh.anuto.hardware;

public class I2C
{
    /**
     * @param deviceName
     *
     * @return return file handler else return <0 on fail
     */
    public native int open(String deviceName);


    /**
     * @param fileHandler
     * @param i2c_adr
     *
     * @return return file handler else return <0 on fail
     */
    public native int SetSlaveAddress(int fileHandler, int i2c_adr);

    /**
     * @param fileHandler
     * @param buffer
     * @param length
     *
     * @return Number of bytes read
     */
    public native int read(int fileHandler, int buffer[], int length);

    /**
     * @param fileHandler
     * @param buffer
     * @param length
     *
     * @return Number of bytes written
     */
    public native int write(int fileHandler, int buffer[], int length);


    /**
     * @param fileHandler
     *
     * @return -
     */
    public native void close(int fileHandler);

    static
    {
        System.loadLibrary("i2c");
    }
}
