package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

/**
 * Created by matt on 9/16/17.
 */

public class MechanumChassis {

    // Output members, corresponds to motors.
    /*
         Front
        ________
        |0    1|
        |      |
        |3    2|
        --------
         Rear
    */
    private double speed0;
    private double speed1;
    private double speed2;
    private double speed3;
    private DcMotor m0;
    private DcMotor m1;
    private DcMotor m2;
    private DcMotor m3;
    private BNO055IMU imu;

    private float tweenTime = 1;
    private float rotationTarget = 0;

    private LinearOpMode context;

    MechanumChassis(DcMotor m0, DcMotor m1, DcMotor m2, DcMotor m3, BNO055IMU imu, LinearOpMode context) {
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.imu = imu;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        imu.initialize(parameters);

        initMotors();
        this.context = context;
    }

    MechanumChassis(DcMotor m0, DcMotor m1, DcMotor m2, DcMotor m3, OpMode context) {
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        initMotors();
//        this.context = context;
    }

    private void initMotors() {
        this.m1.setDirection(DcMotorSimple.Direction.REVERSE);
        this.m2.setDirection(DcMotorSimple.Direction.REVERSE);
        m0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void stopMotors() {
        m0.setPower(0);
        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
    }

    //based on second post here ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
    void setDirectionVector(Vector2D vector) {
        float powerConstant = 0.9f;
        double magnitude = Math.hypot(vector.GetXComponent(), vector.GetYComponent());
        double robotAngle = Math.atan2(vector.GetYComponent(), vector.GetXComponent()) - (Math.PI / 4);
        speed0 = magnitude * Math.cos(robotAngle) * powerConstant;
        speed1 = magnitude * Math.sin(robotAngle) * powerConstant;
        speed2 = magnitude * Math.cos(robotAngle) * powerConstant;
        speed3 = magnitude * Math.sin(robotAngle) * powerConstant;
    }

    void setTweenTime(float millis) {
        this.tweenTime = millis;
    }

    void addJoystickRotation(double rotation){
        speed0 += rotation;
        speed1 -= rotation;
        speed2 -= rotation;
        speed3 += rotation;
    }

    private float getRotation() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
    }

    public void setRotationTarget(float degrees) {
        rotationTarget = degrees;
    }

    public void turnToTarget() {
        while(context.opModeIsActive() && Math.abs(rotationTarget - getRotation()) > 0.4) {
            m0.setPower((getRotation() - rotationTarget) / 30);
            m1.setPower(-(getRotation() - rotationTarget) / 30);
            m2.setPower(-(getRotation() - rotationTarget) / 30);
            m3.setPower((getRotation() - rotationTarget) / 30);

            context.telemetry.addData("rotation: ", getRotation());
            context.telemetry.update();
        }
    }

    void run(long millis, float startSpeed, float endSpeed) {
        long start = System.currentTimeMillis();
        float P;
        float elapsedTime;
        double power;
        while (start + millis > System.currentTimeMillis() && context.opModeIsActive()) {
            elapsedTime = System.currentTimeMillis() - start;
            if (elapsedTime <= tweenTime) {
                power = ((startSpeed - endSpeed)/2) * Math.cos((Math.PI*elapsedTime) / tweenTime) + (startSpeed + endSpeed) / 2;
            } else if (elapsedTime < millis - tweenTime) {
                power = endSpeed;
            } else { // elapsedTime > millis - tweenTime
                power = ((endSpeed - startSpeed)/2) * Math.cos((Math.PI*(millis-tweenTime-elapsedTime)) / tweenTime) + (endSpeed + startSpeed) / 2;
            }

            P = (getRotation() - rotationTarget) / 30;
            m0.setPower(speed0 * power + P);
            m1.setPower(speed1 * power - P);
            m2.setPower(speed2 * power - P);
            m3.setPower(speed3 * power + P);
            context.idle();
        }
        stopMotors();
    }

    void runContinuos() {
        m0.setPower(speed0);
        m1.setPower(speed1);
        m2.setPower(speed2);
        m3.setPower(speed3);
    }
}
