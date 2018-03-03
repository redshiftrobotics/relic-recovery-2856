package org.firstinspires.ftc.teamcode.WS2801;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.Range;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by NoahR on 3/2/18.
 * Wrapper class for controlling a WS2801 led strip.
 */

public class WS2801 {
    List< Integer > pixelBuffer;
    private DigitalChannel clockPin, dataPin;
    private long LATCH_WAIT_TIME_US = 1000; // microseconds


    WS2801( DigitalChannel clockPin, DigitalChannel dataPin, int numPixels )
    {
        this.clockPin = clockPin;
        this.dataPin = dataPin;

        this.clockPin.setMode( DigitalChannel.Mode.OUTPUT );
        this.dataPin.setMode ( DigitalChannel.Mode.OUTPUT );

        // Create and populate pixel buffer
        pixelBuffer = Arrays.asList( new Integer[numPixels * 3] );
    }

    /**
     * Sets the time to wait inbetween messages
     * @param latchTime latch time in microseconds
     */
    void setLatchTime( long latchTime )
    {
        this.LATCH_WAIT_TIME_US = latchTime;
    }

    /**
     * Sets the color of a pixel in the internal buffer.
     * @param iPixel the address of the pixel
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     */
    void setPixel( int iPixel, Integer r, Integer g, Integer b )
    {
        if( iPixel + 2 < pixelBuffer.size() )
        {
            pixelBuffer.set( iPixel, Range.clip( r, 0, 255 ) );
            pixelBuffer.set( iPixel + 1, Range.clip( g, 0, 255 ) );
            pixelBuffer.set( iPixel + 2, Range.clip( b, 0, 255 ) );
        }
    }

    void display() throws InterruptedException
    {
        // Latch
        dataPin.setState ( false );
        clockPin.setState( false );
        TimeUnit.MICROSECONDS.sleep( LATCH_WAIT_TIME_US );

        // Send data
        for( Integer iByte : pixelBuffer )
        {
            boolean[] bits = toBinary( iByte, 8 );
            for( boolean bit : bits )
            {
                dataPin.setState( bit );
                clockPin.setState( true );
                clockPin.setState( false );
            }
        }
    }

    private static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = (1 << i & number) != 0;
        }
        return ret;
    }
}
