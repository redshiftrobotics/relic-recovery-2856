package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by NoahR on 10/3/17.
 * Note about Modern Robotics I2C wiring. (Left to right)
 * 0 - 1 - 2 - 3
 * 5v - Data - Clock - gnd
 */

public class PixyCam
{
    I2cDeviceSynch m_deviceClient;
    private byte[] m_registerValues;
    private int m_dataStartAddress = 0;

    private final int START_ADDRESS = 0x54;
    private final int READ_LENGTH = 26; // Start read at 0x58, end at 0x58 + 26
    private final byte[] PIXY_DEVICE_SIGNATURE = {85, (byte) 170, 85, (byte) 170};
    private final int SIGNATURE_SPACE_SIZE = 10; // Device signature will be in the first 10 bytes

    PixyCam() {}
    PixyCam( I2cDeviceSynch deviceClient ) { initialize( deviceClient); }

    public void initialize( I2cDeviceSynch deviceClient )
    {
        m_deviceClient = deviceClient;
        m_deviceClient.engage();
    }

    public byte[] GetRegisterValues() { return m_registerValues; }

    /**
     * Update the cashed register values
     * @return whether the operation was successful
     */
    public boolean Update()
    {

        byte[] registerValues = m_deviceClient.read( START_ADDRESS, READ_LENGTH );
        byte[] signatureSpace = new byte[SIGNATURE_SPACE_SIZE];

        // Only use the first ten bytes for signature scanning
        for( int iIndex = 0; iIndex < SIGNATURE_SPACE_SIZE; iIndex++)
        {
            signatureSpace[iIndex] = (byte) mod(registerValues[iIndex], 256);
        }

        // If all signature space values are zero then we are not retrieving valid data.
        int signatureIndex = indexOfSubArray(signatureSpace, PIXY_DEVICE_SIGNATURE);
        if( signatureIndex > -1 )
        {
            m_registerValues = registerValues;
            m_dataStartAddress = signatureIndex;
            return true;
        }
        else
            return false;
    }

    /**
     * Update the register values. If the values are not available, halt the thread until they are.
     */
    public void ForceUpdate()
    {
        while(!Update());
    }

    public int GetDataStartAddress()
    {
        return m_dataStartAddress;
    }

    /**
     * @return the X coordinate of the center of the found object. Returns -1 if no data is
     * available from the pixy cam
     */
    public int GetXCenter() { return ReadSignedInt(PixyDataOffsets.X_CENTER);}

    /**
     * @return the Y coordinate of the center of the found object. Returns -1 if no data is
     * available from the pixy cam
     */
    public int GetYCenter() { return ReadSignedInt(PixyDataOffsets.Y_CENTER); }

    /**
     * @return the width of the found object. Returns -1 if no data is available from the pixy cam.
     */
    public int GetWidth() { return ReadSignedInt(PixyDataOffsets.WIDTH); }

    /**
     * @return the height of the found object. Returns -1 if no data is available from the pixy cam.
     */
    public int GetHeight() { return ReadSignedInt(PixyDataOffsets.HEIGHT); }

    /**
     * Java % operator is remainder not mod
     * @return result of modulus operation
     */
    private int mod(int x, int y)
    {
        int result = x % y;
        return result < 0? result + y : result;
    }

    /**
     * Given array and subArray, determines if subArray is in array
     * @return index of the subArray in array. Returns -1 if not present
     */
    public int indexOfSubArray(byte[] outerArray, byte[] smallerArray) {
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    /**
     * Resolved unsigned ints
     * @param offset The offset from the starting data position
     * @return Sum of ints read.
     */
    private int ReadSignedInt(int offset )
    {
        if( m_registerValues == null || m_registerValues.length == 0 || m_dataStartAddress == 0)
            return -1;

        // The byte after the value you are reading from is your "carry". E.g if the second byte is
        // 1 and the first byte is 24, the value of the int is (1 * 256) + 24 = 280
        return (mod(m_registerValues[m_dataStartAddress + offset + 1], 256) * 256) + mod(m_registerValues[m_dataStartAddress + offset], 256);
    }
    /**
     * Java style enums are weird (by which I mean they are not C-style enums).
     * This data structure contains the address offsets from the starting data position.
     */
    private static final class PixyDataOffsets
    {
        public static final int SYNC = 0;
        public static final int UNUSED = 4;
        public static final int X_CENTER = 8;
        public static final int Y_CENTER = 10;
        public static final int WIDTH = 12;
        public static final int HEIGHT = 14;
    }

    // Remove me
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}

