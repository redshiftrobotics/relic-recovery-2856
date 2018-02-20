package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "AutoTests")
public class AutoExperiments extends LinearOpMode {

    MechanumChassis m;
    @Override
    public void runOpMode() throws InterruptedException {
        m = new MechanumChassis(hardwareMap, this);
        long time = 1000;
        float startSpeed = 1f;
        m.initializeWithIMU();
        waitForStart();
        m.setRotationTarget(0);
        m.setDirectionVectorComponents(-1, 0);
        telemetry.addData("time", time);
        while(this.opModeIsActive()) {
            if(gamepad1.dpad_up) time+= 100;
            if(gamepad1.dpad_down) time-= 100;
            if(gamepad1.dpad_left) startSpeed-=0.1;
            if(gamepad1.dpad_right) startSpeed+=0.1;
            if(gamepad1.x) {
                telemetry.log().add("running");
                m.run(time, startSpeed, 1);
            }
            telemetry.addData("time", time);
            telemetry.addData("time", startSpeed);
            telemetry.update();
            sleep(100);
        }
    }
}
