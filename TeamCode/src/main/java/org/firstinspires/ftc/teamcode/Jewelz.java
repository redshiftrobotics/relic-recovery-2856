package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by matt on 3/13/18.
 */

@Autonomous(name = "Jewel Processing Test")
@Disabled
public class Jewelz extends LinearOpMode {
    private VuforiaHelper vHelper;
    private Bitmap forProc;
    @Override
    public void runOpMode() throws InterruptedException {
        vHelper = new VuforiaHelper(this);
        JewelProcessor jp = new JewelProcessor(vHelper);
        int loops = 0;
        String color;
        while(!isStarted() && !isStopRequested()) {
            loops++;
            color = jp.isLeftJewelRed() ? "RED" : "BLUE";
            telemetry.addData("color: ", color);
            telemetry.addData("# of images: ", loops);
            telemetry.update();
        }

    }
}
