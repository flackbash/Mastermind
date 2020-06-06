package com.prangesoftwaresolutions.mastermind.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;
import com.prangesoftwaresolutions.mastermind.logic.Peg;
import com.prangesoftwaresolutions.mastermind.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BoardRow extends RelativeLayout implements View.OnDragListener, View.OnTouchListener {

    // List of target pairs <backgroundIV, foregroundIV>
    private List<Pair<ImageView, ImageView>> mTargetList = new ArrayList<>();

    // Row number
    private int mRowNumber;

    // Boolean indicating whether row is active
    private boolean mActive;

    // Pegs
    Peg mDraggedPeg;
    Peg[] mPegArray = new Peg[4];

    // Listener for game status changes
    GameStatusEventListener mListener;

    public BoardRow(Context context) {
        super(context);
        initializeViews(context);
    }

    public BoardRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BoardRow);
        mRowNumber = typedArray.getInt(R.styleable.BoardRow_row_number, 0);
        mActive = typedArray.getBoolean(R.styleable.BoardRow_active, false);
        typedArray.recycle();

        initializeViews(context);

    }

    public BoardRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BoardRow);
        mRowNumber = typedArray.getInt(R.styleable.BoardRow_row_number, 0);
        mActive = typedArray.getBoolean(R.styleable.BoardRow_active, false);
        typedArray.recycle();

        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.board_row, this);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Set target background
        LinearLayout backgroundLL = findViewById(R.id.background_ll);
        LinearLayout foregroundLL = findViewById(R.id.foreground_ll);
        for (int i = 0; i < backgroundLL.getChildCount(); i++) {
            // Set up background ImageView
            ImageView backgroundIV = (ImageView) backgroundLL.getChildAt(i);
            backgroundIV.setTag("target_background_" + i);
            backgroundIV.setOnDragListener(this);

            // Set up foreground ImageView
            ImageView foregroundIV = (ImageView) foregroundLL.getChildAt(i);
            foregroundIV.setTag("target_" + i);
            foregroundIV.setOnTouchListener(this);

            // Add both ImageViews to target list
            Pair<ImageView, ImageView> pair = new Pair<>(backgroundIV, foregroundIV);
            mTargetList.add(pair);
        }

        // Highlight target backgrounds if row is active
        if (mActive) {
            setBackgroundImages(true);
        }

        // Row number
        TextView mRowNumberTV = findViewById(R.id.row_number);
        mRowNumberTV.setText(Integer.toString(mRowNumber));
    }

    /*
     * Set activation status of row and update background images accordingly.
     */
    public void setActive(boolean active) {
        mActive = active;
        setBackgroundImages(active);
    }

    /*
     * Set currently dragged peg.
     */
    public void setDraggedPeg(Peg draggedPeg) {
        mDraggedPeg = draggedPeg;
    }

    /*
     * Set GameStatusEventListener.
     */
    public void setListener(GameStatusEventListener listener) {
        mListener = listener;
    }

    /*
     * Set background images of targets and hints in row depending on whether row is active.
     */
    private void setBackgroundImages(boolean active) {
        int imageResource;
        if (active) {
            imageResource = R.drawable.target_active;
        } else {
            imageResource = R.drawable.target;
        }

        // Set target background
        LinearLayout ll = findViewById(R.id.background_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            ImageView iv = (ImageView) ll.getChildAt(i);
            iv.setImageResource(imageResource);
        }

        // Set hint background
        GridLayout gl = findViewById(R.id.background_hints_gl);
        for (int i = 0; i < gl.getChildCount(); i++) {
            ImageView iv = (ImageView) gl.getChildAt(i);
            iv.setImageResource(imageResource);
        }
    }

    @Override
    public boolean onDrag(final View v, DragEvent event) {
        // Do not accept drag if row is inactive
        if (!mActive) {
            return false;
        }

        int action = event.getAction();
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
            case DragEvent.ACTION_DRAG_EXITED:
                return true;

            case DragEvent.ACTION_DROP:
                // Get index of drop-target
                String tag = v.getTag().toString();
                int targetIndex = Utils.getTargetIndex(tag);

                // Get drop target foreground image
                ImageView target = mTargetList.get(targetIndex).second;
                Drawable draggedDrawable = mDraggedPeg.getDrawable();
                target.setImageDrawable(draggedDrawable);

                // Do not remove the draggedIV if it was a source peg or it is dropped at the spot
                // from where it was dragged.
                String draggedTag = mDraggedPeg.getImageView().getTag().toString();
                if (draggedTag.startsWith("target") && Utils.getLastChar(tag) != Utils.getLastChar(draggedTag)) {
                    mDraggedPeg.getImageView().setImageDrawable(null);
                }

                mDraggedPeg.setImageView(target);
                mPegArray[targetIndex] = mDraggedPeg;

                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (event.getResult()) {
                    // Drop succeeded. Reset values of origin ImageView
                    if (mDraggedPeg.getLastImageView() != null) {
                        mDraggedPeg.getLastImageView().setVisibility(View.VISIBLE);
                        mDraggedPeg.getLastImageView().setAlpha(1.0f);
                    }

                    // Notify listener that row was completed
                    if (isRowComplete()) {
                        mListener.onRowComplete();
                    }
                } else {
                    // Drop failed. Reset values of ImageView
                    mDraggedPeg.getImageView().setVisibility(View.VISIBLE);
                    mDraggedPeg.getImageView().setAlpha(1.0f);

                    // If the drag was started from a target discard the peg
                    if (mDraggedPeg.getImageView().getTag().toString().startsWith("target")) {
                        mDraggedPeg.getImageView().setImageDrawable(null);
                        targetIndex = Utils.getTargetIndex(mDraggedPeg.getImageView().getTag().toString());
                        mPegArray[targetIndex] = null;
                    }
                }

                mDraggedPeg.clearLastImageView();

                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Only allow drag if row is active and target contains a peg
            if (!mActive || ((ImageView) v).getDrawable() == null) {
                return false;
            }

            // Create drag data
            ClipData.Item item = new ClipData.Item(v.getTag().toString());
            ClipData dragData = new ClipData(v.getTag().toString(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

            // Create DragShadow
            DragShadowBuilder myShadow = new DragShadowBuilder(v);

            // Start the drag
            v.startDrag(dragData, myShadow, null, 0);

            // Get dragged Peg and update Peg Array
            int targetIndex = Utils.getTargetIndex(v.getTag().toString());
            Peg draggedPeg = mPegArray[targetIndex];
            setDraggedPeg(draggedPeg);

            // Hide dragged peg in its origin ImageView
            draggedPeg.getImageView().setVisibility(INVISIBLE);
        }
        return true;
    }

    /*
     * Check if row is completely filled with pegs.
     */
    private boolean isRowComplete() {
        for (Pair<ImageView, ImageView> pair : mTargetList) {
            if (pair.second.getDrawable() == null)
                return false;
        }
        return true;
    }

    /*
     * Get current code from peg array.
     */
    public int[] getCode() {
        int[] code = new int[mPegArray.length];
        for (int i = 0; i < mPegArray.length; i++) {
            code[i] = mPegArray[i].getColor().getValue();
        }
        return code;
    }

    /*
     * Display hints according to result.
     */
    public void showHints(List<Integer> result) {
        GridLayout gl = findViewById(R.id.foreground_hints_gl);
        for (int i = 0; i < result.size(); i++) {
            ImageView iv = (ImageView) gl.getChildAt(i);
            if (result.get(i) == 2) {
                iv.setImageResource(R.drawable.peg_black);
            } else if (result.get(i) == 1) {
                iv.setImageResource(R.drawable.peg_white);
            }
        }
    }

    /*
     * Reset board row.
     */
    public void reset() {
        // Reset variables
        setActive(false);
        mPegArray = new Peg[4];

        // Reset target views
        for (Pair<ImageView, ImageView> pair : mTargetList) {
            pair.second.setImageDrawable(null);
        }

        // Reset hint views
        GridLayout gl = findViewById(R.id.foreground_hints_gl);
        for (int i = 0; i < gl.getChildCount(); i++) {
            ImageView iv = (ImageView) gl.getChildAt(i);
            iv.setImageDrawable(null);
        }
    }
}