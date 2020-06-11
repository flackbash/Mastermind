package com.prangesoftwaresolutions.mastermind.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.logic.Color;
import com.prangesoftwaresolutions.mastermind.logic.Peg;

public class SourcePegs extends LinearLayout {
    public SourcePegs(Context context) {
        super(context);
        initializeViews(context);
    }

    public SourcePegs(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SourcePegs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.source_pegs, this);
    }

    /*
     * Set the number of pegs and inflate the corresponding layout
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setPegs(Context context, int numPegs) {
        LinearLayout sourcePegsLL = findViewById(R.id.source_pegs_ll);

        // Remove potential previous source pegs
        sourcePegsLL.removeAllViews();

        // Get layout parameters such that layout weight can be adjusted dynamically
        int width = getResources().getDimensionPixelSize(R.dimen.img_slot_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, 0);
        lp.weight = 1;

        // Add numPegs source-peg-ImageViews to the LinearLayout
        for (int i = 0; i < numPegs; i++) {
            ImageView pegIV = new ImageView(context);
            pegIV.setImageDrawable(Peg.getDrawable(context, Color.valueOf(i)));
            pegIV.setLayoutParams(lp);
            pegIV.setTag("source_peg_" + i);
            pegIV.setOnTouchListener((View.OnTouchListener) context);
            sourcePegsLL.addView(pegIV);
        }
    }
}
