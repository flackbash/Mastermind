package com.prangesoftwaresolutions.mastermind.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;
import com.prangesoftwaresolutions.mastermind.logic.Game;
import com.prangesoftwaresolutions.mastermind.logic.Peg;
import com.prangesoftwaresolutions.mastermind.utils.Utils;
import com.prangesoftwaresolutions.mastermind.views.BoardRow;
import com.prangesoftwaresolutions.mastermind.views.TrueCodeRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GameStatusEventListener {

    Peg mDraggedPeg;
    int mActiveRowIndex;
    List<BoardRow> mBoardRowList = new ArrayList<>();
    TrueCodeRow mTrueCodeRow;
    Game mGame;
    SharedPreferences mPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set source peg views
        ImageView pegBlueIV = findViewById(R.id.source_peg_blue);
        ImageView pegVioletIV = findViewById(R.id.source_peg_violet);
        ImageView pegRedIV = findViewById(R.id.source_peg_red);
        ImageView pegOrangeIV = findViewById(R.id.source_peg_orange);
        ImageView pegYellowIV = findViewById(R.id.source_peg_yellow);
        ImageView pegGreenIV = findViewById(R.id.source_peg_green);
        pegBlueIV.setTag("peg_blue");
        pegVioletIV.setTag("peg_violet");
        pegRedIV.setTag("peg_red");
        pegOrangeIV.setTag("peg_orange");
        pegYellowIV.setTag("peg_yellow");
        pegGreenIV.setTag("peg_green");
        pegBlueIV.setOnTouchListener(this);
        pegVioletIV.setOnTouchListener(this);
        pegRedIV.setOnTouchListener(this);
        pegOrangeIV.setOnTouchListener(this);
        pegYellowIV.setOnTouchListener(this);
        pegGreenIV.setOnTouchListener(this);

        // Set board rows
        LinearLayout ll = findViewById(R.id.board_row_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View view = ll.getChildAt(i);
            if (view.getId() == R.id.true_code_row) {
                continue;
            }
            BoardRow br = (BoardRow) view;
            br.setListener(this);
            mBoardRowList.add(br);
        }
        Collections.reverse(mBoardRowList);

        // Activate first row
        mActiveRowIndex = 0;
        mBoardRowList.get(mActiveRowIndex).setActive(true);

        // Set true code row
        mTrueCodeRow = findViewById(R.id.true_code_row);

        // Initialize shared preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(4, 6, duplicateColors);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main:
                restartGame();
                return true;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Only allow drag actions if game is ongoing.
                if (mGame.getStatus() != Game.Status.ONGOING) {
                    break;
                }

                // Create drag data
                ClipData.Item item = new ClipData.Item(v.getTag().toString());
                ClipData dragData = new ClipData(v.getTag().toString(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

                // Create DragShadow
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

                // Start the drag
                v.startDrag(dragData, myShadow, null, 0);
                mDraggedPeg = new Peg(v.getTag().toString(), this);
                mDraggedPeg.setImageView((ImageView) v);
                mBoardRowList.get(mActiveRowIndex).setDraggedPeg(mDraggedPeg);
                mDraggedPeg.getImageView().setAlpha(0.7f);

                break;
            case MotionEvent.ACTION_UP:
                v.performClick();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRowComplete() {
        // Deactivate current row
        mBoardRowList.get(mActiveRowIndex).setActive(false);

        // Get result for current row and set hints accordingly
        int[] code = mBoardRowList.get(mActiveRowIndex).getCode();
        List<Integer> result = mGame.checkCode(code);
        mBoardRowList.get(mActiveRowIndex).showHints(result);

        if (Utils.sum(result) == mGame.getNumSlots() * 2) {
            // Game is won
            mTrueCodeRow.setPegs(mGame.getTrueCode(), this);
            mGame.setStatus(Game.Status.WON);
        } else if (mActiveRowIndex + 1 >= mBoardRowList.size()) {
            // Game is lost
            mTrueCodeRow.setPegs(mGame.getTrueCode(), this);
            mGame.setStatus(Game.Status.LOST);
        } else {
            // Game continues. Activate next row
            mActiveRowIndex ++;
            mBoardRowList.get(mActiveRowIndex).setActive(true);
        }
    }

    void restartGame() {
        // Reset board
        mTrueCodeRow.reset();
        for (BoardRow br : mBoardRowList) {
            br.reset();
        }

        // Start new game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(4, 6, duplicateColors);

        // Set up new board
        mActiveRowIndex = 0;
        mBoardRowList.get(mActiveRowIndex).setActive(true);
    }
}