package com.webcheckers.model.Board;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The data structure to represent the rows on the board
 */
public class Row implements Iterable {

    // The index representing how far from the top this row lies.
    private int index;
    // The list of spaces that are contained in this row.
    private ArrayList<Space> spaces;

    public Row(int index) {
        this.index = index;
        this.spaces = new ArrayList<>();
    }

    /**
     * Get the index of this row
     *
     * @return and integer representing the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Add a space to this set of rows, should never be more or less than 8 spaces.
     *
     * @param add the space to be added.
     */
    public void addSpace(Space add) {
        spaces.add(add);
    }

    /**
     * Create a duplicate space of the one at the given index to create an identical board
     * for the white player.
     *
     * @param index the space that needs to be duplicated
     * @return the duplicate of the space at the given index
     */
    public Space duplicateSpace(int index) {
        Space oldSpace = spaces.get(index);
        int oldCol = oldSpace.getCellIdx();
        int oldRow = oldSpace.getMyRow();
        Piece oldPiece = oldSpace.getPiece();
        Space.COLOR oldColor = oldSpace.getColor();
        Space returnSpace = new Space(7 - oldRow, 7 - oldCol, oldColor, oldPiece);
        return returnSpace;
    }

    /**
     * Get the number of spaces in the row.
     *
     * @return the size of the spaces array
     */
    public int getSize() {
        return spaces.size();
    }

    /**
     * Get the space at the given index.
     *
     * @param index the index of the space wanted
     * @return the space at the specified index on the board
     */
    public Space getSpace(int index) {
        return spaces.get(index);
    }

    /**
     * Class to represent the iterator to loop through all of the spaces in the row.
     *
     * @return the iterator
     */
    public Iterator<Space> iterator() {
        return new Iterator<Space>() {

            int current;

            @Override
            public boolean hasNext() {
                if (current < spaces.size()) {
                    return true;
                }
                return false;
            }

            @Override
            public Space next() {
                Space tempSpace = spaces.get(current);
                current++;
                return tempSpace;
            }
        };
    }
}
