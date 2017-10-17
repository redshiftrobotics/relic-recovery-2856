package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "LegitSauceAutoSauce")
public class TesseractAuto extends LinearOpMode {
    MechanumChassis m;
    OpticalDistanceSensor ods;
    @Override
    public void runOpMode() throws InterruptedException {
        ods = hardwareMap.opticalDistanceSensor.get("ods");
        m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3"),
            hardwareMap.get(BNO055IMU.class, "imu"),
            this
        );
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        Vector2D testVec = new Vector2D(0, 1);
        m.setTweenTime(700);
        m.setDirectionVector(testVec);
        m.run(2000);
        testVec.SetComponents(0, -1);
        m.setDirectionVector(testVec);
        m.run(2000);
        sleep(5000);

//        Vector2D testVec = new Vector2D(0, 1);
//        m.setDirectionVector(testVec);
//        m.setTweenTime(700);
//        m.run(2500);
//        sleep(2000);
//        int pos = 1;
//        switch (pos) {
//            case 0:
//                testVec.SetComponents(-1, 0);
//                m.setDirectionVector(testVec);
//                m.run(1000);
//                break;
//            case 1:
//                testVec.SetComponents(-1, 0);
//                m.setDirectionVector(testVec);
//                m.run(1000);
//                break;
//            case 2:
//                testVec.SetComponents(-1, 0);
//                m.setDirectionVector(testVec);
//                m.run(1000);
//                break;
//        }
//        testVec.SetComponents(-0.5, 0);
//        m.columnAlign(ods);
    }
}
