package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by matt on 11/18/17.
 */
@Autonomous(name= "Turn Tester")
public class TurnTest extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        /*

        DcMotor m0 = hardwareMap.dcMotor.get("m0");
        DcMotor m1 = hardwareMap.dcMotor.get("m1");
        DcMotor m2 = hardwareMap.dcMotor.get("m2");
        DcMotor m3 = hardwareMap.dcMotor.get("m3");

        m1.setDirection(DcMotor.Direction.REVERSE);
        m2.setDirection(DcMotor.Direction.REVERSE);

        // As of 12/6/17 this goes forward. (Assuming block deposit is front and collection at back)
        m0.setPower(1);
        m1.setPower(1);
        m2.setPower(1);
        m3.setPower(1);
        sleep(2000);


        // As of 12/6/17 this turns left. (Assuming block deposit is front and collection at back)
        m0.setPower(-1);
        m1.setPower(1);
        m2.setPower(1);
        m3.setPower(-1);
        sleep(2000);

        */

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

        m.setRotationTarget(-90);
        m.turnToTarget();

    }
}
