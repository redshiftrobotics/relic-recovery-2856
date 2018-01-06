package org.firstinspires.ftc.teamcode.blockplacer;

import static org.firstinspires.ftc.teamcode.blockplacer.BlockColors.BROWN;
import static org.firstinspires.ftc.teamcode.blockplacer.BlockColors.GREY;

/**
 * Created by noah on 1/4/18.
 * The different cryptobox patterns
 */

class CryptoPatterns {
    static final int[][] FROG = new int[][] {
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN }
    };

    static final int[][] BIRD = new int[][] {
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY }
    };

    static final int[][] SNAKE = new int[][] {
            { BROWN, GREY, GREY },
            { BROWN, BROWN, GREY },
            { GREY, BROWN, BROWN },
            { GREY, GREY, BROWN }
    };

    private CryptoPatterns(){}
}
