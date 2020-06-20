package com.prangesoftwaresolutions.mastermind.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.prangesoftwaresolutions.mastermind.R;

import java.util.ArrayList;
import java.util.List;

public class Row extends RelativeLayout {
    enum Size {
        TINY, SMALL, NORMAL
    }

    // Row number
    private int mRowNumber = 0;

    // List of slot ImageView pairs <backgroundIV, foregroundIV>
    protected List<Pair<ImageView, ImageView>> mSlotIVList = new ArrayList<>();

    // Number of slots in the row
    int mNumSlots = 4;

    public Row(Context context) {
        super(context);
        initializeViews(context, mNumSlots, Size.NORMAL);
    }

    public Row(Context context, int numSlots, Size size) {
        super(context);
        mNumSlots = numSlots;
        initializeViews(context, mNumSlots, size);
    }

    public Row(Context context, int numSlots, int rowNumber, Size size) {
        super(context);
        mNumSlots = numSlots;
        mRowNumber = rowNumber;
        initializeViews(context, mNumSlots, size);
    }
    /**
     * Inflates the views in the layout.
     */
    @SuppressLint("SetTextI18n")
    protected void initializeViews(Context context, int numSlots, Size size) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.code_row, this);

        // Adjust space size
        Space space = findViewById(R.id.space);
        LinearLayout.LayoutParams lp = getLayoutParamsSpace(size);
        space.setLayoutParams(lp);

        // Set up slot layouts
        LinearLayout backgroundLL = findViewById(R.id.slots_background_ll);
        backgroundLL.removeAllViews();
        LinearLayout foregroundLL = findViewById(R.id.slots_foreground_ll);
        foregroundLL.removeAllViews();

        // Set up hint layouts
        int rowCount = numSlots == 3 ? 1 : 2;
        int columnCount = (int) Math.ceil(numSlots / (float)rowCount);

        GridLayout backgroundHintLL = findViewById(R.id.hints_background_gl);
        backgroundHintLL.removeAllViews();
        backgroundHintLL.setRowCount(rowCount);
        backgroundHintLL.setColumnCount(columnCount);

        GridLayout foregroundHintLL = findViewById(R.id.hints_foreground_gl);
        foregroundHintLL.removeAllViews();
        foregroundHintLL.setRowCount(rowCount);
        foregroundHintLL.setColumnCount(columnCount);

        for (int i = 0; i < numSlots; i++) {
            // Set up background ImageView
            ImageView backgroundIV = new ImageView(context);
            initializeSlot(backgroundIV, i, size, false);
            backgroundLL.addView(backgroundIV);

            // Set up foreground ImageView
            ImageView foregroundIV = new ImageView(context);
            initializeSlot(foregroundIV, i, size, true);
            foregroundLL.addView(foregroundIV);

            // Set up background hint ImageView layout parameters
            ImageView backgroundHintIV = new ImageView(context);
            initializeHint(backgroundHintIV, size, false);
            backgroundHintLL.addView(backgroundHintIV);

            // Set up foreground hint ImageView layout parameters
            ImageView foregroundHintIV = new ImageView(context);
            initializeHint(foregroundHintIV, size, true);
            foregroundHintLL.addView(foregroundHintIV);

            // Add both ImageViews to slot list
            Pair<ImageView, ImageView> pair = new Pair<>(backgroundIV, foregroundIV);
            mSlotIVList.add(pair);
        }

        // Adjust row number weight
        TextView rowNumberTV = findViewById(R.id.row_number);
        LinearLayout.LayoutParams lpRowNum = getLayoutParamsRowNum(size);
        lpRowNum.gravity = Gravity.CENTER;
        LinearLayout allSlotsLL = findViewById(R.id.all_slots_ll);
        LinearLayout.LayoutParams lpAllSlots = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpAllSlots.weight = 10;
        rowNumberTV.setLayoutParams(lpRowNum);
        allSlotsLL.setLayoutParams(lpAllSlots);

        if (mRowNumber > 0) {
            // Set row number text
            rowNumberTV.setText(Integer.toString(mRowNumber));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        this.setLayoutParams(params);
    }

    /*
     * Initialize ImageView for slot (background or foreground)
     */
    protected void initializeSlot(ImageView iv, int index, Row.Size size, boolean foreground) {
        if (!foreground) iv.setImageResource(R.drawable.slot);
        LinearLayout.LayoutParams lp = getLayoutParamsSlot(size);
        String tag = foreground ? "slot_foreground_" : "slot_background_";
        tag += index;
        iv.setTag(tag);
        iv.setLayoutParams(lp);
    }

    /*
     * Initialize ImageView for hint (background or foreground)
     */
    protected void initializeHint(ImageView iv, Row.Size size, boolean foreground) {
        if (!foreground) iv.setImageResource(R.drawable.slot);
        GridLayout.LayoutParams lpHint = getLayoutParamsHint(size);
        iv.setLayoutParams(lpHint);
    }

    /*
     * Determine layout parameters for hints
     */
    private GridLayout.LayoutParams getLayoutParamsHint(Size size) {
        int hintSize;
        int hintMargin;
        switch (size) {
            case TINY:
                hintSize = getResources().getDimensionPixelSize(R.dimen.img_hint_tiny_size);
                hintMargin = getResources().getDimensionPixelSize(R.dimen.margin_tiny);
                break;
            case SMALL:
                hintSize = getResources().getDimensionPixelSize(R.dimen.img_hint_small_size);
                hintMargin = getResources().getDimensionPixelSize(R.dimen.margin_tiny);
                break;
            case NORMAL:
            default:
                hintSize = getResources().getDimensionPixelSize(R.dimen.img_hint_size);
                hintMargin = getResources().getDimensionPixelSize(R.dimen.margin_small);
                break;
        }

        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = hintSize;
        lp.height = hintSize;
        lp.setMargins(hintMargin, hintMargin, hintMargin, hintMargin);
        return lp;
    }

    /*
     * Determine layout parameters for slots
     */
    private LinearLayout.LayoutParams getLayoutParamsSlot(Size size) {
        int slotSize;
        int margin;
        switch (size) {
            case TINY:
                slotSize = getResources().getDimensionPixelSize(R.dimen.img_slot_tiny_size);
                margin = getResources().getDimensionPixelSize(R.dimen.margin_tiny);
                break;
            case SMALL:
                slotSize = getResources().getDimensionPixelSize(R.dimen.img_slot_small_size);
                margin = getResources().getDimensionPixelSize(R.dimen.margin_small);
                break;
            case NORMAL:
            default:
                slotSize = getResources().getDimensionPixelSize(R.dimen.img_slot_size);
                margin = getResources().getDimensionPixelSize(R.dimen.margin_normal);
                break;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(slotSize, slotSize);
        lp.setMargins(margin, margin, margin, margin);
        return lp;
    }

    /*
     * Determine layout parameters for space
     */
    private LinearLayout.LayoutParams getLayoutParamsSpace(Size size) {
        int spaceSize;
        switch (size) {
            case TINY:
                spaceSize = getResources().getDimensionPixelSize(R.dimen.margin_tiny);
                break;
            case SMALL:
                spaceSize = getResources().getDimensionPixelSize(R.dimen.margin_small);
                break;
            case NORMAL:
            default:
                spaceSize = getResources().getDimensionPixelSize(R.dimen.img_hint_tiny_size);
                break;
        }
        return new LinearLayout.LayoutParams(spaceSize, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /*
     * Determine layout parameters for row number TextView
     */
    private LinearLayout.LayoutParams getLayoutParamsRowNum(Size size) {
        int weight = size == Size.NORMAL ? 2 : 1;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = weight;
        return lp;
    }

    /*
     * Reset row
     */
    public void reset() {
        // Reset slot ImageViews
        for (Pair<ImageView, ImageView> pair : mSlotIVList) {
            pair.second.setImageDrawable(null);
        }

        // Reset hint ImageViews
        GridLayout gl = findViewById(R.id.hints_foreground_gl);
        for (int i = 0; i < gl.getChildCount(); i++) {
            ImageView iv = (ImageView) gl.getChildAt(i);
            iv.setImageDrawable(null);
        }
    }
}