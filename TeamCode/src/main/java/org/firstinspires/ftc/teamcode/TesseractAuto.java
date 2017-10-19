package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "LegitSauceAutoSauce")
public class TesseractAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        OpticalDistanceSensor ods = hardwareMap.opticalDistanceSensor.get("ods");
        ColorSensor cs = hardwareMap.colorSensor.get("cs");

        MechanumChassis m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3"),
            hardwareMap.get(BNO055IMU.class, "imu"),
            this
        );

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        char pos = 'A';
        int column = 0;

        while(!opModeIsActive()) {
            if(gamepad1.a) {
                pos = 'A';
                sleep(300);
            } else if (gamepad1.b) {
                pos = 'B';
                sleep(300);
            } else if (gamepad1.left_bumper) {
                column--;
                sleep(300);
            } else if (gamepad1.right_bumper) {
                column++;
                sleep(300);
            }

            telemetry.addData("Position: ", pos);
            telemetry.addData("Column (left: 0, middle: 1, right: 2): ", column);
            telemetry.update();
            idle();
        }

        waitForStart();
        // Set global tween time.
        m.setTweenTime(700);

        // Strafe off motion
        Vector2D testVec = new Vector2D(-1, 0);
        m.setDirectionVector(testVec);

        if (pos == 'A') {
            switch (column) {
                case 0:
                    m.run(1900, 0, 1);
                    break;
                case 1:
                    m.run(1500, 0, 1);
                    break;
                case 2:
                    m.setTweenTime(0);
                    m.run(600, 0, 1);
                    m.setTweenTime(700);
                    break;
            }
        } else {
            m.setRotationTarget(-90);
            m.turnToTarget();
            switch (column) {
                case 0:
                    m.run(3600, 0, 1);
                    break;
                case 1:
                    m.run(3100, 0, 1);
                    break;
                case 2:
                    m.run(2500, 0, 1);
                    break;
            }
        }
        testVec.SetComponents(0, 1);
        m.setDirectionVector(testVec);

        if(pos == 'A') {
            m.run(2500, 0, 1);
        } else {
            m.run(1000, 0, 1);
        }

        testVec.SetComponents(0, -1);
        m.setDirectionVector(testVec);
        m.run(1000, 0, 1);
        testVec.SetComponents(0, 1);
        m.setDirectionVector(testVec);
        m.run(1000, 0, 1);
        testVec.SetComponents(0, -1);
        m.setDirectionVector(testVec);
        m.run(1000, 0, 1);

    }
}
