package org.firstinspires.ftc.teamcode.blockplacer;

import junit.framework.TestCase;

/**
 * Created by NoahR on 1/6/18.
 */
public class BlockPlacerTest extends TestCase {
    public void testGetNextBlockColumn() throws Exception {

        // Test basics
        BlockPlacer bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
        assertEquals( CryptoboxColumns.RIGHT,   bop.getNextBlockColumn( BlockColors.BROWN ));
        assertEquals( CryptoboxColumns.LEFT, bop.getNextBlockColumn( BlockColors.BROWN ));
        assertEquals( CryptoboxColumns.RIGHT,  bop.getNextBlockColumn( BlockColors.GREY  ));
        assertEquals( CryptoboxColumns.MIDDLE,  bop.getNextBlockColumn( BlockColors.BROWN  ));

        // Test switching patterns (Frog -> Snake)
        bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
        assertEquals( CryptoboxColumns.LEFT, bop.getNextBlockColumn(BlockColors.GREY) );
        assertEquals( CryptoPatterns.SNEK, bop.getPattern());

        // Test least movement method
        bop = new BlockPlacer( CryptoboxColumns.MIDDLE, BlockColors.GREY );
        bop.setPlacementMethodPreference( BlockPlacer.PREFER_LEAST_MOVEMENT );
        assertEquals( CryptoboxColumns.MIDDLE, bop.getNextBlockColumn(BlockColors.BROWN) );
        assertEquals( CryptoboxColumns.RIGHT, bop.getNextBlockColumn(BlockColors.BROWN)  );
        assertEquals( CryptoboxColumns.RIGHT, bop.getNextBlockColumn(BlockColors.GREY) );
        assertEquals( CryptoboxColumns.MIDDLE, bop.getNextBlockColumn(BlockColors.GREY) );
    }
}