package org.firstinspires.ftc.teamcode.blockplacer;

import static org.firstinspires.ftc.teamcode.blockplacer.BlockColors.BROWN;
import static org.firstinspires.ftc.teamcode.blockplacer.BlockColors.GREY;

/**
 * Created by noah on 1/4/18.
 * The different cryptobox patterns
 */

class CryptoPatterns {

    // Note these patterns are upsidedown visually because our computers are australian
    static final int[][] FROG = new int[][] {
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY }
    };

    static final int[][] BIRD = new int[][] {
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
    };

    static final int[][] SNAKE = new int[][] {
            { GREY, GREY, BROWN },
            { GREY, BROWN, BROWN },
            { BROWN, BROWN, GREY },
            { BROWN, GREY, GREY }
    };

    private CryptoPatterns(){}
}
