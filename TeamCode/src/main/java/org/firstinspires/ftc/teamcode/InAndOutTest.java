package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Arrays;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "Curve Run Test")
@Disabled
public class InAndOutTest extends LinearOpMode {
    MechanumChassis m;
    @Override
    public void runOpMode() {
        m = new MechanumChassis(hardwareMap, this);
        m.initializeWithIMU();
        waitForStart();

        m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_DOWN);
        m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);
        m.deployFlipper();
        m.lowerIntake();
        m.clawServo.setPosition(ServoValue.RELIC_CLAW_RELEASE);
        m.armServo.setPosition(ServoValue.RELIC_ARM_IN);
        m.deployAlignment(2);


        sleep(10000);
        m.setRotationTarget(0);
        m.lowerIntake();
        m.setDirectionVectorComponents(0, -1);
        adjustableThings[0] = 20000;
        adjustableThings[1] = 6f;
        adjustableThings[2] = 2f;
        adjustableThings[3] = .10f;
        int index = 0;
        while(opModeIsActive()) {
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

            if(gamepad1.x) {
//                m.runAlongCurve((long) adjustableThings[0], adjustableThings[3], adjustableThings[1],  (int) adjustableThings[2], true);
//                m.runAlongCurve((long) adjustableThings[0], adjustableThings[3], adjustableThings[1],  (int) adjustableThings[2], true);
            }

            if(gamepad1.y) {
                m.turnToTarget();
            }
            sleep(200);
        }

    }

    float[] adjustableThings = new float[4];
    public void adjustThing(int thingIndex, int direction) {
        if(thingIndex == 0) {
            adjustableThings[thingIndex] += 50 * direction;
        } else if (thingIndex == 3){
            adjustableThings[thingIndex] += 0.05f * direction;
        } else {
            adjustableThings[thingIndex] += 1f * direction;
        }
    }
}
