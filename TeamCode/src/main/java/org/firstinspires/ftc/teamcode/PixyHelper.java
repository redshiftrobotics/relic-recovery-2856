package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

/**
 * Created by matt on 10/3/17.
 */

public class PixyHelper {
    I2cDeviceSynch pixy;
    int zeroCheck = 0;
    int start = 0;
    boolean found = false;
    byte[] readCache;

    PixyHelper(I2cDeviceSynch pixy, LinearOpMode context) {
        this.pixy = pixy;
        pixy = context.hardwareMap.i2cDeviceSynch.get("pixyCam");
        pixy.engage();
    }


}
