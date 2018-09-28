package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.blockplacer.BlockColors;
import org.firstinspires.ftc.teamcode.blockplacer.BlockPlacerTree;
import org.firstinspires.ftc.teamcode.blockplacer.CryptoboxColumns;

/**
 * Created by matt on 10/10/17.
 */
@Autonomous(name = "Tesseract Auto (Relic Recovery)")
public class TesseractAuto extends LinearOpMode {
    private MechanumChassis m;
    RelicRecoveryVuMark mark;

    private static long NEAR_OFFSET = 1750;
    private static  long CENTER_OFFSET = 2050;
    private static long FAR_OFFSET = 2350;

    private static long A_CENTER_OFFSET = 1325;
    private static long A_FAR_OFFSET = 1600;

    private VuforiaHelper vHelper;
    private JewelProcessor jp;
    boolean lastLeftJewelIsRed;

    private StartPosition startPos = StartPosition.RED_B;
    private int sideModifier = 1;

    @Override
    public void runOpMode() { // removed throws interrupt exception
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

        // Setup processor for jewel
        jp = new JewelProcessor(vHelper);

        // Configuration.
        configurationLoop();

        // Process the VuMark
        mark = vHelper.getVuMark();
        telemetry.log().add("DETECTED COLUMN: " + mark);

        // Zero rotation target.
        m.setRotationTarget(0);

        // Compliment Mark
        telemetry.log().add(Comments.getRandomCompliment());
        telemetry.update();

        // Kick the jewel off.
        doJewel();

        navigateToColumn();

        m.lowerIntake();
        depositBlock(noahTheColumn(mark), 700);


        if (startPos == StartPosition.BLUE_B || startPos == StartPosition.RED_B) {

            m.setDirectionVectorComponents(0, -1);
            m.run(650, 0f, 0.7f);
            m.lift.setPower(0);
            m.rCollect.setPower(0);
            m.lCollect.setPower(0);


            m.run(1000, 0f, 1f);
            collectBlocks();
            if(m.justPark) {
                m.stowAlignment();
                m.setDirectionVectorComponents(0, 1);
                m.run(600, 0.4f, 0.4f);
            } else {
                scoreNextColumn(mark, unnoahTheColumn(BlockPlacerTree.getBlockPlacement(noahTheColumn(mark), m.upperBlockColor, m.lowerBlockColor)[0]));
            }
        } else if (startPos == StartPosition.BLUE_A || startPos == StartPosition.RED_A) {
            m.lift.setPower(0);
            m.rCollect.setPower(0);
            m.lCollect.setPower(0);
            aPositionCollection();
            if(m.justPark) {
                m.stowAlignment();
            } else {
                //scoreNextColumn(mark, unnoahTheColumn(BlockPlacerTree.getBlockPlacement(noahTheColumn(mark), m.upperBlockColor, m.lowerBlockColor)[0]));
                specialDepositBlock();
            }
        }
    }

    void specialDepositBlock() {
//        if(startPos == StartPosition.RED_A) {
//            m.deployAlignment(0);
//        } else {
//            m.deployAlignment(2);
//        }
//        sleep(1000);
//        m.runToFront();

        m.runToSide();

        m.lift.setPower(1);
        m.rCollect.setPower(1);
        m.lCollect.setPower(1);
        sleep(2000);
        m.stowAlignment();
        m.setDirectionVectorComponents(0, -1);
        m.run(900, 0.1f, 0.1f);
        m.lift.setPower(0);
        m.rCollect.setPower(0);
        m.lCollect.setPower(0);
    }


    // Non-directional, ensure setting direction beforehand
    void cryptoToGlyphPitStrafe() {
        long farToPit = 1350;
        long middleToPit = 1600;
        long nearToPit = 1900;
        if (startPos == StartPosition.BLUE_A) {
            switch (mark) {
                case LEFT:
                    m.run(nearToPit, 0, 1);
                    break;
                case CENTER:
                    m.run(middleToPit, 0, 1);
                    break;
                case RIGHT:
                    m.run(farToPit, 0, 1);
                    break;
                case UNKNOWN:
                    m.run(middleToPit, 0, 1);
                    break;
            }
        } else if (startPos == StartPosition.RED_A) {
            switch (mark) {
                case LEFT:
                    m.run(farToPit, 0, 1);
                    break;
                case CENTER:
                    m.run(middleToPit, 0, 1);
                    break;
                case RIGHT:
                    m.run(nearToPit, 0, 1);
                    break;
                case UNKNOWN:
                    m.run(middleToPit, 0, 1);
                    break;
            }
        }
    }

