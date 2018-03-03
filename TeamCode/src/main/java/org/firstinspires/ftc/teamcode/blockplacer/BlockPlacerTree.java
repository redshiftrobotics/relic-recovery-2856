package org.firstinspires.ftc.teamcode.blockplacer;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import java.util.Arrays;

/**
 * Created by NoahR on 2/3/18.
 * All triple block auto placements
 */

public class BlockPlacerTree {

    public static int[] getBlockPlacement( int startingColumn, int blockTwoColor, int blockThreeColor )
    {
        // When thinking about tripple block auto we need not worry about the fourth block.
        // For consistency sake we use the cases in which the fourth block is grey, however this
        // does not change our outcome.

        int[] returnValue;

        if( blockTwoColor == BlockColors.GREY )
            returnValue =  (blockThreeColor == BlockColors.GREY) ? CASE_gggg[ startingColumn ] : CASE_ggbg[ startingColumn ];
        else
            returnValue = (blockThreeColor == BlockColors.GREY) ? CASE_gbgg[ startingColumn ] : CASE_gbbg[ startingColumn ];

        return Arrays.copyOf( returnValue, 2 ); // Truncate fourth block from this list.
    }

    public static int[] getBlockPlacement( int startingColumn, int blockTwoColor, int blockThreeColor, int blockFourColor )
    {

        int[] returnValue;

        // ggxx
        if( blockTwoColor == BlockColors.GREY )
        {
            // gggx
            if( blockThreeColor == BlockColors.GREY )
                return (blockFourColor == BlockColors.GREY ) ? CASE_gggg[ startingColumn ] : CASE_gggb[ startingColumn ];
            //ggbx
            else
                return (blockFourColor == BlockColors.GREY ) ? CASE_ggbg[ startingColumn ] : CASE_ggbb[ startingColumn ];
        }
        // gbxx
        else
        {
            // gbgx
            if( blockThreeColor == BlockColors.GREY )
                return (blockFourColor == BlockColors.GREY ) ? CASE_gbgg[ startingColumn ] : CASE_gbgb[ startingColumn ];
            // gbbx
            else
                return (blockFourColor == BlockColors.GREY) ? CASE_gbbg[ startingColumn ] : CASE_gbbb[ startingColumn ];
        }
    }

    public static RelicRecoveryVuMark[] getBlockPlacement( RelicRecoveryVuMark startingColumn, int blockTwoColor, int blockThreeColor )
    {
        return CryptoboxColumns.toRelicRecoveryVuMarkDeep( getBlockPlacement( CryptoboxColumns.fromRelicRecoveryVuMark( startingColumn ), blockTwoColor, blockThreeColor ) );
    }

    public static RelicRecoveryVuMark[] getBlockPlacement( RelicRecoveryVuMark startingColumn, int blockTwoColor, int blockThreeColor, int blockFourColor )
    {
        return CryptoboxColumns.toRelicRecoveryVuMarkDeep( getBlockPlacement( CryptoboxColumns.fromRelicRecoveryVuMark( startingColumn ), blockTwoColor, blockThreeColor, blockFourColor ) );
    }

    private static final int[][] CASE_gggg = {
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE, CryptoboxColumns.INVALID   }, // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.LEFT, CryptoboxColumns.INVALID     }, // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.INVALID, CryptoboxColumns.INVALID }  // Start RIGHT
    };

    private static final int[][] CASE_gggb = {
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE    }, // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.LEFT, CryptoboxColumns.LEFT        }, // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.INVALID, CryptoboxColumns.INVALID }  // Start RIGHT
    };


    private static final int[][] CASE_ggbg = {
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT },  // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE, CryptoboxColumns.LEFT  },  // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.LEFT, CryptoboxColumns.LEFT   }  // Start RIGHT
    };

    private static final int[][] CASE_ggbb = {
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT },  // Start LEFT
            { CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE  },  // Start MIDDLE
            { CryptoboxColumns.RIGHT, CryptoboxColumns.LEFT, CryptoboxColumns.LEFT   }  // Start RIGHT
    };

    private static final int[][] CASE_gbgg = {
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE },  // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT  },  // Start MIDDLE
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE   }  // Start RIGHT
    };

    private static final int[][] CASE_gbgb = {
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE },  // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT  },  // Start MIDDLE
            { CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE, CryptoboxColumns.MIDDLE   }  // Start RIGHT
    };

    private static final int[][] CASE_gbbg = {
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.MIDDLE },  // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT  },  // Start MIDDLE
            { CryptoboxColumns.LEFT, CryptoboxColumns.LEFT, CryptoboxColumns.LEFT   }  // Start RIGHT
    };

    private static final int[][] CASE_gbbb = {
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.INVALID },  // Start LEFT
            { CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT, CryptoboxColumns.MIDDLE  },  // Start MIDDLE
            { CryptoboxColumns.LEFT, CryptoboxColumns.LEFT, CryptoboxColumns.MIDDLE   }  // Start RIGHT
    };

    // Disallow instantiation
    private BlockPlacerTree(){}
}
