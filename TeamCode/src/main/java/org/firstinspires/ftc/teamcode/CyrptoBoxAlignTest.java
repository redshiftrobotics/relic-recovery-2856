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
    OpticalDistanceSensor roptical;
    boolean detectedCryptBox;
    boolean alignedWithCryptoBox = false;
    double triggerValue = 0.06;
    @Override
    public void init() {
        loptical = hardwareMap.opticalDistanceSensor.get("ls");
        roptical = hardwareMap.opticalDistanceSensor.get("rs");
    }

    @Override
    public void loop() {
        // assumes approach from left for testing purposes
        //alignedWithCryptoBox = (loptical.getLightDetected() < triggerValue && roptical.getLightDetected() < triggerValue);
        // telemetry.addData("hasDetected", detectedCryptBox);
        if (loptical.getLightDetected() > triggerValue) {
            alignedWithCryptoBox = true;
        } else if (loptical.getLightDetected() > 0.5) {
            alignedWithCryptoBox = false;
        }
        telemetry.addData("detected", alignedWithCryptoBox);
        telemetry.addData("left ods:", loptical.getLightDetected());
        telemetry.addData("right ods:", roptical.getLightDetected());
        telemetry.update();
    }
}
