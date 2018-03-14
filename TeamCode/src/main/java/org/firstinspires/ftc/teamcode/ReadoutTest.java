package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "[T1] Sensor Readout")
public class ReadoutTest extends LinearOpMode {
    float[] hsv = new float[3];
    MechanumChassis m;
    @Override
    public void runOpMode() {
        m = new MechanumChassis(hardwareMap, this);
        m.initializeWithIMU();
        waitForStart();
        m.setRotationTarget(0);
        m.setDirectionVectorComponents(1, 0);
        m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);
        m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_DOWN);
        float ration;
        while(opModeIsActive()) {
            ration = (float) m.lowerBlockCS.red() / (float) m.lowerBlockCS.blue();
            telemetry.addData("Right JS (R, G, B, distance)", m.js.red() + " " + m.js.green() + " " + m.js.blue() + " " + m.jsD.getDistance(DistanceUnit.CM));
            telemetry.addData("THE RATIO", Float.toString(ration));
            telemetry.addData("upper RGB", getColorsRGB(m.upperBlockCS));
            telemetry.addData("upper Alpha", m.upperBlockCS.alpha());
            telemetry.addData("lower RGB", getColorsRGB(m.lowerBlockCS));
            telemetry.addData("--", "--");
            telemetry.addData("upper HSV", getColorsHSV(m.upperBlockCS));
            telemetry.addData("lower HSV", getColorsHSV(m.lowerBlockCS));
            telemetry.addData("--", "--");
            telemetry.addData("lower Distance", m.lowerBlock.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
    }
    private String getColorsRGB(ColorSensor c) {
        return "R: " + c.red() + " G: " + c.green() + " B: " + c.blue();
    }

    private String getColorsHSV(ColorSensor c) {
        Color.RGBToHSV(c.red()*2, c.green()*2, c.blue()*2, hsv);
        return "H: " + hsv[0] + " S: " + hsv[1] + " V: " + hsv[2];
    }
}
