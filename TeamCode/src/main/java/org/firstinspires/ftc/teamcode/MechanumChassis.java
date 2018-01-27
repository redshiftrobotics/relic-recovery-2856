package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

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


    // Scoring mechanism controls
    private DcMotor lLift;
    private DcMotor lCollect;
    private DcMotor rCollect;
    private DistanceSensor upperBlock;

    private float imuInitOffset = -9000;

    private float teleopHeading = -9000;

    private float tweenTime = 1;
    private float rotationTarget = 0;

    public boolean debugModeEnabled = false;

    private LinearOpMode context;

    public float powerConstant = 0.9f; // multiplier for drive train change, addition for added time to overcome static friction with new drive-train

    MechanumChassis(DcMotor m0, DcMotor m1, DcMotor m2, DcMotor m3, BNO055IMU i, LinearOpMode context) {
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        this.imu = i;
        this.imu.initialize(parameters);
        initMotors();
        this.context = context;
    }

    MechanumChassis(DcMotor m0, DcMotor m1, DcMotor m2, DcMotor m3, BNO055IMU i) {
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        this.imu = i;
        this.imu.initialize(parameters);

        initMotors();
    }

    private void initMotors() {
        m0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m1.setDirection(DcMotor.Direction.REVERSE);
        m2.setDirection(DcMotor.Direction.REVERSE);
    }

    private void stopMotors() {
        m0.setPower(0);
        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
    }

    //based on second post here ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
    void setDirectionVector(Vector2D vector) {
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

    void addTeleopIMUTarget(double joyInput, Telemetry tm) {
        teleopHeading += 4 * joyInput;
        tm.addData("(rotation, teleopHeading, P value)", getRotation() + ", " + (getRotation() - teleopHeading) + ", " + teleopHeading);
        tm.update();
        setMotorPowers(1, getOffset(getRotation(), teleopHeading) / 40);
    }

    void lockRotation() {
        if(teleopHeading == -9000 /* has not been set already */) {
            teleopHeading = getRotation();
        }
        setMotorPowers(1, getOffset(getRotation(), teleopHeading) / 27);
    }

    private float getRotation() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180;
    }

    public void setRotationTarget(float degrees) {
        if(imuInitOffset == -9000 /* has not been set already */) {
            imuInitOffset = getRotation();
        }

        rotationTarget = imuInitOffset - degrees;
    }

    public void turnToTarget() {
        long start = System.currentTimeMillis();
        long turnTimeout = 3000;
        float P;
        while(context.opModeIsActive() && start + turnTimeout > System.currentTimeMillis()) {

            P = getOffset(getRotation(), rotationTarget);

            if(Math.abs(P) < 0.07) {
                break;
            }

            if (debugModeEnabled) {
                context.telemetry.addData("turnToTarget", "target: "+ rotationTarget);
                context.telemetry.addData("turnToTarget", "rotation: "+ getRotation());
                context.telemetry.addData("turnToTarget", "Rotational Error: " + P);
                context.telemetry.update();
            }

            m0.setPower(P / 20);
            m1.setPower(-P / 20);
            m2.setPower(-P / 20);
            m3.setPower(P / 20);
        }
        stopMotors();
    }

    private float getOffset(float currentAngle, float target) {
        if (currentAngle + 360 - target <= 180) {
            return (currentAngle -  target + 360);
        } else if (target + 360 - currentAngle <= 180) {
            return (target - currentAngle + 360) * -1;
        } else if (currentAngle -  target <= 180) {
            return (currentAngle -  target);
        }
        return 0;
    }

    /***
     * This method is separated from a normal turn because we don't care about precision but more importantly,
     * the oscillations during a P turn will cause the robot to rock off the balance board at a skewed angle, thus loosing traction.
     * A single imprecise turn fixes this issue.
     */
    public void jewelKick(int direction) {
        long start = System.currentTimeMillis();
        long jewelKickTurnTime = 700;
        while (start + jewelKickTurnTime > System.currentTimeMillis() && context.opModeIsActive()) {
            if (debugModeEnabled) {
                context.telemetry.addData("jewelKick", "Kick running");
                context.telemetry.update();
            }
            m0.setPower(-0.25*direction);
            m1.setPower(0.25*direction);
            m2.setPower(0.25*direction);
            m3.setPower(-0.25*direction);
        }
        stopMotors();
    }

    public void jewelBack(int direction) {
        long start = System.currentTimeMillis();
        long jewelKickTurnTime = 700;
        while (start + jewelKickTurnTime > System.currentTimeMillis() && context.opModeIsActive()) {
            if (debugModeEnabled) {
                context.telemetry.addData("jewelKick", "Kick running");
                context.telemetry.update();
            }
            m0.setPower(0.25*direction);
            m1.setPower(-0.25*direction);
            m2.setPower(-0.25*direction);
            m3.setPower(0.25*direction);
        }
        if (debugModeEnabled) {
            context.telemetry.addData("jewelKick", "Kick ended");
            context.telemetry.update();
        }
        stopMotors();
    }

    void initScoring(DcMotor l, DcMotor lC, DcMotor rC, DistanceSensor uB) {
        lLift = l;
        lCollect = lC;
        rCollect = rC;
        upperBlock = uB;
    }

    void safeRunBelting() {
        if(upperBlock != null) {
            context.telemetry.addData("upperBlock is non-null", "");
            if (upperBlock.getDistance(DistanceUnit.CM) > 15 || Double.isNaN(upperBlock.getDistance(DistanceUnit.CM))) {
                lLift.setPower(-1);
                lCollect.setPower(.8);
                rCollect.setPower(-.8);
            } else {
                lLift.setPower(0);
                lCollect.setPower(0);
                rCollect.setPower(0);
            }
        }
    }

    void run(long m, float s, float e) {
        run(m, s, e, false);
    }

    void run(long millis, float startSpeed, float endSpeed, boolean runBelting) {
        long start = System.currentTimeMillis();
        float P;
        float elapsedTime;
        while (start + millis > System.currentTimeMillis() && context.opModeIsActive()) {
            if(runBelting) {
                safeRunBelting();
            }
            elapsedTime = System.currentTimeMillis() - start;
            P = getOffset(getRotation(), rotationTarget) / 30;
            setMotorPowers(calculateTweenCurve(millis, elapsedTime, startSpeed, endSpeed), P);
        }
        stopMotors();
    }

    void homeToCryptoColumn(DigitalChannel frontSwitch, DigitalChannel sideSwitch) {
        float P;
        Vector2D movementDirection = new Vector2D(0, 1);

        // DigitalChannel.getState() evaluates to false when switch is pressed
        while (frontSwitch.getState() && context.opModeIsActive()) {
            P = getOffset(getRotation(), rotationTarget) / 30;
            setMotorPowers(0.9f, P);
        }

        while (!(!frontSwitch.getState() && !sideSwitch.getState()) && context.opModeIsActive()) {
            context.telemetry.addData("frontSwitch", frontSwitch.getState());
            context.telemetry.addData("sideSwitch", sideSwitch.getState());
            context.telemetry.update();
            if(!frontSwitch.getState()) {
                movementDirection.SetYComponent(0);
            } else {
                movementDirection.SetYComponent(Math.sqrt(2)/2);
            }
            if(!sideSwitch.getState()) {
                movementDirection.SetXComponent(0);
            } else {
                movementDirection.SetXComponent(-Math.sqrt(2)/2);
            }

            setDirectionVector(movementDirection);
            P = getOffset(getRotation(), rotationTarget) / 30;
            setMotorPowers(0.6f, P);
        }
        stopMotors();
    }

    // DEPRECATED, use homeToCryptoColumn
    void runToBox(DistanceSensor ods, boolean runBelting) {
        powerConstant = 0.3f;
        float P;
        while (Math.abs(getDistanceError(ods)) > 0.17 && context.opModeIsActive()) {
            if(runBelting) {
                safeRunBelting();
            }
            P = getOffset(getRotation(), rotationTarget) / 20;
            context.telemetry.addData("error", getDistanceError(ods));
            context.telemetry.update();
            setMotorPowers(Range.clip(getDistanceError(ods) / 60, -0.4, 0.4), P);
        }
        stopMotors();
        powerConstant = 0.9f;
    }

    private double getDistanceError(DistanceSensor ods) {
        float targetDistance = 20;
        if(Double.isNaN(ods.getDistance(DistanceUnit.CM))) {
            return 100;
        }
        return ods.getDistance(DistanceUnit.CM) - targetDistance;
    }

    void addJoystickRotation(double rotation){
        speed0 += rotation;
        speed1 -= rotation;
        speed2 -= rotation;
        speed3 += rotation;
    }

    private double calculateTweenCurve(long millis, float elapsedTime, double startSpeed, double endSpeed) {
        if (debugModeEnabled) {
            context.telemetry.addData("run elapsedTime", elapsedTime);
            context.telemetry.addData("run targetTime", millis);
            context.telemetry.update();
        }
        if (elapsedTime <= tweenTime) {
            return ((startSpeed - endSpeed)/2) * Math.cos((Math.PI*elapsedTime) / tweenTime) + (startSpeed + endSpeed) / 2;
        } else if (elapsedTime < millis - tweenTime) {
            return endSpeed;
        } else { // elapsedTime > millis - tweenTime
            return ((endSpeed - startSpeed)/2) * Math.cos((Math.PI*(millis-tweenTime-elapsedTime)) / tweenTime) + (endSpeed + startSpeed) / 2;
        }
    }

    void setMotorPowers() {
        setMotorPowers(1, 0);
    }

    private void setMotorPowers(double power, float P) {
        m0.setPower(speed0 * power + P);
        m1.setPower(speed1 * power - P);
        m2.setPower(speed2 * power - P);
        m3.setPower(speed3 * power + P);
    }
}
