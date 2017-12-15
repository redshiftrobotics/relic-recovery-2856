/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Official Tesseract Teleop
 */
@TeleOp(name = "xX_2856Teleop_Xx", group = "Tesseract")
public class TesseractTeleop extends OpMode {
    private MechanumChassis m;
    private DcMotor lBelting;
    private DcMotor rBelting;
    private DcMotor lCollect;
    private DcMotor rCollect;
    private Servo lFlip;
    private Servo rFlip;

    private Servo glyphFlipperRight;
    private Servo glyphFlipperLeft;
    private Servo armServo;
    private Servo clawServo;
    private Servo armExtensionServo;

    Servo lTentacle;
    Servo rTentacle;

    @Override
    public void init() {

        // Initialize non-drivetrain motors.
        lBelting = hardwareMap.dcMotor.get("lBelting");
        rBelting = hardwareMap.dcMotor.get("rBelting");
//
//        lExtender = hardwareMap.servo.get("lExtender");
//        rExtender = hardwareMap.servo.get("rExtender");


        glyphFlipperRight = hardwareMap.servo.get("flipperRight");
        glyphFlipperRight.setPosition(ServoValue.FLIPPER_RIGHT_DOWN);

        glyphFlipperLeft = hardwareMap.servo.get("flipperLeft");
        glyphFlipperLeft.setPosition(ServoValue.FLIPPER_LEFT_DOWN);


        armServo = hardwareMap.servo.get("armServo");
        armServo.setPosition(ServoValue.RELIC_ARM_STORAGE);
        clawServo = hardwareMap.servo.get("clawServo");
        clawServo.setPosition(ServoValue.RELIC_CLAW_IN);

        armExtensionServo = hardwareMap.servo.get("armExtension");
        armExtensionServo.setPosition(ServoValue.RELIC_ARM_EXTENSION_IN);

        lFlip = hardwareMap.servo.get("lFlip");
        rFlip = hardwareMap.servo.get("rFlip");
        lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
        rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);

        lCollect = hardwareMap.dcMotor.get("lCollect");
        rCollect = hardwareMap.dcMotor.get("rCollect");

        lTentacle = hardwareMap.servo.get("lTentacle");
        rTentacle = hardwareMap.servo.get("rTentacle");
        lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
        rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);


        // Initialize drive-train with appropriate motors and OpMode context.
        m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3")
        );

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        Vector2D v = new Vector2D(-gamepad1.right_stick_x, gamepad1.right_stick_y);
        m.setDirectionVector(v);
        m.addJoystickRotation(gamepad1.left_stick_x);
        m.setMotorPowers();
//        m.addTeleopIMUTarget(gamepad1.left_stick_x, telemetry);

        liftControl(gamepad2);
        relicControl(gamepad2);

        intakeControl(gamepad1);
        scoreControl(gamepad1);
    }

    /***
     * Controls the movement of the glyph lifter.
     * @param pad The joystick to put assign this control to.
     */
    private void liftControl(Gamepad pad) {

        // HACK TO GIVE TURNER FLIP
        if(pad.dpad_down) {
            lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
            rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);
        } else if (pad.dpad_up) {
            lFlip.setPosition(ServoValue.LEFT_FLIP_UP);
            rFlip.setPosition(ServoValue.RIGHT_FLIP_UP);
        }

        lBelting.setPower(-pad.left_trigger);
        rBelting.setPower(pad.right_trigger);

        if (pad.left_bumper || pad.right_bumper) {
            lBelting.setPower(1);
            rBelting.setPower(-1);
        }
    }

    private void intakeControl(Gamepad pad) {
        if(pad.dpad_down) {
            lFlip.setPosition(ServoValue.LEFT_FLIP_DOWN);
            rFlip.setPosition(ServoValue.RIGHT_FLIP_DOWN);
        } else if (pad.dpad_up) {
            lFlip.setPosition(ServoValue.LEFT_FLIP_UP);
            rFlip.setPosition(ServoValue.RIGHT_FLIP_UP);
        }

        rCollect.setPower(-pad.right_trigger);
        lCollect.setPower(pad.left_trigger);

        if (pad.right_bumper || pad.left_bumper) {
            rCollect.setPower(0.8);
            lCollect.setPower(-0.8); // out
        }
    }

    // removed "staged" state
    private void scoreControl(Gamepad pad) {
        if (pad.y) {
            glyphFlipperRight.setPosition(ServoValue.FLIPPER_RIGHT_DOWN);
            glyphFlipperLeft.setPosition(ServoValue.FLIPPER_LEFT_DOWN);
        }
        if(pad.a) {
            glyphFlipperRight.setPosition(ServoValue.FLIPPER_RIGHT_UP);
            glyphFlipperLeft.setPosition(ServoValue.FLIPPER_LEFT_UP);
        }
    }


    // Uses A (inc), B (dec)
    private void armServoControl(Gamepad pad) {
        if( pad.y || pad.a ) {
            armServo.setPosition( (pad.y) ? ServoValue.RELIC_ARM_IN: ServoValue.RELIC_ARM_OUT );
        }
    }

    // Uses X (inc), Y (dec)
    private void clawServoControl(Gamepad pad) {
        if(pad.x || pad.b ) {
            clawServo.setPosition((pad.x) ? ServoValue.RELIC_CLAW_OUT : ServoValue.RELIC_CLAW_IN );
        }
    }

    // Uses right joystick
    private static final double CONTINUOUS_SERVO_JOYSTICK_THRESH = 0.01; // Needs to be calibrated (maybe)
    private void armExtensionControl(Gamepad pad) {

        if (pad.right_stick_y >= 0.05) {
            armExtensionServo.setPosition(ServoValue.RELIC_ARM_EXTENSION_IN);
            clawServo.setPosition(.7);
        } else if (pad.right_stick_y <= -0.05) {
            lTentacle.setPosition(ServoValue.RIGHT_TENTACLE_FOR_RELIC);
            armExtensionServo.setPosition(ServoValue.RELIC_ARM_EXTENSION_OUT);
        }

    }

    private void relicControl(Gamepad pad) {
        armServoControl(pad);
        clawServoControl(pad);
        armExtensionControl(pad);
    }
}
