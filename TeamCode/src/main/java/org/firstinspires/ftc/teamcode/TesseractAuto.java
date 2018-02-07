package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.blockplacer.BlockColors;
import org.firstinspires.ftc.teamcode.blockplacer.BlockPlacer;
import org.firstinspires.ftc.teamcode.blockplacer.BlockPlacerTree;
import org.firstinspires.ftc.teamcode.blockplacer.CryptoboxColumns;

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

    DcMotor lCollect;
    DcMotor rCollect;


    private Servo armServo;
    private Servo clawServo;

    private MechanumChassis m;
    private Vector2D moveVec;

    private ColorSensor jsL;
    private ColorSensor jsR;
    private ColorSensor js;
    private boolean jsConnected = false;

    private DistanceSensor upperBlock;
    private ColorSensor upperBlockCS;
    BlockPlacer bop;

    private Servo rAlignServo;
    private Servo topAlign;
    private DigitalChannel sideSwitch;
    private DigitalChannel lowerFrontSwitch;
    private DigitalChannel upperFrontSwitch;

    // the change in distance we want to travel such that our alignment tool goes next to he crypto box
    private static final int COLUMN_SEAT_OFFSET = 140;
    private static final int COLUMN_STRAFE_SEAT_OFFSET = 300;

    private static final int TWEEN_TIME = 700;
    private static final int SERVO_DEPLOYMENT_TIME = 500;


    private long STRAFE_SINGLE_COLUMN_DISTANCE = 500;


    //2220 Perfectly centers, but we want to error to one side so we can use the button
    private static final long CENTER_MOVE_TIME = 2225;
    private static final long B_OFFSET = 325;
    private static final long FAR_OFFSET = B_OFFSET;
    private static final long NEAR_OFFSET = -B_OFFSET;
    private static final long CENTER_OFFSET = 0;

    private static final long A_CENTER_OFFSET = 1500;
    private static final long A_NEAR_OFFSET = A_CENTER_OFFSET - 410;
    private static final long A_FAR_OFFSET = A_CENTER_OFFSET + 410;

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

        m.setRotationTarget(0);
        // Kick the jewel off.
        doJewel();

