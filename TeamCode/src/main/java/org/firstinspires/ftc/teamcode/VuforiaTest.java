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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * OpMode for testing new Vuforia features
 */
@Disabled
@Autonomous(name="VuforiaTest")
public class VuforiaTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    VuforiaLocalizer vuforia;
    Orientation rotation;
    VectorF position;
    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // Set VuforiaLocalizer parameters.
        parameters.vuforiaLicenseKey = "Acq/trz/////AAAAGXUOe/SqwE6QmoOrIXJe3S4va9eMDKZHt3tVhm6SIswWHx9Je8DgPAhpIK/mvU8vKs30ybIcUOPqYu738fQl/zQe0DKmzR0SxpqcpnH2VIiteHk8vjfmCuZQGX0PWcaJ/rgat+Fm999sc4UgPuyRo86DpJZ73ZVSZ7zpvyQgH5xkrj42cgFpcfPeMuQGgrqxf4+LT8FXV0NlLbXJzCIbniMEvyHiWd/YMbAO2x83oXa6bjZBUjZlCCUN+EhuKKQlCdfwxzN0aqQEHy8U1svpOPGd7SDp5FmIZsVdt089IGrcwDPYqkgg61sFL7PgaVQAffT6WeUrZq0xXg78FH5i1VYsVkETDu/I69lOdQXKjGlY";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        // Create a new VuforiaLocalizer from parameters.
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        // Load the Relic Recover specific VuMarks
        VuforiaTrackables trackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable template = trackables.get(0);
        template.setName("relicVuMarkTemplate"); // Label for debugging, otherwise unnecessary.
        telemetry.addData("Initialized:", "VuMarks, Camera, Timer");
        telemetry.update();

        // OPMODE START

        waitForStart();
        // Reset runtime
        runtime.reset();

        // Start tracking the trackables
        trackables.activate();

        while(opModeIsActive()) {
            // Instantiate new VuMark
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(template);

            // When we see a vumark do the following...
            if(vuMark != RelicRecoveryVuMark.UNKNOWN) {
                // Create a new position matrix from the template.
                OpenGLMatrix matrixPosition = ((VuforiaTrackableDefaultListener)template.getListener()).getPose();
                rotation = Orientation.getOrientation(matrixPosition, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                position = matrixPosition.getTranslation();

                /**
                 * position.get(angle) where angle = 0, 1, or 2 will return the X, Y, and Z translation relative to the target
                 */

                /**
                 * rotation.firstAngle
                 * rotation.secondAngle
                 * rotation.thirdAngle
                 * will return rotation about the X, Y, and Z axis relative to the target
                 */

                /**
                 * RelicRecoveryVuMark.LEFT
                 * RelicRecoveryVuMark.RIGHT
                 * RelicRecoveryVuMark.CENTER
                 * will evaluate truthy if the cryptokey is at that location
                 *
                 * RelicRecoveryVuMark.UNKNOWN
                 * will evaluate truthy if the robot could not evaluate the cryptokey
                 */
            }
        }

    }
}
