package org.firstinspires.ftc.teamcode;

import java.util.Random;

/**
 * Created by matt on 2/7/18.
 */

public class Comments {
    static Random r = new Random();
    private static String[] compliments = new String[]{
            "Wow, nice moves Mark!",
            "Lookin' good with those classy black side-panels, Mark!"
    };

    public static String getRandomCompliment() {
        return compliments[r.nextInt(compliments.length)];
    }
}
