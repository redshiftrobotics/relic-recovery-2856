package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "LegitSauceAutoSauce")
public class TesseractAuto extends LinearOpMode {
    private DcMotor lift;
    private Servo rTentacle;
    private Servo lTentacle;
    private MechanumChassis m;
    private Vector2D moveVec;

    private ColorSensor jsL;
    private ColorSensor jsR;
    private ColorSensor js;
    private boolean jsConnected = false;

    private static final int TWEEN_TIME = 700;
    private static final int LIFT_DEPOSIT_TIME = 1000;
    private static final int SERVO_DEPLOYMENT_TIME = 500;
    private static final int HOME_HEADING = 0;

    private VuforiaHelper vHelper;

    private StartPosition startPos = StartPosition.RED_B;
    private int sideModifier = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        configurationLoop();
        waitForStart();

        // Process the VuMark
        RelicRecoveryVuMark mark = vHelper.getVuMark();
        telemetry.log().add("DETECTED COLUMN: " + mark);

        // Kick the jewel off.
        doJewel();
        navigateToColumn(mark);
        depositBlock();
        safetyPush(); // to ensure block is in column
    }

    void initialize() {
        // Initialize Vuforia
        telemetry.log().add("Initializing Vuforia...");
        vHelper = new VuforiaHelper(this);
        telemetry.log().add("Done Initializing Vuforia");

        // Get hardware devices
        rTentacle = hardwareMap.servo.get("rTentacle");
        lTentacle = hardwareMap.servo.get("lTentacle");
        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotor.Direction.REVERSE);

        // Initialize mechanum chassis.
        m = new MechanumChassis(
                hardwareMap.dcMotor.get("m0"),
                hardwareMap.dcMotor.get("m1"),
                hardwareMap.dcMotor.get("m2"),
                hardwareMap.dcMotor.get("m3"),
                hardwareMap.get(BNO055IMU.class, "imu"),
                this
        );
        m.debugModeEnabled = true;
        // Set global tween time.
        m.setTweenTime(TWEEN_TIME);

        // Strafe off motion setup.
        moveVec = new Vector2D(-1*sideModifier, 0);
        m.setDirectionVector(moveVec);

        // Initialize servos up.
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);

        jsL = hardwareMap.colorSensor.get("jsLeft");
        jsR = hardwareMap.colorSensor.get("jsRight");
        js = jsR;
    }

    void depositBlock() {
        lift.setPower(1);
        sleep(LIFT_DEPOSIT_TIME);
        lift.setPower(0);
    }

    private int getSideCoefficient(StartPosition pos) {
        return (pos == StartPosition.BLUE_A || pos == StartPosition.BLUE_B) ? -1 : 1;
    }

    private void hardwareValidation() {
        // Check for real values from the color sensor... This will catch and unplugged or misconfigured sensor.
        if (js.red() != 255 && js.red() != 0 && js.blue() != 0 && js.red() != 255) {
            jsConnected = true;
        }
    }

    private void configurationLoop() {
        while(!isStarted()) {
            // Side and position configuration.
            if(gamepad1.a) {
                startPos = StartPosition.BLUE_A;
                js = jsL;
            } else if (gamepad1.b) {
                startPos = StartPosition.BLUE_B;
                js = jsL;
                sideModifier = -1;
            } else if (gamepad1.x) {
                startPos = StartPosition.RED_A;
                js = jsR;
            } else if (gamepad1.y) {
                startPos = StartPosition.RED_B;
                js = jsR;
            }

            sideModifier = getSideCoefficient(startPos);

            hardwareValidation();

            // Update the drive team.
            telemetry.addData("Starting Position: ", startPos);
            telemetry.addData("Jewel Color Sensor Connected ", jsConnected);
            telemetry.update();
        }
    }

    private void doJewel() {

        // Lower the tentacles.
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_DOWN);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);

        sleep(SERVO_DEPLOYMENT_TIME);

        // Detect color and kick correct jewel. No need to side modify these!!!
        if(js.red() > js.blue()) {
            telemetry.log().add("JEWEL SENSOR SAW:::: RED");
            m.jewelKick(-1);
        } else {
            telemetry.log().add("JEWEL SENSOR SAW:::: BLUE");
            m.jewelKick(1);
        }
        // Tentacles should initialize slightly out for teleop to ensure unobstructed lift
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP - .1);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP + .1);

        // Return to home heading after jewel kick.
        m.setRotationTarget(HOME_HEADING);
        m.turnToTarget();

        telemetry.log().add("FINISHED RESETTING TO HOME ROTATION");
    }

    private void navigateToColumn(RelicRecoveryVuMark mark) {
        if (startPos == StartPosition.BLUE_A || startPos == StartPosition.RED_A) {
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
                    m.setTweenTime(TWEEN_TIME);
                    break;
            }
        } else {
            telemetry.log().add("Executing on position B");
            moveVec.SetComponents(0, 1);
            m.setDirectionVector(moveVec);
            switch (mark) {
                case LEFT:
                    if (startPos == StartPosition.BLUE_A || startPos == StartPosition.BLUE_B) {
                        telemetry.log().add("CASE RIGHT");
                        m.run(1750, 0, 1);
                        telemetry.log().add("Finished running, starting turn");
                        m.setRotationTarget(-90 * sideModifier);
                        m.turnToTarget();
                    } else {
                        telemetry.log().add("CASE LEFT");
                        m.run(2400, 0, 1);
                        m.setRotationTarget(-90 * sideModifier);
                        m.turnToTarget();
                        moveVec.SetComponents(-1 * sideModifier, 0);
                        m.setDirectionVector(moveVec);
                        m.setTweenTime(0);
                        m.run(600, 0, 1);
                        m.setTweenTime(TWEEN_TIME);
                    }
                    break;
                case CENTER:
                    telemetry.log().add("CASE CENTER");
                    m.run(2400, 0, 1);
                    telemetry.log().add("Finished running, starting turn");
                    m.setRotationTarget(-90 * sideModifier);
                    m.turnToTarget();
                    break;
                case RIGHT:
                    if (startPos == StartPosition.BLUE_A || startPos == StartPosition.BLUE_B) {
                        telemetry.log().add("CASE LEFT");
                        m.run(2400, 0, 1);
                        m.setRotationTarget(-90 * sideModifier);
                        m.turnToTarget();
                        moveVec.SetComponents(-1 * sideModifier, 0);
                        m.setDirectionVector(moveVec);
                        m.setTweenTime(0);
                        m.run(600, 0, 1);
                        m.setTweenTime(TWEEN_TIME);
                    } else {
                        telemetry.log().add("CASE RIGHT");
                        m.run(1750, 0, 1);
                        telemetry.log().add("Finished running, starting turn");
                        m.setRotationTarget(-90 * sideModifier);
                        m.turnToTarget();
                    }
                    break;
            }
        }
        if(startPos == StartPosition.BLUE_A || startPos == StartPosition.RED_A) {
            moveVec.SetComponents(0, 1);
            m.setDirectionVector(moveVec);
            m.run(2200, 0, 1);
        } else {
            m.setTweenTime(0);
            moveVec.SetComponents(0, 1);
            m.setDirectionVector(moveVec);
            m.run(300, 0, 1);
            m.setTweenTime(TWEEN_TIME);
        }
    }

    private void safetyPush() {
        m.run(1000, 0, 1);
        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(1000, 0, 1);
        depositBlock();
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(TWEEN_TIME, 0, 1);
    }

    private enum StartPosition {
        RED_A, RED_B, BLUE_A, BLUE_B
    }

}
