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

    private static long NEAR_OFFSET = 1500;
    private static  long CENTER_OFFSET = 1650;
    private static long FAR_OFFSET = 1900;

    private static long A_CENTER_OFFSET = 1300;
    private static long A_NEAR_OFFSET = 400;
    private static long A_FAR_OFFSET = 1750;

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

        // Process the VuMark
        mark = vHelper.getVuMark();
        telemetry.log().add("DETECTED COLUMN: " + mark);

        // Zero rotation target.
        m.setRotationTarget(0);

        // Kick the jewel off.
        doJewel();

        navigateToColumn();
        sleep(5000);
        depositBlock();

        m.lowerIntake();

        if (startPos == StartPosition.BLUE_B || startPos == StartPosition.RED_B) {
            collectBlocks();
            if(m.justPark) {
                m.stowAlignment();
                m.setDirectionVectorComponents(0, 1);
                m.run(600, 0.4f, 0.4f);
            } else {
                scoreNextColumn(mark, unnoahTheColumn(BlockPlacerTree.getBlockPlacement(noahTheColumn(mark), BlockColors.GREY, BlockColors.GREY)[0]));
            }
        }

        m.stowAlignment();
        sleep(m.SERVO_DEPLOYMENT_TIME+500);

        // TODO: Give Mark (Cark?) compliments of the chef
    }


    void depositBlock() {
        m.deployAlignment(noahTheColumn(mark));
        m.homeToCryptoColumn();
        m.lift.setPower(1);
        sleep(1000);
        m.stowAlignment();
        m.setDirectionVectorComponents(0, -1);
        m.run(800, 0.1f, 0.1f);
    }

    private int getSideCoefficient(StartPosition pos) {
        return (pos == StartPosition.BLUE_A || pos == StartPosition.BLUE_B) ? -1 : 1;
    }

    private void configurationLoop() {
        while(!isStarted() && !isStopRequested()) {
            // Side and position configuration.
            if(gamepad1.a) {
                startPos = StartPosition.BLUE_A;
                m.js = m.jsL;
            } else if (gamepad1.b) {
                startPos = StartPosition.BLUE_B;
                m.js = m.jsL;
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
            telemetry.addData("Scoring Sensors (upper, lower):",
                m.upperBlock.getDistance(DistanceUnit.CM)
                + " | " + m.upperBlockCS.blue()
                + " | " + m.lowerBlock.getDistance(DistanceUnit.CM)
                + " | " + m.lowerBlockCS.blue()
            );
            telemetry.addData("Switches (side, topFront, lowerFront)",
                m.sideSwitch.getState()
                + " | " + m.upperFrontSwitch.getState()
                + " | " + m.lowerFrontSwitch.getState()
            );
            telemetry.update();
        }
    }

    private void doJewel() {
        m.deployTentacles();

        // Detect color and kick correct jewel. No need to side modify these!!!
        if(m.js.red() > m.js.blue()) {
            telemetry.log().add("JEWEL SENSOR SAW:::: RED");
            m.encoderTurn(-1, 700);
            m.stowTentacles();
            m.encoderTurn(1, 700);
        } else {
            telemetry.log().add("JEWEL SENSOR SAW:::: BLUE");
            m.encoderTurn(1, 700);
            m.stowTentacles();
            m.encoderTurn(-1, 700);
        }

        // Just to be safe, can probably be removed
        m.setRotationTarget(0);
    }

    private void navigateToColumn() {
        long unknownDefault = CENTER_OFFSET;
        long unknownADefault = A_CENTER_OFFSET;

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
            m.run(1650, 0, 1);
            m.setDirectionVectorComponents(-1*sideModifier, 0);
        }

        if(startPos == StartPosition.BLUE_A) {
            A_NEAR_OFFSET += 400;
            A_CENTER_OFFSET += 400;
            A_FAR_OFFSET += 400;
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
        m.run(1200, 0, 1, true);

        m.setDirectionVectorComponents(0, -1);
        m.run(1100, 0, 1, true);
        m.setDirectionVectorComponents(0, 1);
        m.run(650, 0, 1, true);

        m.setDirectionVectorComponents(0, -1);
        m.run(950, 0, 1, true);
        m.setDirectionVectorComponents(0, 1);
        m.run(650, 0, 1, true);

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

    private RelicRecoveryVuMark unnoahTheColumn(int column){
        switch (column) {
            case CryptoboxColumns.LEFT:
                return RelicRecoveryVuMark.LEFT;
            case CryptoboxColumns.MIDDLE:
                return RelicRecoveryVuMark.CENTER;
            case CryptoboxColumns.RIGHT:
                return RelicRecoveryVuMark.RIGHT;
        }
        return RelicRecoveryVuMark.UNKNOWN;
    }

    private void scoreNextColumn(RelicRecoveryVuMark startColumn, RelicRecoveryVuMark targetColumn) {
        int start = numberizeColumn(startColumn);
        int target = numberizeColumn(targetColumn);
        shiftColumns(target - start);
        depositBlock();
        m.setDirectionVectorComponents(0, -1);
        m.run(1400, 0, 0.6f);

        m.stowAlignment();
    }

    private void shiftColumns(int numberOfColumns) {
        // Use the sign of numberOfColumns to determine direction to move
        m.setDirectionVectorComponents(numberOfColumns/Math.abs(numberOfColumns), 0);

        // Because move direction has already been set, the signage of the motion becomes irrelevant and would make for longer conditionals
        int absoluteMotion = Math.abs(numberOfColumns);
        if (absoluteMotion == 1) {
            m.run(600, 1f, 1f);
        } else if (absoluteMotion == 2) {
            m.run(1400, 0f, 1f);
        } // Else, either we do not need to move or no VuMark was seen... Score in the same column with no added motion.
    }

    private int numberizeColumn(RelicRecoveryVuMark column) {
        switch(column) {
            case LEFT: return 0;
            case CENTER: return 1;
            case RIGHT: return 2;
        }
        // Will only be reached if VuMark is UNKNOWN
        return -9;
    }

    private void balanceToColumn(long columnOffset) {
        m.run(columnOffset, 0, 1);
        telemetry.log().add("Finished running, starting turn");
        sleep(500);
        m.setRotationTarget(90 * sideModifier);
        m.turnToTarget();
        m.lowerIntake();
    }

    private enum StartPosition {
        RED_A, RED_B, BLUE_A, BLUE_B
    }

}
