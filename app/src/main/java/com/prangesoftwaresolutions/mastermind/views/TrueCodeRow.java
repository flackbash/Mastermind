package com.prangesoftwaresolutions.mastermind.views;

import android.content.Context;
import android.widget.ImageView;

import com.prangesoftwaresolutions.mastermind.logic.Color;
import com.prangesoftwaresolutions.mastermind.logic.Peg;


public class TrueCodeRow extends Row {

    public TrueCodeRow(Context context) {
        super(context);
    }

    public TrueCodeRow(Context context, int numSlots, Size size) {
        super(context, numSlots, size);
    }

    /*
     * Initialize ImageView for hint (background or foreground)
     */
    @Override
    protected void initializeHint(ImageView iv, Row.Size size, boolean foreground) {
        super.initializeHint(iv, size, foreground);
        iv.setVisibility(INVISIBLE);
    }

    /*
     * Set pegs according to given code.
     */
    public void setPegs(int[] code, Context context) {
        for (int i = 0; i < code.length; i++) {
            Color color = Color.valueOf(code[i]);
            Peg peg = new Peg(color, context);
            mSlotIVList.get(i).second.setImageDrawable(peg.getDrawable());
        }
    }
}