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

/**
 * Official Tesseract Teleop
 */
@TeleOp(name = "xX_2856Teleop_Xx", group = "Tesseract")
public class TesseractTeleop extends OpMode {
    private MechanumChassis m;
    private DcMotor lift;
    private DcMotor lCollect;
    private DcMotor rCollect;
    @Override
    public void init() {

        // Initialize non-drivetrain motors.
        lift = hardwareMap.dcMotor.get("lift");

        lCollect = hardwareMap.dcMotor.get("lCollect");
        rCollect = hardwareMap.dcMotor.get("rCollect");

        hardwareMap.servo.get("lTentacle").setPosition(ServoValue.LEFT_TENTACLE_UP + .1);
        hardwareMap.servo.get("rTentacle").setPosition(ServoValue.RIGHT_TENTACLE_UP - .1);

        // Initialize drive-train with appropriate motors and OpMode context.
        m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3"),
            this
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
        liftControl(gamepad1);
        intakeControl(gamepad1);
    }

    /***
     * Controls the movement of the glyph lifter.
     * @param pad The joystick to put assign this control to.
     */
    private void liftControl(Gamepad pad) {
        if(pad.right_bumper) {
            lift.setPower(1);
        } else if (pad.left_bumper) {
            lift.setPower(-1);
        } else {
            lift.setPower(0);
        }
    }

    private void intakeControl(Gamepad pad) {
        if(pad.right_trigger > 0.1) {
            lCollect.setPower(1);
            rCollect.setPower(-1);
        } else if (pad.left_trigger > 0.1) {
            lCollect.setPower(-1);
            rCollect.setPower(1);
        } else {
            lCollect.setPower(0);
            rCollect.setPower(0);
        }
    }
}
