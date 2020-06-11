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
import android.widget.LinearLayout;

import com.prangesoftwaresolutions.mastermind.R;
import com.prangesoftwaresolutions.mastermind.interfaces.GameStatusEventListener;
import com.prangesoftwaresolutions.mastermind.logic.Game;
import com.prangesoftwaresolutions.mastermind.logic.Peg;
import com.prangesoftwaresolutions.mastermind.utils.Utils;
import com.prangesoftwaresolutions.mastermind.views.BoardRow;
import com.prangesoftwaresolutions.mastermind.views.SourcePegs;
import com.prangesoftwaresolutions.mastermind.views.TrueCodeRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GameStatusEventListener {

    // View related variables
    List<BoardRow> mBoardRowList;
    TrueCodeRow mTrueCodeRow;
    SourcePegs mSourcePegs;
    int mActiveRowIndex;

    // Game logic variables
    Game mGame;
    int mNumSlots;
    int mNumColors;

    // Preferences
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize shared preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNumSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        mNumColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));
        setLayout(mNumSlots);

        // Set source peg views
        mSourcePegs = findViewById(R.id.source_pegs);
        mSourcePegs.setPegs(this, mNumColors);

        // Set board rows
        initBoardRowList();

        // Activate first row
        mActiveRowIndex = 0;
        mBoardRowList.get(mActiveRowIndex).setActive(true);

        // Set true code row
        mTrueCodeRow = findViewById(R.id.true_code_row);

        // Initialize game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(mNumSlots, mNumColors, duplicateColors);
    }

    @Override
    protected void onRestart() {
        int numSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        int numColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));
        if (numSlots != mNumSlots || numColors != mNumColors) {
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
                mBoardRowList.get(mActiveRowIndex).setDraggedPeg(draggedPeg);
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

    void setLayout(int numSlots) {
        int boardLayout;
        switch (numSlots) {
            case 3:
                boardLayout = R.layout.board_3;
                break;
            case 4:
                boardLayout = R.layout.board_4;
                break;
            case 5:
                boardLayout = R.layout.board_5;
                break;
            case 6:
            default:
                boardLayout = R.layout.board_6;
                break;
        }
        setContentView(boardLayout);
    }

    void initBoardRowList() {
        mBoardRowList = new ArrayList<>();
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
    }

    void restartGame() {
        // Reset board
        mTrueCodeRow.reset();
        for (BoardRow br : mBoardRowList) {
            br.reset();
        }

        // Set new layout in case number of slots has changed
        int numSlots = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_slots_key), getString(R.string.settings_number_slots_default)));
        int numColors = Integer.parseInt(mPreferences.getString(getString(R.string.settings_number_colors_key), getString(R.string.settings_number_colors_default)));

        if (numSlots != mNumSlots || numColors != mNumColors) {
            mNumSlots = numSlots;
            mNumColors = numColors;
            setLayout(mNumSlots);
            mSourcePegs.setPegs(this, mNumColors);
            initBoardRowList();
        }

        // Start new game
        boolean duplicateColors = mPreferences.getBoolean(getString(R.string.settings_duplicate_colors_key), Boolean.getBoolean(getString(R.string.settings_duplicate_colors_default)));
        mGame = new Game(mNumSlots, mNumColors, duplicateColors);

        // Set up new board
        mActiveRowIndex = 0;
        mBoardRowList.get(mActiveRowIndex).setActive(true);
    }
}
