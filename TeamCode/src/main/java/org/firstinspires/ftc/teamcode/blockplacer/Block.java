package org.firstinspires.ftc.teamcode.blockplacer;

/**
 * Created by NoahR on 1/9/18.
 * Data structure to represent a block and its position in the cryptobox
 */

public class Block {
    Integer column, row, color;

    public Block( Integer column, Integer row, Integer color) { this.column = column; this.row = row; this.color = color; }

    @Override
    public String toString()
    {
        return column + " " + row + " " + color;
    }
}
