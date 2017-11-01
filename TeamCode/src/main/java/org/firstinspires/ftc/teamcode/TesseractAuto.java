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

        VuforiaHelper vHelper = new VuforiaHelper(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        char pos = 'A';
        String side = "RED";
        int sideModifier = 1;

        ColorSensor js = hardwareMap.colorSensor.get("jsRight");

        while(!opModeIsActive()) {
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

            telemetry.addData("Position: ", pos);
            telemetry.addData("Side: ", side);
            telemetry.update();
            idle();
        }

        waitForStart();

        // Process the VuMark
        RelicRecoveryVuMark mark = vHelper.getVuMark();

        // Set global tween time.
        m.setTweenTime(700);

        // Strafe off motion
        Vector2D testVec = new Vector2D(-1*sideModifier, 0);
        m.setDirectionVector(testVec);

        if (side.equals("BLUE")) {
            lTentacle.setPosition(ServoValue.LEFT_TENTACLE_DOWN);
        } else {
            rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);
        }


        if(js.blue() > js.red()) {
            m.setRotationTarget(10*sideModifier);
        } else {
            m.setRotationTarget(-10*sideModifier);
        }

        m.turnToTarget();
        m.setRotationTarget(0);
        m.turnToTarget();

        hardwareMap.servo.get("lTentacle").setPosition(ServoValue.LEFT_TENTACLE_UP + .1);
        hardwareMap.servo.get("rTentacle").setPosition(ServoValue.RIGHT_TENTACLE_UP - .1);


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
            m.setRotationTarget(-90 * sideModifier);
            m.turnToTarget();
            switch (mark) {
                case LEFT:
                    m.run(3600, 0, 1);
                    break;
                case CENTER:
                    m.run(3100, 0, 1);
                    break;
                case RIGHT:
                    m.run(2500, 0, 1);
                    break;
            }
        }
        testVec.SetComponents(0, 1);
        m.setDirectionVector(testVec);

        if(pos == 'A') {
            m.run(2200, 0, 1);
        } else {
            m.run(700, 0, 1);
        }

        depositBlock();
        m.run(1000, 0, 1);
    }

    void depositBlock() {
        lift.setPower(1);
        sleep(1000);
        lift.setPower(0);
    }
}
