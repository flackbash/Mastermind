package com.prangesoftwaresolutions.mastermind.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

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

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;
import com.prangesoftwaresolutions.mastermind.logic.Game;
import com.prangesoftwaresolutions.mastermind.logic.Peg;
import com.prangesoftwaresolutions.mastermind.utils.Utils;
import com.prangesoftwaresolutions.mastermind.views.Board;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GameStatusEventListener {

    // View related variables
    Board mBoard;

    // Game logic variables
    Game mGame;
    int mNumSlots;
    int mNumColors;
    int mNumRows;

    // Preferences
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize shared preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNumSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        mNumColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));
        mNumRows = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_rows_key), getString(R.string.settings_number_rows_default)));

        // Initialize board
        mBoard = findViewById(R.id.board);
        mBoard.initializeBoard(this, mNumSlots, mNumColors, mNumRows);

        // Initialize game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(mNumSlots, mNumColors, duplicateColors);
    }

    @Override
    protected void onRestart() {
        int numSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        int numColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));
        int numRows = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_rows_key), getString(R.string.settings_number_rows_default)));

        if (numSlots != mNumSlots || numColors != mNumColors || numRows != mNumRows) {
            recreate();
        }
        super.onRestart();
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
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
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
                Peg draggedPeg = new Peg(v.getTag().toString(), this);
                draggedPeg.setImageView((ImageView) v);
                mBoard.getActiveRow().setDraggedPeg(draggedPeg);
                draggedPeg.getImageView().setAlpha(0.7f);

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
        // Get result for current row and set hints accordingly
        int[] code = mBoard.getActiveRow().getCode();
        List<Integer> result = mGame.checkCode(code);
        mBoard.getActiveRow().showHints(result);

        if (Utils.sum(result) == mGame.getNumSlots() * 2) {
            // Game is won
            mBoard.getTrueCodeRow().setPegs(mGame.getTrueCode(), this);
            mBoard.deactivateRow();
            mGame.setStatus(Game.Status.WON);
        } else {
            // Try to activate the next row
            boolean nextRowActivated = mBoard.activateNextRow();
            if (!nextRowActivated) {
                // Game is lost
                mBoard.getTrueCodeRow().setPegs(mGame.getTrueCode(), this);
                mGame.setStatus(Game.Status.LOST);
            }
        }

    }

    void restartGame() {
        // Re-initialize board in case number of pegs, colors or rows has changed
        int numSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        int numColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));
        int numRows = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_rows_key), getString(R.string.settings_number_rows_default)));
        if (numSlots != mNumSlots || numColors != mNumColors || numRows != mNumRows) {
            mNumSlots = numSlots;
            mNumColors = numColors;
            mBoard.initializeBoard(this, mNumSlots, mNumColors, mNumRows);
        } else {
            mBoard.resetBoard();
        }

        // Start new game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(mNumSlots, mNumColors, duplicateColors);
    }
}
