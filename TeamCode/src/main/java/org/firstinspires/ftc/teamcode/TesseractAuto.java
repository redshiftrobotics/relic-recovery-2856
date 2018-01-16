package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
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

    DcMotor lCollect;
    DcMotor rCollect;


    private Servo armServo;
    private Servo clawServo;
    private Servo armExtensionServo;

    private MechanumChassis m;
    private Vector2D moveVec;

    private ColorSensor jsL;
    private ColorSensor jsR;
    private ColorSensor js;
    private boolean jsConnected = false;

    private DistanceSensor upperBlock;

    private static final int TWEEN_TIME = 700;
    private static final int SERVO_DEPLOYMENT_TIME = 500;

    private static final long CENTER_MOVE_TIME = 2700;
    private static final long FAR_OFFSET = 400;
    private static final long NEAR_OFFSET = -400;
    private static final long CENTER_OFFSET = 0;

    private static final long A_FAR_OFFSET = 2400;
    private static final long A_CENTER_OFFSET = 1800;
    private static final long A_NEAR_OFFSET = 1250;

    private VuforiaHelper vHelper;

    private StartPosition startPos = StartPosition.BLUE_B;
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

        m.setRotationTarget(0);


        // Kick the jewel off.
        doJewel();

//        m.turnToTarget();
        navigateToColumn(mark);



        depositBlock();
        lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);

        sleep(4000);

        if (startPos == StartPosition.BLUE_B || startPos == StartPosition.RED_B) {
            collectBlocks();
            rLift.setPower(0);
            lLift.setPower(0);
            sleep(4000);
            // get block to sensor
            while((upperBlock.getDistance(DistanceUnit.CM) > 15 || Double.isNaN(upperBlock.getDistance(DistanceUnit.CM))) && opModeIsActive()) {
                rLift.setPower(1);
                lLift.setPower(-1);
            }
            rLift.setPower(0);
            lLift.setPower(0);
            sleep(4000);
            // run until sensor no longer sees block
            while(!(upperBlock.getDistance(DistanceUnit.CM) > 15 || Double.isNaN(upperBlock.getDistance(DistanceUnit.CM))) && opModeIsActive()) {
                rLift.setPower(1);
                lLift.setPower(-1);
            }
            rLift.setPower(0);
            lLift.setPower(0);
            sleep(4000);
            depositBlock();
        }

    }

    void initialize() {
        lFlip = hardwareMap.servo.get("lFlip");
        rFlip = hardwareMap.servo.get("rFlip");
        lFlip.setPosition(ServoValue.LEFT_FLIP_UP);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_UP);


        armServo = hardwareMap.servo.get("armServo");
        armServo.setPosition(ServoValue.RELIC_ARM_STORAGE);
        clawServo = hardwareMap.servo.get("clawServo");
        clawServo.setPosition(ServoValue.RELIC_CLAW_IN);

        armExtensionServo = hardwareMap.servo.get("armExtension");
        armExtensionServo.setPosition(ServoValue.RELIC_ARM_EXTENSION_IN);

        lLift = hardwareMap.dcMotor.get("lBelting");
        rLift = hardwareMap.dcMotor.get("rBelting");

        lCollect = hardwareMap.dcMotor.get("lCollect");
        rCollect = hardwareMap.dcMotor.get("rCollect");

        // Initialize Vuforia
        telemetry.log().add("Initializing Vuforia...");
        vHelper = new VuforiaHelper(this);
        telemetry.log().add("Done Initializing Vuforia");

        // Get hardware devices
        rTentacle = hardwareMap.servo.get("rTentacle");
        lTentacle = hardwareMap.servo.get("lTentacle");

