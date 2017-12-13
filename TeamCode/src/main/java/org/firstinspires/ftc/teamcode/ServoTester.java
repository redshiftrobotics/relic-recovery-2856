package org.firstinspires.ftc.teamcode;

import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by matt on 10/28/17.
 */

@TeleOp(name = "servotester")
public class ServoTester extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        float lv = .5f;
        float rv = .8f;
        Servo leftServo = hardwareMap.servo.get("clawServo");
        Servo rightServo = hardwareMap.servo.get("armServo");
        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("lv", lv);
            telemetry.addData("rv", rv);
            telemetry.update();
            if (gamepad1.a) {
                rv += 0.01f;
                sleep(500);
            } else if (gamepad1.b) {
                rv -= 0.01f;
                sleep(500);
            } else if (gamepad1.x) {
                lv += 0.01f;
                sleep(500);
            } else if (gamepad1.y) {
                lv -= 0.01f;
                sleep(500);
            }
            leftServo.setPosition(lv);
            rightServo.setPosition(rv);
            idle();
        }
    }
}
