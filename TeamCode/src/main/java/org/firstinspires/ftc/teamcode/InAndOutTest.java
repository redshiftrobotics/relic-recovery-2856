package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "[T4] In and Out Test")
public class InAndOutTest extends LinearOpMode {

    MechanumChassis m;
    @Override
    public void runOpMode() {
        m = new MechanumChassis(hardwareMap, this);
        m.initializeWithIMU();
        waitForStart();
        m.setRotationTarget(0);
        m.lowerIntake();
        while(opModeIsActive()){
            if (gamepad1.x) {
                m.lCollect.setPower(-.8);
                m.rCollect.setPower(.8);
                sleep(1000);
            }
        }
    }
}
