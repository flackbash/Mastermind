package com.prangesoftwaresolutions.mastermind.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board extends LinearLayout {

    // View related variables
    List<CodeRow> mCodeRowList;
    TrueCodeRow mTrueCodeRow;
    SourcePegs mSourcePegs;

    // Currently active code row
    int mActiveRowIndex;

    public Board(Context context) {
        super(context);
        initializeViews(context);
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public Board(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.board, this);
    }

    /*
     * Initialize the board by determining the number of rows, initializing the CodeRows
     * accordingly as well as the source pegs
     */
    public void initializeBoard(Context context, int numPegs, int numColors) {
        // Determine how many rows are needed
        int numRows = getNumRows(numPegs, numColors);

        LinearLayout codeRowLL = findViewById(R.id.board_row_ll);

        // Remove potential previous code rows
        codeRowLL.removeAllViews();

        // Determine size of code row image views
        CodeRow.Size size = getSize(numPegs, numRows);

        // Get layout parameters such that layout weight can be adjusted dynamically
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        lp.weight = 1;
        mTrueCodeRow = new TrueCodeRow(context, numPegs, size);
        mTrueCodeRow.setLayoutParams(lp);
        codeRowLL.addView(mTrueCodeRow);

        // Add code rows to the LinearLayout
        mCodeRowList = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            CodeRow codeRow = new CodeRow(context, numPegs, numRows - i, size);
            codeRow.setListener((GameStatusEventListener)context);
            codeRow.setLayoutParams(lp);
            codeRowLL.addView(codeRow);
            mCodeRowList.add(codeRow);
        }
        Collections.reverse(mCodeRowList);

        // Activate first row
        mActiveRowIndex = 0;
        mCodeRowList.get(mActiveRowIndex).setActive(true);

        // Set source peg views
        mSourcePegs = findViewById(R.id.source_pegs);
        mSourcePegs.setPegs(context, numColors);
    }

    /*
     * Reset board by removing pegs from rows and activating first row
     */
    public void resetBoard() {
        mTrueCodeRow.reset();
        for (CodeRow br : mCodeRowList) {
            br.reset();
        }
        mActiveRowIndex = 0;
        mCodeRowList.get(mActiveRowIndex).setActive(true);
    }

    /*
     * Determine the number of rows to display based on the number of pegs and the number of colors.
     * See https://www.sciencedirect.com/science/article/pii/S0304397515005496 section 6 for an
     * analysis of the difficulty in different scenarios
     */
    private int getNumRows(int numPegs, int numColors) {
        int base = numPegs < 5 ? 4 : 5;
        return base + numColors;
    }

    private CodeRow.Size getSize(int numPegs, int numRows) {
        CodeRow.Size size;
        if (numRows < 11 && numPegs < 5) {
            size = CodeRow.Size.NORMAL;
        } else if ((numRows > 10 || numPegs == 5) && (numRows < 13 && numPegs < 6)) {
            size = CodeRow.Size.SMALL;
        } else {
            size = CodeRow.Size.TINY;
        }
        return size;
    }

    /*
     * Activate the next row on the board
     */
    public boolean activateNextRow() {
        mCodeRowList.get(mActiveRowIndex).setActive(false);
        mActiveRowIndex += 1;
        if (mActiveRowIndex < mCodeRowList.size()) {
            mCodeRowList.get(mActiveRowIndex).setActive(true);
            return true;
        }
        return false;
    }

    /*
     * Deactivate current row
     */
    public void deactivateRow() {
        mCodeRowList.get(mActiveRowIndex).setActive(false);
    }

    /*
     * Get currently active row on the board
     */
    public CodeRow getActiveRow() {
        return mCodeRowList.get(mActiveRowIndex);
    }

    /*
     * Get true code row
     */
    public TrueCodeRow getTrueCodeRow() {
        return mTrueCodeRow;
    }
}
