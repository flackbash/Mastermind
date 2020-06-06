package com.prangesoftwaresolutions.mastermind.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.logic.Color;
import com.prangesoftwaresolutions.mastermind.logic.Peg;

import java.util.ArrayList;
import java.util.List;


public class TrueCodeRow extends RelativeLayout {

    // Boolean indicating whether true code is shown or not
    private boolean mShow;

    // Target list
    List<ImageView> mTargetList = new ArrayList<>();

    public TrueCodeRow(Context context) {
        super(context);
        initializeViews(context);
    }

    public TrueCodeRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);

    }

    public TrueCodeRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.true_code_row, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        LinearLayout foregroundLL = findViewById(R.id.foreground_ll);
        for (int i = 0; i < foregroundLL.getChildCount(); i++) {
            ImageView foregroundIV = (ImageView) foregroundLL.getChildAt(i);
            mTargetList.add(foregroundIV);
        }
    }

    /*
     * Set activation status of row and update background images accordingly.
     */
    public void show(boolean show) {
        mShow = show;
    }

    /*
     * Set pegs according to given code.
     */
    public void setPegs(int[] code, Context context) {
        for (int i = 0; i < code.length; i++) {
            Color color = Color.valueOf(code[i]);
            Peg peg = new Peg(color, context);
            mTargetList.get(i).setImageDrawable(peg.getDrawable());
        }
    }

    /*
     * Reset true code row
     */
    public void reset() {
        // Reset variables
        show(false);

        // Reset views
        for (ImageView iv : mTargetList) {
            iv.setImageDrawable(null);
        }
    }

}