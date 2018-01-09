package org.firstinspires.ftc.teamcode.blockplacer;

import com.google.blocks.ftcrobotcontroller.runtime.Block;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;

import org.firstinspires.ftc.teamcode.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.firstinspires.ftc.teamcode.blockplacer.CryptoPatterns.*;
import static org.firstinspires.ftc.teamcode.blockplacer.CryptoboxColumns.*;

/**
 * Created by noah on 1/4/18.
 * Informs the robot where to place a block given its color.
 */

public class BlockPlacer {

    /**
     * Pattern definitions and preference with index 0 being highest preference.
     * Has no setter as not expected to change with starting position or alliance.
     */
    private final int[][][] CryptoPatterns = new int[][][] { FROG, BIRD, SNAKE };
    private int CurrentPattern = 0; // The index of pattern being used by ai in CryptoPatterns.
    public int[][] getPattern(){ return CryptoPatterns[CurrentPattern]; }

    /**
     * Column placement preference with index 0 being highest preference.
     */
    private int[] CryptoColumnPreference = new int[] { MIDDLE, RIGHT, LEFT };
    public void setCryptoColumnPreference( int[] iPreference ) { CryptoColumnPreference = iPreference; }

    /**
     * Placement method preference. Use setPlacementMethod with PREFER_COLUMNS or PREFER_ROWS to indicate preference.
     */
    private static final int PREFER_COLUMNS = 0;
    private static final int PREFER_ROWS = 1;
    private int PlacementMethodPreference = PREFER_ROWS;
    public void setPlacementMethodPreference( int iPreference ) { PlacementMethodPreference = iPreference; }

    /**
     * Data about blocks we have already placed.
     * CryptoboxState may be tricky to understand. Its an arraylist where each element has a column/row
     * as a pair and a color all bundled into a pair. Oof, braintwister.
     *  ((Column, Row), Color). With the bottom row being 0 and top row being 3.
     */
    ArrayList< Pair< Pair< Integer, Integer >, Integer > > CryptoboxState;

    /**
     * The "thinking" function of the AI. Call this when a block has been scanned to determine drive position.
     * @param nextBlockColor the color of the next block to be placed.
     * @return the position in which to place the block.
     */
    public int getNextBlockColumn( int nextBlockColor )
    {
        int nextBlockColumn = CryptoboxColumns.INVALID;
        int nextBlockRow = -1;

        int[] viableColumns = getViableColumns(nextBlockColor);
        int[][] sortedCryptoboxState = sortCryptoboxState( CryptoboxState );
        ArrayList<Integer> nextBlockColumnCanidates = new ArrayList<>();

        if( viableColumns.length == 0 )
        {
            // Attempt to use a different pattern
            while(true)
            {
                CurrentPattern++;

                if( CurrentPattern >= CryptoPatterns.length )
                    return CryptoboxColumns.INVALID;

                if( doesCryptoboxStateFitPattern() )
                    return getNextBlockColumn( nextBlockColor );
            }
        }

        switch ( PlacementMethodPreference )
        {
            case PREFER_COLUMNS:
                break;
            case PREFER_ROWS:
            default:
                // Loop through rows, find empty space.
                for( int iIndexRow = 0; iIndexRow < sortedCryptoboxState.length; iIndexRow++ )
                {
                    ArrayList<Integer> emptySpaceInRow = new ArrayList<>();

                    for( int iIndexColumn = 0; iIndexColumn < sortedCryptoboxState[iIndexRow].length; iIndexColumn++ )
                    {
                        if( sortedCryptoboxState[iIndexRow][iIndexColumn] == BlockColors.INVALID )
                        {
                            emptySpaceInRow.add( iIndexColumn );
                        }
                    }

                    // Find matching elements
                    if( emptySpaceInRow.size() != 0 )
                    {
                        for( int iColumn : emptySpaceInRow )
                        {
                            for( int iViableColumn : viableColumns )
                            {
                                if( iColumn == iViableColumn )
                                {
                                    nextBlockColumnCanidates.add( iColumn );
                                }
                            }
                        }

                        // We have found an incomplete row. No need to keep searching
                        nextBlockRow = iIndexRow;
                        break;
                    }
                }

                if( nextBlockColumnCanidates.size() == 0 )
                {
                    nextBlockColumnCanidates = IntArrayToArrayList( viableColumns );
                }

                if( nextBlockColumnCanidates.size() == 1 )
                {
                    nextBlockColumn = nextBlockColumnCanidates.get(0);
                    break;
                }

                // Use the prefered column if possible
                columnloop:
                for( int iColumnPreference : CryptoColumnPreference )
                {
                    for( int iColumn : nextBlockColumnCanidates )
                    {
                        if( iColumnPreference == iColumn )
                        {
                            nextBlockColumn = iColumn;
                            break columnloop;
                        }
                    }
                }

                break;
        }

        if( nextBlockColumn != CryptoboxColumns.INVALID && nextBlockRow != -1 )
        {
            CryptoboxState.add( new Pair<>( new Pair<>( nextBlockColumn, nextBlockRow ), nextBlockColor));
        }

        return nextBlockColumn;
    }

