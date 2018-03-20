package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.io.FileOutputStream;
import java.io.IOException;

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
    VuforiaLocalizer.CloseableFrame frame;

    VuforiaHelper(LinearOpMode context) {
        cameraMonitorViewId = context.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", context.hardwareMap.appContext.getPackageName());
        parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Acq/trz/////AAAAGXUOe/SqwE6QmoOrIXJe3S4va9eMDKZHt3tVhm6SIswWHx9Je8DgPAhpIK/mvU8vKs30ybIcUOPqYu738fQl/zQe0DKmzR0SxpqcpnH2VIiteHk8vjfmCuZQGX0PWcaJ/rgat+Fm999sc4UgPuyRo86DpJZ73ZVSZ7zpvyQgH5xkrj42cgFpcfPeMuQGgrqxf4+LT8FXV0NlLbXJzCIbniMEvyHiWd/YMbAO2x83oXa6bjZBUjZlCCUN+EhuKKQlCdfwxzN0aqQEHy8U1svpOPGd7SDp5FmIZsVdt089IGrcwDPYqkgg61sFL7PgaVQAffT6WeUrZq0xXg78FH5i1VYsVkETDu/I69lOdQXKjGlY";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        // Create a new VuforiaLocalizer from parameters.
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

        vuforia.setFrameQueueCapacity(1);

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

    public Bitmap getCameraImage() {
        // Poll will check asynchronously, but there should almost always be one there. take() would block until received.
        frame = vuforia.getFrameQueue().poll();
        if(frame != null) {
            long numImages = frame.getNumImages();
            for (int i = 0; i < numImages; i++) {
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    Image rawBuffer = frame.getImage(i);
                    Bitmap bm = Bitmap.createBitmap(rawBuffer.getWidth(), rawBuffer.getHeight(), Bitmap.Config.RGB_565);
                    bm.copyPixelsFromBuffer(rawBuffer.getPixels());
//                saveImage(bm);
                    frame.close();
                    return bm;
                }
            }
        }
        return null;
    }

    private void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/Pictures/debug/" + System.currentTimeMillis() + ".png");
            drawDebugSquare(bmp);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawDebugSquare(Bitmap bmp) {
        int lineWeight = 20;
        for (int i = 1; i < JewelProcessor.boxWidth; i++) {
            for (int j = 1; j < lineWeight; j++) {
                bmp.setPixel(bmp.getWidth() - i - JewelProcessor.boxRightOffset, bmp.getHeight() - JewelProcessor.boxHeight - j - JewelProcessor.boxBottomOffset, Color.rgb(0, 255, 0));
            }
        }
        for (int i = 1; i < JewelProcessor.boxHeight; i++) {
            for (int j = 1; j < lineWeight; j++) {
                bmp.setPixel(bmp.getWidth() - JewelProcessor.boxWidth - j - JewelProcessor.boxRightOffset,  bmp.getHeight() - i  - JewelProcessor.boxBottomOffset, Color.rgb(0, 255, 0));
            }
        }
    }
}