//        m.turnToTarget();
        navigateToColumn(mark);



        depositBlock(mark);


        if (startPos == StartPosition.BLUE_B || startPos == StartPosition.RED_B) {
            collectBlocks();
            scoreNextColumn(mark);
        } else {
            lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
            rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);
        }
    }

    void initialize() {
        lFlip = hardwareMap.servo.get("lFlip");
        rFlip = hardwareMap.servo.get("rFlip");
        lFlip.setPosition(ServoValue.LEFT_FLIP_UP);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_UP);

        rAlignServo = hardwareMap.servo.get("rAlignServo");
        rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);
        topAlign = hardwareMap.servo.get("topAlign");
        topAlign.setPosition(ServoValue.TOP_ALIGN_IN);

        armServo = hardwareMap.servo.get("armServo");
        armServo.setPosition(ServoValue.RELIC_ARM_STORAGE);
        clawServo = hardwareMap.servo.get("clawServo");
        clawServo.setPosition(ServoValue.RELIC_CLAW_IN);

        lLift = hardwareMap.dcMotor.get("lBelting");

        lCollect = hardwareMap.dcMotor.get("lCollect");
        rCollect = hardwareMap.dcMotor.get("rCollect");

        // Initialize Vuforia
        telemetry.log().add("Initializing Vuforia...");
        vHelper = new VuforiaHelper(this);
        telemetry.log().add("Done Initializing Vuforia");

        // Get hardware devices
        rTentacle = hardwareMap.servo.get("rTentacle");
        lTentacle = hardwareMap.servo.get("lTentacle");

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
        upperBlockCS = hardwareMap.get(ColorSensor.class, "upperBlock");

        sideSwitch = hardwareMap.get(DigitalChannel.class, "upperSide");
        upperFrontSwitch = hardwareMap.get(DigitalChannel.class, "upperFront");
        lowerFrontSwitch = hardwareMap.get(DigitalChannel.class, "frontSwitch");

        m.initScoring(lLift, lCollect, rCollect, upperBlock);
    }

    void depositBlock(RelicRecoveryVuMark mark) {
        if(mark == RelicRecoveryVuMark.RIGHT) {
            topAlign.setPosition(ServoValue.TOP_ALIGN_OUT);
            rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_DOWN);
            sleep(SERVO_DEPLOYMENT_TIME);
            m.homeToCryptoColumn(lowerFrontSwitch, sideSwitch);
        } else {
            topAlign.setPosition(ServoValue.TOP_ALIGN_OUT);
            rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);
            sleep(SERVO_DEPLOYMENT_TIME);
            m.homeToCryptoColumn(upperFrontSwitch, sideSwitch);
        }

        lLift.setPower(-1);
        hardwareMap.servo.get("flipperRight").setPosition(ServoValue.FLIPPER_RIGHT_UP);
        hardwareMap.servo.get("flipperLeft").setPosition(ServoValue.FLIPPER_LEFT_UP);
        sleep(1000);
        hardwareMap.servo.get("flipperRight").setPosition(ServoValue.FLIPPER_RIGHT_DOWN);
        hardwareMap.servo.get("flipperLeft").setPosition(ServoValue.FLIPPER_LEFT_DOWN);
        rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);

        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(500, 0.2f, 0.2f);

        topAlign.setPosition(ServoValue.TOP_ALIGN_IN);
        rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);

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
        js = jsR;
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
            telemetry.addData("upperBlock", upperBlock.getDistance(DistanceUnit.CM));
            telemetry.addData("upperBlockCS", upperBlockCS.red() + ", " + upperBlockCS.green() + ", " + upperBlockCS.blue());
            telemetry.addData("side, topFront, lowerFront", sideSwitch.getState() + " " + upperFrontSwitch.getState() + " " + lowerFrontSwitch.getState());
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
        long unknownADefault = A_NEAR_OFFSET;

        telemetry.log().add("Executing on position B");
        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);

        switch(mark) {
            case LEFT:
                bop = new BlockPlacer( CryptoboxColumns.LEFT, BlockColors.GREY );
                break;
            case CENTER:
                bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
                break;
            case RIGHT:
                bop = new BlockPlacer( CryptoboxColumns.RIGHT, BlockColors.GREY );
                break;
            case UNKNOWN:
                bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
                break;
        }

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
            m.run(1800, 0, 1);
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
                    m.run(unknownADefault, 0, 1);
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
                    m.run(unknownADefault, 0, 1);
                    break;
            }
        }


    }

    private void collectBlocks() {
        lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);

        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);

        m.powerConstant = 0.9f;
        m.run(1500, 0, 1, true);

        moveVec.SetComponents(0, 1);
        m.setDirectionVector(moveVec);
        m.run(650, 0, 1, false);

        moveVec.SetComponents(0, -1);
        m.setDirectionVector(moveVec);
        m.run(950, 0, 1, true);

        while (upperBlock.getDistance(DistanceUnit.CM) > 15 || Double.isNaN(upperBlock.getDistance(DistanceUnit.CM))) {
            lLift.setPower(-1);
            lCollect.setPower(.8);
            rCollect.setPower(-.8);
        }
        lLift.setPower(0);
        lCollect.setPower(0);
        rCollect.setPower(0);
    }

    private int noahTheColumn(RelicRecoveryVuMark mark){
        switch (mark) {
            case LEFT:
                return CryptoboxColumns.LEFT;
            case CENTER:
                return CryptoboxColumns.MIDDLE;
            case RIGHT:
                return CryptoboxColumns.RIGHT;
        }
        return CryptoboxColumns.INVALID;
    }

    private void scoreNextColumn(RelicRecoveryVuMark startColumn) {
        // If the first block is grey.
        int firstBlockColor;
        int secondBlockColor;
        if((upperBlockCS.red() + upperBlockCS.blue() + upperBlockCS.green())/3 > 35) {
            firstBlockColor = BlockColors.GREY;
        } else {
            firstBlockColor = BlockColors.BROWN;
        }

        /// GET SECOND BLOCK COLOR HERE
        if(true) {
            secondBlockColor = BlockColors.BROWN;
        } else {
            secondBlockColor = BlockColors.GREY;
        }


        long toZero = 0;
        long zeroToColumn = 0;

        int[] columns = BlockPlacerTree.getBlockPlacement(noahTheColumn(startColumn), firstBlockColor, secondBlockColor);

        telemetry.log().add("Column: ", columns[0] + ":   Reference: lcr -- " + CryptoboxColumns.LEFT + " " + CryptoboxColumns.MIDDLE + " " + CryptoboxColumns.RIGHT);

        switch (startColumn) {
            case LEFT:
                toZero = (STRAFE_SINGLE_COLUMN_DISTANCE * 2);
                break;
            case CENTER:
                toZero = STRAFE_SINGLE_COLUMN_DISTANCE;
                break;
            case RIGHT:
                toZero = 0;
                break;
        }

        switch (columns[0]) {
            case CryptoboxColumns.LEFT:
                zeroToColumn = (STRAFE_SINGLE_COLUMN_DISTANCE * 2);
                break;
            case CryptoboxColumns.MIDDLE:
                zeroToColumn = STRAFE_SINGLE_COLUMN_DISTANCE;
                break;
            case CryptoboxColumns.RIGHT:
                zeroToColumn = 0;
                break;
        }


        long moveDistance = toZero - zeroToColumn;
        int directionModifier = 1;
        telemetry.log().add("moveDistance:" + moveDistance);
        if(moveDistance < 0) {
            moveDistance = -moveDistance;
            directionModifier = -1;
        }
        moveVec.SetComponents(directionModifier, 0);
        m.setDirectionVector(moveVec);

        sleep(5000);

        m.run((moveDistance*directionModifier)+COLUMN_STRAFE_SEAT_OFFSET, 0, 1);

        if(columns[0] == CryptoboxColumns.RIGHT) {
            topAlign.setPosition(ServoValue.TOP_ALIGN_OUT);
            rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_DOWN);

            m.homeToCryptoColumn(lowerFrontSwitch, sideSwitch);
        } else {
            topAlign.setPosition(ServoValue.TOP_ALIGN_OUT);
            rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);

            m.homeToCryptoColumn(upperFrontSwitch, sideSwitch);
        }

        rAlignServo.setPosition(ServoValue.RIGHT_ALIGN_UP);
        topAlign.setPosition(ServoValue.TOP_ALIGN_IN);

        if(columns[0] == columns[1]) {
            // Deposit both blocks
            lLift.setPower(-1);
            sleep(2000);
        } else {
            // Deposit a single block
            lLift.setPower(-1);
            sleep(500);
        }
    }

    private void balanceToColumn(long columnOffset) {
        m.run(CENTER_MOVE_TIME + columnOffset - COLUMN_SEAT_OFFSET, 0, 1);
        telemetry.log().add("Finished running, starting turn");
        m.setRotationTarget(90 * sideModifier);
        m.turnToTarget();

        lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);
    }

    private enum StartPosition {
        RED_A, RED_B, BLUE_A, BLUE_B
    }

}