    /**
     * @return the columns in which a block can be placed that lead to a valid CryptoKey.
     */
    private int[] getViableColumns( int blockColor )
    {
        // Another one of the reasons I like C more than java... TYPEDEFS!
        // Implicit indexing based on the order of CryptoboxColumns. Cant really think of a more elegant way to do this. Sorry DavidB.
        ArrayList< Pair< Pair< Integer, Integer >, Integer > > ColumnsTopmostBlocks = new ArrayList< Pair< Pair< Integer, Integer >, Integer > >(){{
            add(null); add(null); add(null);
        }};

        // Loop through placed blocks to find topmost blocks.
        for( Pair< Pair< Integer, Integer >, Integer > curBlock : CryptoboxState )
        {
            Integer curBlockColumn = curBlock.fst.fst;
            Integer curBlockRow = curBlock.fst.snd;

            Pair< Pair< Integer, Integer >, Integer > curBlockColumnTopmostBlock = ColumnsTopmostBlocks.get(curBlockColumn);

            if( curBlockColumnTopmostBlock == null || curBlockRow > curBlockColumnTopmostBlock.fst.snd )
            {
                ColumnsTopmostBlocks.set( curBlockColumn, curBlock );
            }
        }

        ArrayList<Integer> ViableColumns = new ArrayList<Integer>() {{
            add(LEFT);
            add(MIDDLE);
            add(RIGHT);
        }};

        // We no longer need null elements in ColumnsTopmostBlocks
        //ColumnsTopmostBlocks.removeAll( Collections.<Pair<Pair<Integer, Integer>, Integer>>singleton( null ) );

        // Determine viable columns
        for( int iColumn = 0; iColumn < ColumnsTopmostBlocks.size(); iColumn++ )
        {
            Pair<Pair<Integer, Integer>, Integer> topmostBlock = ColumnsTopmostBlocks.get(iColumn);

            // Remove column if full ( Having 4 blocks )
            if( topmostBlock != null && topmostBlock.fst.snd >= 3 ) // Row
            {
                ViableColumns.remove( topmostBlock.fst.fst ); // Column
            }

            // If there is no space in the cryptobox return early
            if( ViableColumns.size() == 0 )
            {
                return new int[0];
            }

            // Remove column if a block placed in it would not lead to a cipher
            int targetRow = topmostBlock == null ? 0 : topmostBlock.fst.snd + 1;

            if( this.CryptoPatterns[CurrentPattern][targetRow][iColumn] != blockColor )
            {
                // have to cast to Integer so that AL.remove removes the matching object not the object at the index.
                ViableColumns.remove( Integer.valueOf(iColumn) );
            }

        }

        return ArraylistToIntArray( ViableColumns );
    }

    /**
     * Public constructor
     */
    public BlockPlacer( int firstBlockColumn, int firstBlockColor )
    {
        CryptoboxState = new ArrayList<>();
        CryptoboxState.add( new Pair<>( new Pair<>( firstBlockColumn, 0 ), firstBlockColor ));
    }

    /**
     *  Disallow default constructor
     */
    private BlockPlacer(){}

    /**
     * Utility functions
     */
    private int[] ArraylistToIntArray( ArrayList< Integer > arrayList )
    {
        arrayList.trimToSize();
        int[] v = new int[ arrayList.size() ];
        for( int iIndex = 0; iIndex < arrayList.size(); iIndex++ )
        {
            v[iIndex] = arrayList.get(iIndex);
        }
        return v;
    }

    private ArrayList<Integer> IntArrayToArrayList(final int[] intArray )
    {
        return new ArrayList<Integer>() {{ for (int i : intArray) add(i); }};
    }

    private int[][] sortCryptoboxState( ArrayList< Pair< Pair< Integer, Integer >, Integer > > cryptoboxState )
    {
        int[][] sortedCryptoboxState = new int[4][3]; // 4 rows, 3 columns

        for( int[] a : sortedCryptoboxState )
            Arrays.fill(a, BlockColors.INVALID);

        for( Pair< Pair< Integer, Integer >, Integer > curBlock : cryptoboxState )
        {
            sortedCryptoboxState[curBlock.fst.snd][curBlock.fst.fst] = curBlock.snd;
        }

        return sortedCryptoboxState;
    }

    private boolean doesCryptoboxStateFitPattern()
    {
        int[][] sortedCryptoboxState = sortCryptoboxState( CryptoboxState );

        for( int iRowIndex = 0; iRowIndex < sortedCryptoboxState.length; iRowIndex++ )
        {
            for( int iColumnIndex = 0; iColumnIndex < sortedCryptoboxState[iRowIndex].length; iColumnIndex++ )
            {
                int cryptoboxBlockColorAtPosition = sortedCryptoboxState[iRowIndex][iColumnIndex];
                if( cryptoboxBlockColorAtPosition != CryptoPatterns[CurrentPattern][iRowIndex][iColumnIndex] && cryptoboxBlockColorAtPosition != BlockColors.INVALID )
                {
                    return false;
                }
            }
        }

        return true;
    }
}
