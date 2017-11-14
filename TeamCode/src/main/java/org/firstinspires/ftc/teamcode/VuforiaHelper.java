package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by matt on 11/1/17.
 */

public class VuforiaHelper {
    VuforiaLocalizer vuforia;
    int cameraMonitorViewId;
    VuforiaLocalizer.Parameters parameters;
    VuforiaTrackables trackables;
    VuforiaTrackable template;
    RelicRecoveryVuMark vuMark;

    VuforiaHelper(LinearOpMode context) {
        cameraMonitorViewId = context.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", context.hardwareMap.appContext.getPackageName());
        parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Acq/trz/////AAAAGXUOe/SqwE6QmoOrIXJe3S4va9eMDKZHt3tVhm6SIswWHx9Je8DgPAhpIK/mvU8vKs30ybIcUOPqYu738fQl/zQe0DKmzR0SxpqcpnH2VIiteHk8vjfmCuZQGX0PWcaJ/rgat+Fm999sc4UgPuyRo86DpJZ73ZVSZ7zpvyQgH5xkrj42cgFpcfPeMuQGgrqxf4+LT8FXV0NlLbXJzCIbniMEvyHiWd/YMbAO2x83oXa6bjZBUjZlCCUN+EhuKKQlCdfwxzN0aqQEHy8U1svpOPGd7SDp5FmIZsVdt089IGrcwDPYqkgg61sFL7PgaVQAffT6WeUrZq0xXg78FH5i1VYsVkETDu/I69lOdQXKjGlY";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        // Create a new VuforiaLocalizer from parameters.
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        setRelicTrackables();
        vuMark = RelicRecoveryVuMark.from(template);
    }

    public RelicRecoveryVuMark getVuMark() {
        return RelicRecoveryVuMark.from(template);
    }

    private void setRelicTrackables() {
        // Load the Relic Recover specific VuMarks
        trackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        trackables.activate();
        template = trackables.get(0);
        template.setName("relicVuMarkTemplate"); // Label for debugging, otherwise unnecessary.
    }
}
