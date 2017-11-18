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

import com.qualcomm.hardware.bosch.BNO055IMU;
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
    private DcMotor lBelting;
    private DcMotor rBelting;
    private DcMotor lCollect;
    private DcMotor rCollect;
    @Override
    public void init() {

        // Initialize non-drivetrain motors.
        lBelting = hardwareMap.dcMotor.get("lBelting");
        rBelting = hardwareMap.dcMotor.get("rBelting");

        lCollect = hardwareMap.dcMotor.get("lCollect");
        rCollect = hardwareMap.dcMotor.get("rCollect");

//        hardwareMap.servo.get("lTentacle").setPosition(ServoValue.LEFT_TENTACLE_UP - .1);
//        hardwareMap.servo.get("rTentacle").setPosition(ServoValue.RIGHT_TENTACLE_UP + .1);

        // Initialize drive-train with appropriate motors and OpMode context.
        m = new MechanumChassis(
            hardwareMap.dcMotor.get("m0"),
            hardwareMap.dcMotor.get("m1"),
            hardwareMap.dcMotor.get("m2"),
            hardwareMap.dcMotor.get("m3"),
            hardwareMap.get(BNO055IMU.class, "imu"),
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
//        m.addTeleopIMUTarget(gamepad1.left_stick_x, telemetry);
        liftControl(gamepad2);
        intakeControl(gamepad1);
    }

    /***
     * Controls the movement of the glyph lifter.
     * @param pad The joystick to put assign this control to.
     */
    private void liftControl(Gamepad pad) {

        lBelting.setPower(-pad.left_trigger);
        rBelting.setPower(pad.right_trigger);

        if (pad.left_bumper) {
            lBelting.setPower(1);
        } else if (pad.right_bumper) {
            rBelting.setPower(-1);
        }
    }

    private void intakeControl(Gamepad pad) {
        rCollect.setPower(-pad.right_trigger);
        lCollect.setPower(pad.left_trigger);

        if (pad.right_bumper || pad.left_bumper) {
            rCollect.setPower(0.8);
            lCollect.setPower(-0.8); // out
        }
    }
}
