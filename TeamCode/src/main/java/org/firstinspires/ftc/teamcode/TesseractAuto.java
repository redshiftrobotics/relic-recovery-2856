package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.blockplacer.BlockColors;
import org.firstinspires.ftc.teamcode.blockplacer.BlockPlacerTree;
import org.firstinspires.ftc.teamcode.blockplacer.CryptoboxColumns;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "LegitSauceAutoSauce")
public class TesseractAuto extends LinearOpMode {
    private MechanumChassis m;
    RelicRecoveryVuMark mark;

    // the change in distance we want to travel such that our alignment tool goes next to he crypto box
    private static final int COLUMN_SEAT_OFFSET = 140;
    private static final int COLUMN_STRAFE_SEAT_OFFSET = 300;

    private long STRAFE_SINGLE_COLUMN_DISTANCE = 1200;

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
        telemetry.addData("Hardware: ", "Initializing...");
        telemetry.addData("Vuforia: ", "Initializing...");
        telemetry.update();

        // Hardware.
        m = new MechanumChassis(hardwareMap, this);
        m.initializeWithIMU();
        telemetry.addData("Hardware: ", "Initialized.");
        telemetry.update();

        // Vuforia.
        vHelper = new VuforiaHelper(this);
        telemetry.addData("Vuforia: ", "Initialized.");
        telemetry.update();

        // Configuration.
        configurationLoop();

        waitForStart();

        // Process the VuMark
        mark = vHelper.getVuMark();
        telemetry.log().add("DETECTED COLUMN: " + mark);

        // Zero rotation target.
        m.setRotationTarget(0);

        // Kick the jewel off.
        doJewel();

        navigateToColumn();
        depositBlock();

        m.lowerIntake();

        if (startPos == StartPosition.BLUE_B || startPos == StartPosition.RED_B) {
            collectBlocks();
            scoreNextColumn();
        }

        // TODO: Give Mark (Cark?) compliments of the chef
    }


    void depositBlock() {
        m.deployAlignment(noahTheColumn(mark));
        m.homeToCryptoColumn();

        m.lift.setPower(1);
        m.deployFlipper();
        sleep(1000);
        m.stowFlipper();

        m.stowAlignment();

        m.setDirectionVectorComponents(0, -1);
        m.run(800, 0.2f, 0.2f);
    }

    private int getSideCoefficient(StartPosition pos) {
        return (pos == StartPosition.BLUE_A || pos == StartPosition.BLUE_B) ? -1 : 1;
    }

    private void configurationLoop() {
        m.js = m.jsR;
        while(!isStarted()) {
            // Side and position configuration.
            if(gamepad1.a) {
                startPos = StartPosition.BLUE_A;
                m.js = m.jsL;
            } else if (gamepad1.b) {
                startPos = StartPosition.BLUE_B;
                m.js = m.jsL;
                sideModifier = -1;
            } else if (gamepad1.x) {
                startPos = StartPosition.RED_A;
                m.js = m.jsR;
            } else if (gamepad1.y) {
                startPos = StartPosition.RED_B;
                m.js = m.jsR;
            }

            sideModifier = getSideCoefficient(startPos);

            // Validate all sensors are operational.
            telemetry.addData("Starting Position: ", startPos);
            telemetry.addData("Jewel Color Sensor: ", m.js.blue());
            telemetry.addData("upperBlock: ", m.upperBlock.getDistance(DistanceUnit.CM));
            telemetry.addData("upperBlockCS: ", m.upperBlockCS.blue());
            telemetry.addData("Switches (side, topFront, lowerFront)", m.sideSwitch.getState() + " " + m.upperFrontSwitch.getState() + " " + m.lowerFrontSwitch.getState());
            telemetry.update();
        }
    }

    private void doJewel() {
        m.deployTentacles();

        // Detect color and kick correct jewel. No need to side modify these!!!
        if(m.js.red() > m.js.blue()) {
            telemetry.log().add("JEWEL SENSOR SAW:::: RED");
            m.jewelKick(-1);
        } else {
            telemetry.log().add("JEWEL SENSOR SAW:::: BLUE");
            m.jewelKick(1);
        }

        // Just to be safe, can probably be removed
        m.setRotationTarget(0);
    }

    private void navigateToColumn() {
        long unknownDefault = CENTER_OFFSET;
        long unknownADefault = A_NEAR_OFFSET;

        telemetry.log().add("Executing on position B");
        m.setDirectionVectorComponents(0, 1);

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
            m.setDirectionVectorComponents(-1*sideModifier, 0);
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
        } else if (startPos == StartPosition.RED_A) {
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
        m.setDirectionVectorComponents(0, -1);
        m.run(1500, 0, 1, true);

        m.setDirectionVectorComponents(0, 1);
        m.run(650, 0, 1, false);

        m.setDirectionVectorComponents(0, -1);
        m.run(950, 0, 1, true);

        m.intakeUntilStaged();
    }

    private int noahTheColumn(RelicRecoveryVuMark inputColumn){
        switch (inputColumn) {
            case LEFT:
                return CryptoboxColumns.LEFT;
            case CENTER:
                return CryptoboxColumns.MIDDLE;
            case RIGHT:
                return CryptoboxColumns.RIGHT;
        }
        return CryptoboxColumns.INVALID;
    }

    private void scoreNextColumn() {
        // If the first block is grey.
        int firstBlockColor;
        int secondBlockColor;
        if((m.upperBlockCS.red() + m.upperBlockCS.blue() + m.upperBlockCS.green())/3 > 35) {
            firstBlockColor = BlockColors.GREY;
            telemetry.log().add("First block: GREY");
        } else {
            firstBlockColor = BlockColors.BROWN;
            telemetry.log().add("First block: BROWN");
        }
        telemetry.update();

        /// TODO: GET SECOND BLOCK COLOR HERE
        if(true) {
            secondBlockColor = BlockColors.GREY;
        } else {
            secondBlockColor = BlockColors.BROWN;
        }

        long toZero = 0;
        long zeroToColumn = 0;

        int[] columns = BlockPlacerTree.getBlockPlacement(noahTheColumn(mark), firstBlockColor, secondBlockColor);

        telemetry.log().add("Column: ", columns[0] + ":   Reference: lcr -- " + CryptoboxColumns.LEFT + " " + CryptoboxColumns.MIDDLE + " " + CryptoboxColumns.RIGHT);

        switch (mark) {
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

        sleep(5000);
        m.setDirectionVectorComponents(directionModifier, 0);
        m.run((moveDistance*directionModifier)+COLUMN_STRAFE_SEAT_OFFSET, 0, 1);

        m.deployAlignment(columns[0]);
        m.homeToCryptoColumn();
        m.stowAlignment();

        if(columns[0] == columns[1]) {
            // Deposit both blocks
            m.lift.setPower(1);
            sleep(2000);
        } else {
            // Deposit a single block
            m.lift.setPower(1);
            sleep(500);
        }
        m.lift.setPower(0);
    }

    private void balanceToColumn(long columnOffset) {
        m.run(CENTER_MOVE_TIME + columnOffset - COLUMN_SEAT_OFFSET, 0, 1);
        telemetry.log().add("Finished running, starting turn");
        m.setRotationTarget(90 * sideModifier);
        m.turnToTarget();
        m.lowerIntake();
    }

    private enum StartPosition {
        RED_A, RED_B, BLUE_A, BLUE_B
    }

}
