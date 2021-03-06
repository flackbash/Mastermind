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

    public Peg(Color color, Context context) {
        mColor = color;
        mDrawable = getDrawable(context, mColor);
    }

    public Peg(String string, Context context) {
        mColor = getColor(string);
        mDrawable = getDrawable(context, mColor);
    }

    /*
     * Get color from peg string.
     */
    private static Color getColor(String string) {
        Color color;
        switch (string) {
            case "source_peg_0":
                color = Color.BLUE;
                break;
            case "source_peg_1":
                color = Color.VIOLET;
                break;
            case "source_peg_2":
                color = Color.RED;
                break;
            case "source_peg_3":
                color = Color.ORANGE;
                break;
            case "source_peg_4":
                color = Color.YELLOW;
                break;
            case "source_peg_5":
                color = Color.GREEN;
                break;
            case "source_peg_6":
                color = Color.TURQUOISE;
                break;
            case "source_peg_7":
                color = Color.LIGHT_BLUE;
                break;
            case "source_peg_8":
                color = Color.PURPLE;
                break;
            default:
                color = null;
        }
        return color;
    }

    /*
     * Get Peg drawable resource from color
     */
    public static Drawable getDrawable(Context context, Color color) {
        Drawable drawable;
        switch (color) {
            case BLUE:
                drawable =  context.getResources().getDrawable(R.drawable.peg_blue);
                break;
            case VIOLET:
                drawable =  context.getResources().getDrawable(R.drawable.peg_violet);
                break;
            case RED:
                drawable =  context.getResources().getDrawable(R.drawable.peg_red);
                break;
            case ORANGE:
                drawable =  context.getResources().getDrawable(R.drawable.peg_orange);
                break;
            case YELLOW:
                drawable =  context.getResources().getDrawable(R.drawable.peg_yellow);
                break;
            case GREEN:
                drawable =  context.getResources().getDrawable(R.drawable.peg_green);
                break;
            case TURQUOISE:
                drawable =  context.getResources().getDrawable(R.drawable.peg_turquoise);
                break;
            case LIGHT_BLUE:
                drawable =  context.getResources().getDrawable(R.drawable.peg_light_blue);
                break;
            case PURPLE:
                drawable =  context.getResources().getDrawable(R.drawable.peg_purple);
                break;
            default:
                drawable = null;
        }
        return drawable;
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
