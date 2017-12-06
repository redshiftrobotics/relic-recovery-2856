package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by matt on 11/18/17.
 */
@Autonomous(name= "Turn Tester")
public class TurnTest extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize mechanum chassis.
        MechanumChassis m = new MechanumChassis(
                hardwareMap.dcMotor.get("m0"),
                hardwareMap.dcMotor.get("m1"),
                hardwareMap.dcMotor.get("m2"),
                hardwareMap.dcMotor.get("m3"),
                hardwareMap.get(BNO055IMU.class, "imu"),
                this
        );

        m.debugModeEnabled = true;
        // Set global tween time.
        m.setTweenTime(700);

        waitForStart();

        m.setRotationTarget(90);
        m.turnToTarget();

    }
}
