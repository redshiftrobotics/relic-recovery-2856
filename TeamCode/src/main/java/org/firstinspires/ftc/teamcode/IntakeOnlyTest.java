package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by matt on 2/13/18.
 */
@Autonomous (name = "[T2] Intake Only")
public class IntakeOnlyTest extends LinearOpMode {
    MechanumChassis m;
    @Override
    public void runOpMode() throws InterruptedException {
        m = new MechanumChassis(hardwareMap, this);
        m.initializeWithIMU();
        waitForStart();
        m.setRotationTarget(0);
        m.setDirectionVectorComponents(1, 0);
        m.lowerIntake();
        while(opModeIsActive()) {
            m.safeIntakeAsync();
        }
    }
}
