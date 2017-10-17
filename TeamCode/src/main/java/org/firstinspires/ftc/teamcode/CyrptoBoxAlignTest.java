package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by matt on 9/23/17.
 */

@Autonomous(name = "CyrptoAlignTest")
public class CyrptoBoxAlignTest extends OpMode {
    OpticalDistanceSensor loptical;
    @Override
    public void init() {
        loptical = hardwareMap.opticalDistanceSensor.get("ods");
    }

    @Override
    public void loop() {
        // assumes approach from left for testing purposes
        //alignedWithCryptoBox = (loptical.getLightDetected() < triggerValue && roptical.getLightDetected() < triggerValue);
        // telemetry.addData("hasDetected", detectedCryptBox);
        telemetry.addData("ods:", (loptical.getLightDetected() - .9)*1024);
        telemetry.update();
    }
}
