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
    private Servo rTentacle;
    private Servo lTentacle;

    private Servo lFlip;
    private Servo rFlip;

    DcMotor lLift;
    DcMotor rLift;

    private MechanumChassis m;
    private Vector2D moveVec;

    private ColorSensor jsL;
    private ColorSensor jsR;
    private ColorSensor js;
    private boolean jsConnected = false;

    private static final int TWEEN_TIME = 700;
    private static final int SERVO_DEPLOYMENT_TIME = 500;

    private static final long CENTER_MOVE_TIME = 2700;
    private static final long FAR_OFFSET = 400;
    private static final long NEAR_OFFSET = -400;
    private static final long CENTER_OFFSET = 0;

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

//        m.hackTurn(-1);
//        m.hackTurn(1);

        // Kick the jewel off.
//        doJewel();
        navigateToColumn(mark);
        depositBlock();
    }

    void initialize() {
        lFlip = hardwareMap.servo.get("lFlip");
        rFlip = hardwareMap.servo.get("rFlip");
        lFlip.setPosition(ServoValue.LEFT_FLIP_UP);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_UP);

        lLift = hardwareMap.dcMotor.get("lBelting");
        rLift = hardwareMap.dcMotor.get("rBelting");

        // Initialize Vuforia
        telemetry.log().add("Initializing Vuforia...");
        vHelper = new VuforiaHelper(this);
        telemetry.log().add("Done Initializing Vuforia");

        // Get hardware devices
        rTentacle = hardwareMap.servo.get("rTentacle");
        lTentacle = hardwareMap.servo.get("lTentacle");

        hardwareMap.servo.get("magic").setPosition(ServoValue.MAGIC_IN);

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
        m.powerConstant = 0.5f;
        m.setTweenTime(0);
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(1500, 0, 1);
        rLift.setPower(1);
        lLift.setPower(-1);
        // allow block to reach top before backing
        sleep(500);

        m.powerConstant = 0.25f;
        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(1600, 0, 1);
        rLift.setPower(0);
        lLift.setPower(0);
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(1000, 0, 1);
        m.powerConstant = 0.9f;
        m.setTweenTime(TWEEN_TIME);
        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(700, 0, 1);

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
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);

        // Return to home heading after jewel kick.
//        m.setRotationTarget(0);
//        m.turnToTarget();

        telemetry.log().add("FINISHED RESETTING TO HOME ROTATION");
    }

    private void navigateToColumn(RelicRecoveryVuMark mark) {

        long unknownDefault = FAR_OFFSET;

        telemetry.log().add("Executing on position B");
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);

        if (startPos == StartPosition.BLUE_A || startPos == StartPosition.BLUE_B) {
            switch (mark) {
                case LEFT:
                    balanceToColumn(NEAR_OFFSET);
                    break;
                case CENTER:
                    balanceToColumn(CENTER_OFFSET);
                    break;
                case RIGHT:
                    balanceToColumn(FAR_OFFSET);
                    break;
                case UNKNOWN:
                    balanceToColumn(unknownDefault);
                    break;
            }
        } else {
            switch (mark) {
                case LEFT:
                    balanceToColumn(FAR_OFFSET);
                    break;
                case CENTER:
                    balanceToColumn(CENTER_OFFSET);
                    break;
                case RIGHT:
                    balanceToColumn(NEAR_OFFSET);
                    break;
                case UNKNOWN:
                    balanceToColumn(unknownDefault);
                    break;
            }
        }
    }

    private void balanceToColumn(long columnOffset) {
        m.run(CENTER_MOVE_TIME + columnOffset, 0, 1);
        telemetry.log().add("Finished running, starting turn");
        m.setRotationTarget(90 * sideModifier);
        m.turnToTarget();
    }

    private enum StartPosition {
        RED_A, RED_B, BLUE_A, BLUE_B
    }

}
