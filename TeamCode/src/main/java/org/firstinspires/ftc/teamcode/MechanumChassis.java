package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

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

    MechanumChassis(DcMotor m0, DcMotor m1, DcMotor m2, DcMotor m3) {
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
    }

    //based on second post here ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
    void setDirectionVector(Vector2D vector) {
        double radius = Math.hypot(vector.GetXComponent(), vector.GetYComponent());
        double robotAngle = Math.atan2(vector.GetYComponent(), vector.GetXComponent()) - Math.PI / 4;
        speed0 = radius * Math.cos(robotAngle);
        speed1 = radius * Math.sin(robotAngle);
        speed2 = radius * Math.sin(robotAngle);
        speed3 = radius * Math.cos(robotAngle);
    }

    void ENGAGE() {
        m0.setPower(speed0);
        m1.setPower(speed1);
        m2.setPower(speed2);
        m3.setPower(speed3);
    }

    void DISENGAGE() {
        m0.setPower(0);
        m1.setPower(0);
        m2.setPower(0);
        m3.setPower(0);
    }
}