    void aPositionCollection() {
        m.setDirectionVectorComponents(0, -1);
        m.run(450, 0.4f, 1);

        m.setDirectionVectorComponents(-1*sideModifier, 0);

        cryptoToGlyphPitStrafe();

        m.setDirectionVectorComponents(0, -1);
        m.run(1400, 0f, 1f);
        collectBlocks();

        if (startPos == StartPosition.BLUE_A) {
            m.deployAlignment(2);
            m.runToFront();
            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_IN);
        } else {
            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_OUT);
            m.runToFront();
            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_IN);
        }

        m.setDirectionVectorComponents(0, -1);
        m.run(300, 0f, 0.3f);

        m.setDirectionVectorComponents(sideModifier, 0);
        if(startPos == StartPosition.BLUE_A) {
            m.run(1100, 0, 1);
        } else {
            m.run(1400, 0, 1);
        }
    }

    void depositBlock(int col, long depositTime) {
        m.deployAlignment(col);
        sleep(400);

        // Handle special case
        if(!(startPos == StartPosition.RED_A && mark == RelicRecoveryVuMark.RIGHT)) {
            // Bump for physical align sensor
            m.setDirectionVectorComponents(1, 0);
            m.run(450, 0.4f, 1f);
        }
        m.homeToCryptoColumn(6000);
        m.lift.setPower(1);
        m.rCollect.setPower(1);
        m.lCollect.setPower(1);
        sleep(depositTime);
        m.stowAlignment();
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
                m.jsD = m.jsLD;
            } else if (gamepad1.b) {
                startPos = StartPosition.BLUE_B;
                m.js = m.jsL;
                m.jsD = m.jsLD;
            } else if (gamepad1.x) {
                startPos = StartPosition.RED_A;
                m.js = m.jsR;
                m.jsD = m.jsRD;
            } else if (gamepad1.y) {
                startPos = StartPosition.RED_B;
                m.js = m.jsR;
                m.jsD = m.jsRD;
            }

            lastLeftJewelIsRed = jp.isLeftJewelRed();

            sideModifier = getSideCoefficient(startPos);

            // Validate all sensors are operational.
            telemetry.addData("Starting Position: ", startPos);
            telemetry.addData("Jewel Color: ", lastLeftJewelIsRed ? "RED" : "BLUE");
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
        if (lastLeftJewelIsRed) {
            m.encoderTurn(-1*sideModifier, 700);
            m.stowTentacles();
            m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP + .1);
            m.encoderTurn(1*sideModifier, 700);
        } else {
            m.encoderTurn(1*sideModifier, 700);
            m.stowTentacles();
            m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP + .1);
            m.encoderTurn(-1*sideModifier, 700);
        }

        // Just to be safe, can probably be removed
        m.setRotationTarget(0);
    }

    private void navigateToColumn() {
        long unknownDefault = CENTER_OFFSET;
        long unknownADefault = A_CENTER_OFFSET;

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
            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_OUT);
            m.runToFront();
            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_IN);
            m.setDirectionVectorComponents(0, -1);
            m.run(600, 0.4f, 0.4f);
            m.setDirectionVectorComponents(-1*sideModifier, 0);
        }

        if (startPos == StartPosition.BLUE_A) {
            switch (mark) {
                case LEFT:
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
                    break;
                case UNKNOWN:
                    m.run(unknownADefault, 0, 1);
                    break;
            }
        }
    }

    private void collectBlocks() {
        // last working well 15000, 0, 0.1f, 5f, 1
        if(startPos == StartPosition.BLUE_A || startPos == StartPosition.RED_A) {
            m.runAlongCurve(8000, 0, 0.1f, 5f, 1, true, sideModifier, 0);
            m.setRotationTarget(0);
        } else {
            m.runAlongCurve(8000, 0, 0.1f, 5f, 1, true, sideModifier, 90);
            m.setRotationTarget(90*sideModifier);
        }
//        m.turnToTarget();
        m.setDirectionVectorComponents(0, 1);
        m.run(1000, 0f, 1f);
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
        return CryptoboxColumns.MIDDLE;
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
        return RelicRecoveryVuMark.CENTER;
    }

    private void scoreNextColumn(RelicRecoveryVuMark startColumn, RelicRecoveryVuMark targetColumn) {
        int start = numberizeColumn(startColumn);
        int target = numberizeColumn(targetColumn);
        shiftColumns(target - start);
        int[] placement = BlockPlacerTree.getBlockPlacement(noahTheColumn(mark), m.upperBlockColor, m.lowerBlockColor);
//        if (placement[0] != placement[1]) {
//            depositBlock(noahTheColumn(targetColumn), 600);
//        } else {
        depositBlock(noahTheColumn(targetColumn), 2000);
//        }
        m.setDirectionVectorComponents(0, -1);
        m.run(600, 0, 0.6f);

        m.stowAlignment();
    }

    private void shiftColumns(int numberOfColumns) {
        // Use the sign of numberOfColumns to determine direction to move
        if (Math.abs(numberOfColumns) > 0) {
            m.setDirectionVectorComponents(numberOfColumns / Math.abs(numberOfColumns), 0);
        } else {
            m.setDirectionVectorComponents(1, 0);
        }

        // Because move direction has already been set, the signage of the motion becomes irrelevant and would make for longer conditionals
        int absoluteMotion = Math.abs(numberOfColumns);
        if (absoluteMotion == 1) {
            m.run(600, 1f, 1f);
        } else if (absoluteMotion == 2) {
            m.run(1450, 0f, 1f); // 1400, 1370 for red 2
        } // Else, either we do not need to move or no VuMark was seen... Score in the same column with no added motion.
    }

    private int numberizeColumn(RelicRecoveryVuMark column) {
        switch(column) {
            case LEFT: return 0;
            case CENTER: return 1;
            case RIGHT: return 2;
        }
        // Will only be reached if VuMark is UNKNOWN
        return 1;
    }

    private void balanceToColumn(long columnOffset) {
        m.run(columnOffset, 0, 0.7f);
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
