package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.firstinspires.ftc.teamcode.VuforiaHelper;

/**
 * Created by matt on 3/18/18.
 */

public class JewelProcessor {
    private VuforiaHelper v;
    private Bitmap currentImage;
    private int pixelColors;

    JewelProcessor(VuforiaHelper v) {
        this.v = v;
    }

    boolean isLeftJewelRed() throws InterruptedException{
        currentImage = v.getCameraImage();
        float[] average = new float[3];
        if (currentImage != null) {
            int boxHeight = 100;
            int boxWidth = 200;
            for(int x = 1; x < boxWidth; x++) {
                for(int y = 1; y < boxHeight; y++) {
                    pixelColors = currentImage.getPixel(currentImage.getWidth() - 1, currentImage.getHeight() - 1);
                    average[0] += Color.red(pixelColors);
                    average[1] += Color.green(pixelColors);
                    average[2] += Color.blue(pixelColors);
                }
            }
            average[0] /= boxHeight*boxWidth;
            average[1] /= boxHeight*boxWidth;
            average[2] /= boxHeight*boxWidth;
        }

        return average[0] > average[2];
    }
}
