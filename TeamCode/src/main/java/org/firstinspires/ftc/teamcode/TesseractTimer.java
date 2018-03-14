package org.firstinspires.ftc.teamcode;

/**
 * Created by NoahR on 2/20/18.
 * Lightweight timer class
 */

public class TesseractTimer {
    private long timeout;

    public TesseractTimer( long deltaTime )
    {
        timeout = System.currentTimeMillis() + deltaTime;
    }

    public boolean hasReachedTimeout()
    {
        return this.timeout < System.currentTimeMillis();
    }

    public long getMillisecondsRemaining() { return this.timeout - System.currentTimeMillis(); }
}
