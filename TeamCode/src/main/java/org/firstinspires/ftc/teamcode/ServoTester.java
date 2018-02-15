package org.firstinspires.ftc.teamcode;

import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by matt on 10/28/17.
 */

@TeleOp(name = "servotester")
public class ServoTester extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {


        float increment = 0.1f;
        float lv = 0f;
        float rv = .8f;
        Servo leftServo = hardwareMap.servo.get("topAlign");
        Servo rightServo = hardwareMap.servo.get("rAlignServo");
        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("lv", lv);
            telemetry.addData("rv", rv);
            telemetry.update();
            if (gamepad1.left_bumper || gamepad1.right_bumper) {
                increment = 0.01f;
            } else {
                increment = 0.1f;
            }
            if (gamepad1.a) {
                rv += increment;
                sleep(200);
            } else if (gamepad1.b) {
                rv -= increment;
                sleep(200);
            } else if (gamepad1.x) {
                lv += increment;
                sleep(200);
            } else if (gamepad1.y) {
                lv -= increment;
                sleep(200);
            }
            leftServo.setPosition(lv);
            rightServo.setPosition(rv);
            idle();
        }
    }
}
