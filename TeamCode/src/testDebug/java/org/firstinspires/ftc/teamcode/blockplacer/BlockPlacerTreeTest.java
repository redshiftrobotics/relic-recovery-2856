package org.firstinspires.ftc.teamcode.blockplacer;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by NoahR on 2/3/18.
 */
public class BlockPlacerTreeTest extends TestCase {
    public void testGetBlockPlacement() throws Exception {
        assertTrue( Arrays.equals(new int[]{CryptoboxColumns.RIGHT, CryptoboxColumns.RIGHT}, BlockPlacerTree.getBlockPlacement( CryptoboxColumns.MIDDLE, BlockColors.BROWN, BlockColors.GREY )));
    }

}