//        hardwareMap.servo.get("magic").setPosition(ServoValue.MAGIC_IN);

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

        upperBlock = hardwareMap.get(DistanceSensor.class, "upperBlock");
    }

    void depositBlock() {
        m.powerConstant = 0.5f * 35/45;
        m.setTweenTime(0);
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(1500, 0, 1);
        rLift.setPower(1);
        lLift.setPower(-1);
        // allow block to reach top before backing
        sleep(500);

        m.powerConstant = 0.25f * 35/45;
        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(1600, 0, 1);
        rLift.setPower(0);
        lLift.setPower(0);
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(1000, 0, 1);
        m.powerConstant = 0.9f * 35/45;
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
            telemetry.addData("Upper Block Sensor: ", upperBlock.getDistance(DistanceUnit.CM));
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
            // Tentacles should initialize slightly out for teleop to ensure unobstructed lift
            lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
            rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);
            sleep(500);
            m.jewelBack(-1);
        } else {
            telemetry.log().add("JEWEL SENSOR SAW:::: BLUE");
            m.jewelKick(1);
            // Tentacles should initialize slightly out for teleop to ensure unobstructed lift
            lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
            rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);
            sleep(500);
            m.jewelBack(1);
        }

        // Return to home heading after jewel kick.
        m.setRotationTarget(0);
//        m.turnToTarget();

        telemetry.log().add("FINISHED RESETTING TO HOME ROTATION");
    }

    private void navigateToColumn(RelicRecoveryVuMark mark) {

        long unknownDefault = CENTER_OFFSET;

        telemetry.log().add("Executing on position B");
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);

        // B POSITION

        if (startPos == StartPosition.BLUE_B) {
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
        } else if (startPos == StartPosition.RED_B) {
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

        // A POSITION
        if (startPos == StartPosition.BLUE_A || startPos == StartPosition.RED_A) {
            m.run(2300, 0, 1);
            moveVec.SetComponents(-1*sideModifier, 0);
            m.setDirectionVector(moveVec);
        }

        if (startPos == StartPosition.BLUE_A) {
            switch (mark) {
                case LEFT:
                    m.run(A_NEAR_OFFSET, 0, 1);
                    break;
                case CENTER:
                    m.run(A_CENTER_OFFSET, 0, 1);
                    break;
                case RIGHT:
                    m.run(A_FAR_OFFSET, 0, 1);
                    break;
                case UNKNOWN:
                    m.run(A_NEAR_OFFSET, 0, 1);
                    break;
            }
        } else if (startPos ==  StartPosition.RED_A) {
            switch (mark) {
                case LEFT:
                    m.run(A_FAR_OFFSET, 0, 1);
                    break;
                case CENTER:
                    m.run(A_CENTER_OFFSET, 0, 1);
                    break;
                case RIGHT:
                    m.run(A_NEAR_OFFSET, 0, 1);
                    break;
                case UNKNOWN:
                    m.run(A_CENTER_OFFSET, 0, 1);
                    break;
            }
        }


    }

    private void collectBlocks() {

        /**
         *  New solution... Go forward running intake and belting until a block is seen by the "lowerBlock" sensor. Then simultaneously
         *  - Run belting until "upperBlock" sees a block.
         *  - When "lowerBlock" no longer sees a block, go forward running INTAKE ONLY (belting should be controlled by the aforementioned other check) until a block is seen by the "lowerBlock" sensor.
         *  As soon as the second block is seen in the intake, all of the above can be terminated. The robot should then return to the crypto box and
         *  PUSH AGAINST THE FIRST BLOCK and back off a certain encoder count (or better yet, use a REV distance sensor to get the value by looking at the first scored block).
         *  The distance between the robot and the first scored block should now be such that running the belting with NO EXTRA MOVEMENTS, can score BOTH BLOCKS...
         *  So the final action is to run the belting and intake while staying still, and finishing with a super silky push in and back away.
         */



        lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);

        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);

        rLift.setPower(1);
        lLift.setPower(-1);
        lCollect.setPower(.8);
        rCollect.setPower(-.8);

        m.powerConstant = 0.9f * 35/45;
        m.run(1900, 0, 1);

        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(800, 0, 1);

        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(1200, 0, 1);


        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(2300, 0, 1);

        rLift.setPower(0);
        lLift.setPower(0);
        lCollect.setPower(0);
        rCollect.setPower(0);
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
