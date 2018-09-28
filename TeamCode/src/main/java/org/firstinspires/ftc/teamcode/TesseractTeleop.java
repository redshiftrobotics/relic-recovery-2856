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
@TeleOp(name = "Tesseract TeleOp (Relic Recovery)", group = "Tesseract")
public class TesseractTeleop extends OpMode {
    private MechanumChassis m;
    private Debouncer flipBounce;
    private Debouncer slowBounce;
    private Debouncer deScoreBounce;
    boolean slowDown = false;
    boolean deScoreMode = false;
    @Override
    public void init() {
        // Initialize drive-train with appropriate motors and OpMode context.
        m = new MechanumChassis(hardwareMap);
        m.initialize();
        m.lowerIntake();

        flipBounce = new Debouncer();
        slowBounce = new Debouncer();
        deScoreBounce = new Debouncer();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("filterJoystickOut", filterJoystick(gamepad1.right_stick_x));
        telemetry.update();
        m.setDirectionVectorComponents(-filterJoystick(gamepad1.right_stick_x), filterJoystick(gamepad1.right_stick_y));
        driveTrainControl(gamepad1);
        intakeControl(gamepad1);
        scoreControl(gamepad1);
        liftControl(gamepad2);
        relicControl(gamepad2);

        telemetry.addData("relic pos",  m.relic.getCurrentPosition());
        telemetry.update();
    }

    private void driveTrainControl(Gamepad pad) {
        m.addJoystickRotation(filterJoystick(pad.left_stick_x));
        m.setMotorPowers();
        if(slowBounce.debounce(pad.right_stick_button)) {
            slowDown = true;
        } else {
            slowDown = false;
        }

//        if(pad.dpad_left) {
//            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_OUT);
//        } else {
//            m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_IN);
//        }
    }

    /***
     * Controls the movement of the glyph lifter.
     * @param pad The joystick to put assign this control to.
     */
    private void liftControl(Gamepad pad) {

        // HACK TO GIVE TURNER FLIP
        if(pad.dpad_down) {
            m.lCollectServo.setPosition(ServoValue.LEFT_COLLECT_DOWN);
            m.rCollectServo.setPosition(ServoValue.RIGHT_COLLECT_DOWN);
        } else if (pad.dpad_up) {
            m.lCollectServo.setPosition(ServoValue.LEFT_COLLECT_UP);
            m.rCollectServo.setPosition(ServoValue.RIGHT_COLLECT_UP);
        }

        m.lift.setPower(pad.left_trigger);

        if (pad.left_bumper) {
            m.lift.setPower(-1);
        }
    }

    private double filterJoystick(float val) {
        float expoK = 0.4f;
        double output = ((1 - expoK) * Math.pow(val, 3) + expoK * val);
        if (slowDown) {
            return output*0.7;
        } else {
            return output;
        }
    }

