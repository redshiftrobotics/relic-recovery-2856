package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by matt on 10/4/17.
 */
@Autonomous (name = "MovementMethodTests")
public class MovementTests extends LinearOpMode {
    MechanumChassis m;

    @Override
    public void runOpMode() throws InterruptedException {
        m = new MechanumChassis(
                hardwareMap.dcMotor.get("m0"),
                hardwareMap.dcMotor.get("m1"),
                hardwareMap.dcMotor.get("m2"),
                hardwareMap.dcMotor.get("m3"),
                this
        );
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        Vector2D testVec = new Vector2D(1, 0);
        m.setDirectionVector(testVec);
        m.run(4000);
        testVec = new Vector2D(-1, 0);
        sleep(500);
        m.setDirectionVector(testVec);
        m.run(4000);
    }

}
