package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "LegitSauceAutoSauce")
public class TesseractAuto extends LinearOpMode {
    DcMotor lift;
    @Override
    public void runOpMode() throws InterruptedException {
        Servo rTentacle = hardwareMap.servo.get("rTentacle");
        Servo lTentacle = hardwareMap.servo.get("lTentacle");

        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);

        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotor.Direction.REVERSE);

        MechanumChassis m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3"),
            hardwareMap.get(BNO055IMU.class, "imu"),
            this
        );

        // Set global tween time.
        m.setTweenTime(700);

        telemetry.log().add("Initializing Vuforia");
        VuforiaHelper vHelper = new VuforiaHelper(this);
        telemetry.log().add("Done Initializing Vuforia");


        char pos = 'A';
        String side = "RED";
        int sideModifier = 1;
        ColorSensor js = hardwareMap.colorSensor.get("jsRight");
        boolean jsConnected = false;
        while(!isStarted()) {

            // Side and position configuration.
            if(gamepad1.a) {
                pos = 'A';
                sleep(300);
            } else if (gamepad1.b) {
                pos = 'B';
                sleep(300);
            } else if (gamepad1.x) {
                side = "RED";
                js = hardwareMap.colorSensor.get("jsRight");
                sideModifier = 1;
                sleep(300);
            } else if (gamepad1.y) {
                side = "BLUE";
                js = hardwareMap.colorSensor.get("jsLeft");
                sideModifier = -1;
                sleep(300);
            }

            // Check for real values from the color sensor... This will catch and unplugged or misconfigured sensor.
            if(js.red() != 255 && js.red() != 0 && js.blue() != 0 && js.red() != 255) {
                jsConnected = true;
            }

            // Update the drive team.
            telemetry.addData("Position: ", pos);
            telemetry.addData("Side: ", side);
            telemetry.addData("Jewel Color Sensor Connected ", jsConnected);
            telemetry.update();
        }

        waitForStart();

        // Process the VuMark
        RelicRecoveryVuMark mark = vHelper.getVuMark();

        telemetry.log().add("DETECTED COLUMN: " + mark);

        // Strafe off motion setup.
        Vector2D testVec = new Vector2D(-1*sideModifier, 0);
        m.setDirectionVector(testVec);

        // Lower the tentacles.
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_DOWN);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);

        sleep(500);

        // Detect color and kick correct jewel.
        if(js.red() > js.blue()) {
            telemetry.log().add("JEWEL SENSOR SAW:::: RED");
            m.jewelKick(-1*sideModifier);
        } else {
            telemetry.log().add("JEWEL SENSOR SAW:::: BLUE");
            m.jewelKick(sideModifier); // essentially 1 * sideModifier
        }
        // Tentacles should initialize slightly out for teleop to ensure unobstructed lift
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP - .1);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP + .1);

        sleep(500);

        // Return to home heading after jewel kick.
        m.setRotationTarget(0);
        m.turnToTarget();

        telemetry.log().add("FINISHED RESETTING TO HOME ROTATION");

        if (pos == 'A') {
            switch (mark) {
                case LEFT:
                    m.run(1900, 0, 1);
                    break;
                case CENTER:
                    m.run(1500, 0, 1);
                    break;
                case RIGHT:
                    m.setTweenTime(0);
                    m.run(600, 0, 1);
                    m.setTweenTime(700);
                    break;
            }
        } else {
            telemetry.log().add("Executing on position B");
            testVec.SetComponents(0, 1);
            m.setDirectionVector(testVec);
            switch (mark) {
                case LEFT:
                    telemetry.log().add("CASE LEFT");
                    m.run(2400, 0, 1);
                    m.setRotationTarget(-90*sideModifier);
                    m.turnToTarget();
                    testVec.SetComponents(-1*sideModifier, 0);
                    m.setDirectionVector(testVec);
                    m.setTweenTime(0);
                    m.run(600, 0, 1);
                    m.setTweenTime(700);
                    break;
                case CENTER:
                    telemetry.log().add("CASE CENTER");
                    m.run(2400, 0, 1);
                    m.setRotationTarget(-90*sideModifier);
                    m.turnToTarget();
                    break;
                case RIGHT:
                    telemetry.log().add("CASE RIGHT");
                    m.run(1750, 0, 1);
                    m.setRotationTarget(-90*sideModifier);
                    m.turnToTarget();
                    break;
            }
        }
        if(pos == 'A') {
            testVec.SetComponents(0, 1);
            m.setDirectionVector(testVec);
            m.run(2200, 0, 1);
        } else {
            m.setTweenTime(0);
            testVec.SetComponents(0, 1);
            m.setDirectionVector(testVec);
            m.run(300, 0, 1);
            m.setTweenTime(700);
        }

        depositBlock();
        m.run(1000, 0, 1);
        testVec.SetComponents(0, -1);
        m.setDirectionVector(testVec);
        m.run(1000, 0, 1);
        testVec.SetComponents(0, 1);
        m.setDirectionVector(testVec);
        m.run(700, 0, 1);
    }

    void depositBlock() {
        lift.setPower(1);
        sleep(1000);
        lift.setPower(0);
    }
}