    private void singleDriverControls(Gamepad pad) {
        if(deScoreMode) {
            if(m.relic.getCurrentPosition() >= 4420) {
                if(pad.right_trigger > 0.1) {
                    m.relic.setPower(-pad.right_trigger);
                } else {
                    m.relic.setPower(0);
                }
            } else if(m.relic.getCurrentPosition() <= 0) {
                if(pad.left_trigger > 0.1) {
                    m.relic.setPower(pad.left_trigger);
                } else {
                    m.relic.setPower(0);
                }
            } else {
                if(pad.left_trigger > 0.1) {
                    m.relic.setPower(pad.left_trigger);
                } else if (pad.right_trigger > 0.1) {
                    m.relic.setPower(-pad.right_trigger);
                } else {
                    m.relic.setPower(0);
                }
            }

            if (pad.right_trigger >= 0.1 || pad.left_trigger >= 0.1) {
                m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_FOR_RELIC);
            } else {
                m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
            }

            if(pad.right_trigger > 0.1) {
                m.armServo.setPosition(ServoValue.RELIC_ARM_IN);
            } else if (pad.left_trigger > 0.1) {
//                m.armServo.setPosition(ServoValue.RELIC_ARM_OUT);
            }


            if(pad.left_bumper || pad.right_bumper) {
                m.clawServo.setPosition((pad.left_bumper) ? ServoValue.RELIC_CLAW_RELEASE : ServoValue.RELIC_CLAW_GRAB);
            }
        }
    }

    private void intakeControl(Gamepad pad) {
        deScoreMode = deScoreBounce.debounce(pad.a);

        if(deScoreMode) {
            if(pad.left_bumper || pad.left_trigger > 0.1) {
                m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_OUT);
            } else {
                m.lowerAlign.setPosition(ServoValue.LOWER_ALIGN_IN);
            }
        } else {
            if (pad.dpad_down) {
                m.lCollectServo.setPosition(ServoValue.LEFT_COLLECT_DOWN);
                m.rCollectServo.setPosition(ServoValue.RIGHT_COLLECT_DOWN);
            } else if (pad.dpad_up) {
                m.lCollectServo.setPosition(ServoValue.LEFT_COLLECT_UP);
                m.rCollectServo.setPosition(ServoValue.RIGHT_COLLECT_UP);
            }

            if(gamepad2.right_trigger > 0.3) {
                m.rCollect.setPower(gamepad2.right_trigger);
                m.lCollect.setPower(gamepad2.right_trigger);
            } else {
                m.rCollect.setPower(pad.right_trigger);
                m.lCollect.setPower(pad.left_trigger);
            }

            if (pad.left_bumper) {
                m.rCollect.setPower(-0.8);
                m.lCollect.setPower(-0.8); // out
            }
        }
    }

    // removed "staged" state
    private void scoreControl(Gamepad pad) {
        if (flipBounce.debounce(pad.right_bumper)) {
            m.flipperRight.setPosition(ServoValue.FLIPPER_RIGHT_UP);
            m.flipperLeft.setPosition(ServoValue.FLIPPER_LEFT_UP);
        } else {
            m.flipperRight.setPosition(ServoValue.FLIPPER_RIGHT_DOWN);
            m.flipperLeft.setPosition(ServoValue.FLIPPER_LEFT_DOWN);
        }
    }


    // Uses A (inc), B (dec)
    private void armServoControl(Gamepad pad) {
        if( pad.left_stick_button || pad.a ) {
            m.armServo.setPosition( (pad.left_stick_button) ? ServoValue.RELIC_ARM_IN: ServoValue.RELIC_ARM_OUT );
        }
    }

    // Uses X (inc), Y (dec)
    private void clawServoControl(Gamepad pad) {
        if(pad.x || pad.b ) {
            m.clawServo.setPosition((pad.x) ? ServoValue.RELIC_CLAW_RELEASE : ServoValue.RELIC_CLAW_GRAB);
        }
    }

    // Uses right joystick
    private static final double CONTINUOUS_SERVO_JOYSTICK_THRESH = .05; // Needs to be calibrated (maybe)
    private void armExtensionControl(Gamepad pad) {

        if(m.relic.getCurrentPosition() >= 4420) {
            if(-pad.right_stick_y < 0) {
                m.relic.setPower(-pad.right_stick_y);
            } else {
                m.relic.setPower(0);
            }
        } else if(m.relic.getCurrentPosition() <= 0) {
            if(-pad.right_stick_y > 0) {
                m.relic.setPower(-pad.right_stick_y);
            } else {
                m.relic.setPower(0);
            }
        } else {
            m.relic.setPower(-pad.right_stick_y);
        }

        if (Math.abs(pad.right_stick_y) >= 0.05) {
            m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_FOR_RELIC);
        } else {
            m.lTentacle.setPosition(ServoValue.LEFT_TENTACLE_UP);
        }

    }

    private void relicControl(Gamepad pad) {
        armServoControl(pad);
        clawServoControl(pad);
        armExtensionControl(pad);
        if(pad.dpad_left) {
            m.topAlign.setPosition(ServoValue.TOP_ALIGN_OUT);
            m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_DOWN);
        } else {
            m.topAlign.setPosition(ServoValue.TOP_ALIGN_IN);
            m.rTentacle.setPosition(ServoValue.RIGHT_TENTACLE_UP);
        }
    }
}
