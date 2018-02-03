package org.firstinspires.ftc.teamcode.blockplacer;

/**
 * Created by NoahR on 2/3/18.
 * All triple block auto placements
 */

class BlockPlacerTree {

    static int[] getBlockPlacement( int startingColumn, int blockTwoColor, int blockThreeColor )
    {
        if( blockTwoColor == BlockColors.GREY )
            return (blockThreeColor == BlockColors.GREY) ? CASE_ggg[ startingColumn ] : CASE_ggb[ startingColumn ];
        else
            return (blockThreeColor == BlockColors.GREY) ? CASE_gbg[ startingColumn ] : CASE_gbb[ startingColumn ];
    }

    private static final int[][] CASE_ggg = {
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE },  // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE },  // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.INVALID}  // Start RIGHT
    };

    private static final int[][] CASE_ggb = {
            { CryptoboxColumns.LEFT, CryptoboxColumns.RIGHT },   // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE },  // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.LEFT}     // Start RIGHT
    };

    private static final int[][] CASE_gbg = {
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE },   // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT },     // Start MIDDLE
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE}     // Start RIGHT
    };

    private static final int[][] CASE_gbb = {
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT },     // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT },     // Start MIDDLE
            { CryptoboxColumns.LEFT, CryptoboxColumns.LEFT}         // Start RIGHT
    };

    // Disallow instantiation
    private BlockPlacerTree(){}
}
