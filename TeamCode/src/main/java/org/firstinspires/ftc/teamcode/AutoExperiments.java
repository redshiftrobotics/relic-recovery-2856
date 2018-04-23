package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Arrays;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "AutoTests")
public class AutoExperiments extends LinearOpMode {
    float[] adjustableThings = new float[4];
    public void adjustThing(int thingIndex, int direction) {
        if(thingIndex == 0 || thingIndex == 3) {
            adjustableThings[thingIndex] += 50 * direction;
        } else {
            adjustableThings[thingIndex] += 0.05f * direction;
        }
    }

    MechanumChassis m;
    @Override
    public void runOpMode() throws InterruptedException {
        // [time, start, end, tweenTime]
        adjustableThings[0] = 1000;
        adjustableThings[1] = 0f;
        adjustableThings[2] = 1f;
        adjustableThings[3] = 700;
        m = new MechanumChassis(hardwareMap, this);
        long time = 1000;
        float startSpeed = 0f;
        m.initializeWithIMU();
        waitForStart();
        m.setRotationTarget(0);
        m.setDirectionVectorComponents(0, 1);
        telemetry.addData("time", time);
        int index = 0;
        while(this.opModeIsActive()) {
//            if(gamepad1.left_bumper) {
//                m.setRotationTarget(-90);
//                m.turnToTarget();
//            }
//            if(gamepad1.right_bumper) {
//                m.setRotationTarget(90);
//                m.turnToTarget();
//            }


            if(gamepad1.left_bumper) {
                index++;
            } else if (gamepad1.right_bumper) {
                index--;
            }
            if(gamepad1.dpad_up) {
                adjustThing(index, 1);
            } else if (gamepad1.dpad_down) {
                adjustThing(index, -1);
            }
            telemetry.addData("modified parameter index", index);
            telemetry.addData("Params: ", Arrays.toString(adjustableThings));
            telemetry.update();

            if(gamepad1.left_stick_x > 0.1) {
                telemetry.addData("direction", "left");
                m.setDirectionVectorComponents(-1, 0);
            }
            if(gamepad1.left_stick_x < -0.1) {
                telemetry.addData("direction", "right");
                m.setDirectionVectorComponents(1, 0);
            }
            if(gamepad1.left_stick_y < -0.1) {
                telemetry.addData("direction", "forward");
                m.setDirectionVectorComponents(0, 1);
            }
            if(gamepad1.left_stick_y > 0.1) {
                telemetry.addData("direction", "backward");
                m.setDirectionVectorComponents(0, -1);
            }

            if(gamepad1.x) {
                telemetry.log().add("running");
                m.setTweenTime(adjustableThings[3]);
                m.run((long) adjustableThings[0], adjustableThings[1], adjustableThings[2]);
//                m.setRotationTarget(90);
//                m.turnToTarget();
            }
            telemetry.update();
            sleep(100);
        }
    }
}
