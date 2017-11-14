package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by matt on 11/1/17.
 */

@Autonomous (name="cs test")
public class CSTest extends OpMode {
    ColorSensor jsR;
    ColorSensor jsL;
    @Override
    public void init() {
         jsR = hardwareMap.colorSensor.get("jsRight");
         jsL = hardwareMap.colorSensor.get("jsLeft");
    }

    @Override
    public void loop() {
        telemetry.addData("right", jsR.red() + " - " + jsR.blue());
        telemetry.addData("left", jsL.red() + " - " + jsL.blue());
        telemetry.update();

    }
}
