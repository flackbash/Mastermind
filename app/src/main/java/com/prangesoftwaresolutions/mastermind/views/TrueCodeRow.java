package com.prangesoftwaresolutions.mastermind.views;

import android.content.Context;
import android.content.res.TypedArray;
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

    // Number of slots
    int mNumSlots = 4;

    // Target list
    List<ImageView> mTargetList = new ArrayList<>();

    public TrueCodeRow(Context context) {
        super(context);
        initializeViews(context, mNumSlots);
    }

    public TrueCodeRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrueCodeRow);
        mNumSlots = typedArray.getInt(R.styleable.TrueCodeRow_slot_num, 4);
        typedArray.recycle();

        initializeViews(context, mNumSlots);

    }

    public TrueCodeRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrueCodeRow);
        mNumSlots = typedArray.getInt(R.styleable.TrueCodeRow_slot_num, 4);
        typedArray.recycle();

        initializeViews(context, mNumSlots);
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context, int numSlots) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        int boardRowLayout;
        switch (numSlots) {
            case 3:
                boardRowLayout = R.layout.true_code_row_3;
                break;
            case 4:
                boardRowLayout = R.layout.true_code_row_4;
                break;
            case 5:
                boardRowLayout = R.layout.true_code_row_5;
                break;
            case 6:
                boardRowLayout = R.layout.true_code_row_6;
                break;
            default:
                boardRowLayout = R.layout.true_code_row_4;
                break;
        }

        inflater.inflate(boardRowLayout, this);
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