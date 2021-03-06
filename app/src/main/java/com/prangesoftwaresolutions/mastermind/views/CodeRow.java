package com.prangesoftwaresolutions.mastermind.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;
import com.prangesoftwaresolutions.mastermind.logic.Peg;
import com.prangesoftwaresolutions.mastermind.utils.Utils;

import java.util.List;

public class CodeRow extends Row implements View.OnDragListener, View.OnTouchListener {
    // Boolean indicating whether row is active
    private boolean mActive;

    // Pegs
    Peg mDraggedPeg;
    Peg[] mSlotArray;

    // Listener for game status changes
    GameStatusEventListener mListener;

    public CodeRow(Context context) {
        super(context);
        mSlotArray = new Peg[mNumSlots];
    }

    public CodeRow(Context context, int numSlots, int rowNumber, Size size) {
        super(context, numSlots, rowNumber, size);
        mSlotArray = new Peg[mNumSlots];
    }

    /**
     * Inflates the views in the layout.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeViews(Context context, int numSlots, Size size) {
        super.initializeViews(context, numSlots, size);

        // Highlight slot backgrounds if row is active
        if (mActive) {
            setBackgroundImages(true);
        }
    }

    /*
     * Initialize ImageView for slot (background or foreground)
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initializeSlot(ImageView iv, int index, Row.Size size, boolean foreground) {
        super.initializeSlot(iv, index, size, foreground);
        if (foreground) {
            iv.setOnTouchListener(this);
        } else {
            iv.setOnDragListener(this);
        }
    }

    /*
     * Reset board row.
     */
    @Override
    public void reset() {
        setActive(false);
        mSlotArray = new Peg[mNumSlots];

        super.reset();
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
     * Set background images of slots and hints in row depending on whether row is active.
     */
    private void setBackgroundImages(boolean active) {
        int imageResource;
        if (active) {
            imageResource = R.drawable.slot_active;
        } else {
            imageResource = R.drawable.slot;
        }

        // Set slot background
        LinearLayout ll = findViewById(R.id.slots_background_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            ImageView iv = (ImageView) ll.getChildAt(i);
            iv.setImageResource(imageResource);
        }

        // Set hint background
        GridLayout gl = findViewById(R.id.hints_background_gl);
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
                // Get index of drop-target-slot
                String tag = v.getTag().toString();
                int slotIndex = Utils.getSlotIndex(tag);

                // Get drop-target-slot foreground image
                ImageView targetSlot = mSlotIVList.get(slotIndex).second;
                Drawable draggedDrawable = mDraggedPeg.getDrawable();
                targetSlot.setImageDrawable(draggedDrawable);

                // Do not remove the draggedIV if it was a source peg or it is dropped at the spot
                // from where it was dragged.
                String draggedTag = mDraggedPeg.getImageView().getTag().toString();
                if (draggedTag.startsWith("slot") && Utils.getLastChar(tag) != Utils.getLastChar(draggedTag)) {
                    mDraggedPeg.getImageView().setImageDrawable(null);
                }

                mDraggedPeg.setImageView(targetSlot);
                mSlotArray[slotIndex] = mDraggedPeg;

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

                    // If the drag was started from a slot discard the peg
                    if (mDraggedPeg.getImageView().getTag().toString().startsWith("slot")) {
                        mDraggedPeg.getImageView().setImageDrawable(null);
                        slotIndex = Utils.getSlotIndex(mDraggedPeg.getImageView().getTag().toString());
                        mSlotArray[slotIndex] = null;
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
            // Only allow drag if row is active and slot contains a peg
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
            int slotIndex = Utils.getSlotIndex(v.getTag().toString());
            Peg draggedPeg = mSlotArray[slotIndex];
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
        for (Pair<ImageView, ImageView> pair : mSlotIVList) {
            if (pair.second.getDrawable() == null)
                return false;
        }
        return true;
    }

    /*
     * Get current code from peg array.
     */
    public int[] getCode() {
        int[] code = new int[mSlotArray.length];
        for (int i = 0; i < mSlotArray.length; i++) {
            code[i] = mSlotArray[i].getColor().getValue();
        }
        return code;
    }

    /*
     * Display hints according to result.
     */
    public void showHints(List<Integer> result) {
        GridLayout gl = findViewById(R.id.hints_foreground_gl);
        for (int i = 0; i < result.size(); i++) {
            ImageView iv = (ImageView) gl.getChildAt(i);
            if (result.get(i) == 2) {
                iv.setImageResource(R.drawable.peg_black);
            } else if (result.get(i) == 1) {
                iv.setImageResource(R.drawable.peg_white);
            }
        }
    }
}