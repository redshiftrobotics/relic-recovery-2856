package org.firstinspires.ftc.teamcode.blockplacer;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by noah on 1/4/18.
 * Enumerable to represent cryptobox columns
 */

public class CryptoboxColumns {

    static RelicRecoveryVuMark toRelicRecoveryVuMark( int cryptoboxColumn )
    {
        switch( cryptoboxColumn )
        {
            case LEFT:
                return RelicRecoveryVuMark.LEFT;
            case MIDDLE:
                return RelicRecoveryVuMark.CENTER;
            case RIGHT:
                return RelicRecoveryVuMark.RIGHT;
            default:
                return RelicRecoveryVuMark.UNKNOWN;
        }
    }

    static int fromRelicRecoveryVuMark( RelicRecoveryVuMark vuMark )
    {
        switch ( vuMark )
        {
            case LEFT:
                return CryptoboxColumns.LEFT;
            case CENTER:
                return CryptoboxColumns.MIDDLE;
            case RIGHT:
                return CryptoboxColumns.RIGHT;
            default:
                return CryptoboxColumns.INVALID;
        }
    }

    static RelicRecoveryVuMark[] toRelicRecoveryVuMarkDeep( int cryptoboxColumn[] )
    {
        RelicRecoveryVuMark vuMarks[] = {null, null, null};

        for( int iColumnIndex = 0; iColumnIndex < cryptoboxColumn.length; iColumnIndex++ )
        {
            vuMarks[iColumnIndex] = CryptoboxColumns.toRelicRecoveryVuMark( cryptoboxColumn[iColumnIndex] );
        }

        return vuMarks;
    }

    public static final int INVALID = -1;
    public static final int LEFT = 0;
    public static final int MIDDLE = 1;
    public static final int RIGHT = 2;

    private CryptoboxColumns(){}
}
