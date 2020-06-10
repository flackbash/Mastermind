package com.prangesoftwaresolutions.mastermind.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;

public class SourcePegs extends LinearLayout{

    // Number of pegs
    int mNumPegs = 6;

    // Context
    Context mContext;

    public SourcePegs(Context context) {
        super(context);
        mContext = context;
    }

    public SourcePegs(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SourcePegs);
        mNumPegs = typedArray.getInt(R.styleable.SourcePegs_num_pegs, mNumPegs);
        typedArray.recycle();

        mContext = context;
    }

    public SourcePegs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SourcePegs);
        mNumPegs = typedArray.getInt(R.styleable.SourcePegs_num_pegs, mNumPegs);
        typedArray.recycle();

        mContext = context;
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context, int numPegs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        int sourcePegsLayout;
        switch (numPegs) {
            case 4:
                sourcePegsLayout = R.layout.source_pegs_4;
                break;
            case 5:
                sourcePegsLayout = R.layout.source_pegs_5;
                break;
            case 6:
                sourcePegsLayout = R.layout.source_pegs_6;
                break;
            case 7:
                sourcePegsLayout = R.layout.source_pegs_7;
                break;
            case 8:
                sourcePegsLayout = R.layout.source_pegs_8;
                break;
            case 9:
                sourcePegsLayout = R.layout.source_pegs_9;
                break;
            default:
                sourcePegsLayout = R.layout.source_pegs_6;
                break;
        }
        inflater.inflate(sourcePegsLayout, this);
    }

    /*
     * Set OnTouchListener for source pegs ImageViews
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setOnTouchListener(View.OnTouchListener listener) {
        LinearLayout sourcePegsLL = findViewById(R.id.source_pegs_ll);
        for (int i = 0; i < sourcePegsLL.getChildCount(); i++) {
            // Set up source pegs
            ImageView pegIV = (ImageView) sourcePegsLL.getChildAt(i);
            pegIV.setTag("source_peg_" + i);
            pegIV.setOnTouchListener(listener);
        }
    }

    /*
     * Set the number of pegs and inflate the corresponding layout
     */
    public void setNumPegsAndInflate(Context context, int numPegs) {
        mNumPegs = numPegs;
        mContext = context;
        initializeViews(context, mNumPegs);
    }
}
