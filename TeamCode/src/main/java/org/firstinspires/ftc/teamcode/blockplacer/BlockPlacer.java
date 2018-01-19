package org.firstinspires.ftc.teamcode.blockplacer;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
    public static final int PREFER_COLUMNS = 0; // Prefer columns not done.
    public static final int PREFER_ROWS = 1;
    public static final int PREFER_LEAST_MOVEMENT = 2;
    private int PlacementMethodPreference = PREFER_ROWS;
    private int LastColumn = CryptoboxColumns.INVALID;
    public void setPlacementMethodPreference( int iPreference ) { PlacementMethodPreference = iPreference; }

    /**
     * Data about blocks we have already placed.
     */
    ArrayList< Block > CryptoboxState;

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
                // This method has been benched.
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
            case PREFER_LEAST_MOVEMENT:
                // Should never be invalid, but just in case...
                if( LastColumn == INVALID || !IntArrayToArrayList(viableColumns).contains(LastColumn) )
                {
                    // We either havent placed a block or we cannot place our block in the same column we just placed it in.
                    // Therefore placement requires horizontal movement and we have "broken formation"
                    // Choose from our preferred columns TODO: Choose next column based on distance from current column.
                    columnloop:
                    for( int iColumnPreference : CryptoColumnPreference )
                    {
                        for( int iColumn : viableColumns )
                        {
                            if( iColumnPreference == iColumn )
                            {
                                nextBlockColumn = iColumn;
                                LastColumn = iColumn;
                                break columnloop;
                            }
                        }
                    }
                }
                else
                {
                    nextBlockColumn = LastColumn;
                }

                Block topmostBlockForColumn = getColumnTopmostBlocks().get(nextBlockColumn);
                nextBlockRow = topmostBlockForColumn != null ? topmostBlockForColumn.row + 1 : 0; // If null (meaning no block) the next row is 0.

                break;
        }

        if( nextBlockColumn != CryptoboxColumns.INVALID && nextBlockRow != -1 )
        {
            CryptoboxState.add( new Block( nextBlockColumn, nextBlockRow, nextBlockColor ) );
        }

        return nextBlockColumn;
    }

    /**
     * @return the columns in which a block can be placed that lead to a valid CryptoKey.
     */
    private int[] getViableColumns( int blockColor )
    {
        ArrayList< Block > ColumnsTopmostBlocks = getColumnTopmostBlocks();

        ArrayList<Integer> ViableColumns = new ArrayList<Integer>() {{
            add(LEFT);
            add(MIDDLE);
            add(RIGHT);
        }};

        // Determine viable columns
        for( int iColumn = 0; iColumn < ColumnsTopmostBlocks.size(); iColumn++ )
        {
            Block topmostBlock = ColumnsTopmostBlocks.get(iColumn);

            // Remove column if full ( Having 4 blocks )
            if( topmostBlock != null && topmostBlock.row >= 3 )
            {
                ViableColumns.remove( topmostBlock.column );
            }

            // If there is no space in the cryptobox return early
            if( ViableColumns.size() == 0 )
            {
                return new int[0];
            }

            // Remove column if a block placed in it would not lead to a cipher
            int targetRow = topmostBlock == null ? 0 : topmostBlock.row + 1;

            if( this.CryptoPatterns[CurrentPattern][targetRow][iColumn] != blockColor )
            {
                // have to cast to Integer so that AL.remove removes the matching object not the object at the index.
                ViableColumns.remove( Integer.valueOf(iColumn) );
            }

        }

        return ArraylistToIntArray( ViableColumns );
    }

    private ArrayList< Block > getColumnTopmostBlocks()
    {
        // Implicit indexing based on the order of CryptoboxColumns.
        ArrayList< Block > ColumnsTopmostBlocks = new ArrayList< Block >(){{
            add(null); add(null); add(null);
        }};

        // Loop through placed blocks to find topmost blocks.
        for( Block curBlock : CryptoboxState )
        {
            Block curBlockColumnTopmostBlock = ColumnsTopmostBlocks.get(curBlock.column);

            if( curBlockColumnTopmostBlock == null || curBlock.row > curBlockColumnTopmostBlock.row )
            {
                ColumnsTopmostBlocks.set( curBlock.column, curBlock );
            }
        }
        return ColumnsTopmostBlocks;
    }

    /**
     * Public constructor
     */
    public BlockPlacer( int firstBlockColumn, int firstBlockColor )
    {
        CryptoboxState = new ArrayList<>();
        CryptoboxState.add( new Block(firstBlockColumn, 0 , firstBlockColor ));
        LastColumn = firstBlockColumn;
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

    private int[][] sortCryptoboxState( ArrayList< Block > cryptoboxState )
    {
        int[][] sortedCryptoboxState = new int[4][3]; // 4 rows, 3 columns

        for( int[] a : sortedCryptoboxState )
            Arrays.fill(a, BlockColors.INVALID);

        for( Block curBlock : cryptoboxState )
        {
            sortedCryptoboxState[curBlock.row][curBlock.column] = curBlock.color;
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

    /**
     * Inverts the current cryptobox state such that blocks are addressable by [row][column] as
     * opposed to [column][row]
     * @return the inverted cryptobox state.
     */
    private int[][] getInvertedCryptoboxState()
    {
        int[][] sortedCryptoboxState = sortCryptoboxState(CryptoboxState);
        int[][] invertedCryptoboxState = new int[sortedCryptoboxState[0].length][sortedCryptoboxState.length]; // Inverse dimensions

        for( int iRowIndex = 0; iRowIndex < sortedCryptoboxState[0].length; iRowIndex++ )
        {
            for( int iColumnIndex = 0; iColumnIndex < sortedCryptoboxState.length; iColumnIndex++ )
            {
                invertedCryptoboxState[iRowIndex][iColumnIndex] = sortedCryptoboxState[iColumnIndex][iRowIndex];
            }
        }

        return invertedCryptoboxState;
    }
}
