package com.prangesoftwaresolutions.mastermind.logic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.prangesoftwaresolutions.mastermind.R;

public class Peg {
    private Color mColor;
    private ImageView mImageView;
    private ImageView mLastImageView;
    private Drawable mDrawable;
    private Context mContext;

    public Peg(Color color, Context context) {
        mColor = color;
        mContext = context;
        mDrawable = getDrawable(mColor);
    }

    public Peg(String string, Context context) {
        mColor = getColor(string);
        mContext = context;
        mDrawable = getDrawable(mColor);
    }

    /*
     * Get Peg drawable resource from color
     */
    private Drawable getDrawable(Color color) {
        Drawable drawable;
        switch (color) {
            case BLUE:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_blue);
                break;
            case VIOLET:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_violet);
                break;
            case RED:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_red);
                break;
            case ORANGE:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_orange);
                break;
            case YELLOW:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_yellow);
                break;
            case GREEN:
                drawable =  mContext.getResources().getDrawable(R.drawable.peg_green);
                break;
            default:
                drawable = null;
        }
        return drawable;
    }

    /*
     * Get color from peg string.
     */
    private static Color getColor(String string) {
        Color color;
        switch (string) {
            case "peg_blue":
                color = Color.BLUE;
                break;
            case "peg_violet":
                color = Color.VIOLET;
                break;
            case "peg_red":
                color = Color.RED;
                break;
            case "peg_orange":
                color = Color.ORANGE;
                break;
            case "peg_yellow":
                color = Color.YELLOW;
                break;
            case "peg_green":
                color = Color.GREEN;
                break;
            default:
                color = null;
        }
        return color;
    }

    public void clearLastImageView() {
        mLastImageView = null;
    }

    public void setImageView(ImageView imageView) {
        mLastImageView = mImageView;
        mImageView = imageView;
    }

    public ImageView getImageView() { return mImageView; }

    public ImageView getLastImageView() { return mLastImageView; }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public Color getColor() { return mColor; }

}
