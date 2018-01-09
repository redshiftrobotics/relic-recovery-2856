package org.firstinspires.ftc.teamcode.blockplacer;

import junit.framework.TestCase;

/**
 * Created by NoahR on 1/6/18.
 */
public class BlockPlacerTest extends TestCase {
    public void testGetNextBlockColumn() throws Exception {
        BlockPlacer bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
        assertEquals( CryptoboxColumns.RIGHT,   bop.getNextBlockColumn( BlockColors.BROWN ));
        assertEquals( CryptoboxColumns.LEFT, bop.getNextBlockColumn( BlockColors.BROWN ));
        assertEquals( CryptoboxColumns.RIGHT,  bop.getNextBlockColumn( BlockColors.GREY  ));
        assertEquals( CryptoboxColumns.MIDDLE,  bop.getNextBlockColumn( BlockColors.BROWN  ));
    }
}