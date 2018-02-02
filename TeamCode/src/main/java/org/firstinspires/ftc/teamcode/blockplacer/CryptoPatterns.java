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

    static final int[][] FROG_INV = new int[][] {
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN }
    };

    static final int[][] BIRD = new int[][] {
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
    };

    static final int[][] BIRD_INV = new int[][] {
            { BROWN, GREY, BROWN },
            { GREY, BROWN, GREY },
            { GREY, BROWN, GREY },
            { BROWN, GREY, BROWN },
    };

    static final int[][] SNEK = new int[][] {
            { GREY, GREY, BROWN },
            { GREY, BROWN, BROWN },
            { BROWN, BROWN, GREY },
            { BROWN, GREY, GREY }
    };

    static final int[][] SNAKE_INV = new int[][] {
            { BROWN, BROWN, GREY },
            { BROWN, GREY, GREY },
            { GREY, GREY, BROWN },
            { GREY, BROWN, BROWN }
    };

    private CryptoPatterns(){}
}
