package com.prangesoftwaresolutions.mastermind.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    public enum Status {
        ONGOING, WON, LOST
    }

    private int mNumSlots;
    private int mNumColors;
    private int[] mCode;
    private Status mStatus;

    public Game(int numSlots, int numColors, boolean duplicateColors) {
        mNumSlots = numSlots;
        mNumColors = numColors;
        mCode = generateRandomCode(duplicateColors);
        mStatus = Status.ONGOING;
    }

    /*
     * Generate a random code.
     */
    private int[] generateRandomCode(boolean duplicateColors) {
        int[] code = new int[mNumSlots];

        if (!duplicateColors) {
            // Get a random code without double occurrences
            List<Integer> colorList = new ArrayList<>();
            for (int i = 0; i < mNumColors; i++) {
                colorList.add(i);
            }
            Collections.shuffle(colorList);
            for(int i = 0; i < mNumSlots; i++) {
                code[i] = colorList.get(i);
            }
        } else {
            // Get a random code with possible double occurrences
            for (int i = 0; i < mNumSlots; i++) {
                Random rand = new Random();
                int randomColor = rand.nextInt(mNumColors);
                code[i] = randomColor;
            }
        }
        return code;
    }

    /*
     * Compare given code against true code and return result.
     */
    public List<Integer> checkCode(int[] code) {
        // result format: [#correct_color_correct_slot, #correct_color_wrong_slot]
        List<Integer> result = new ArrayList<>();
        List<Integer> remaining = new ArrayList<>();
        List<Integer> remainingTrue = new ArrayList<>();

        // In a first iteration, check for correct color in correct slot
        for (int i = 0; i < mNumSlots; i++) {
            if (code[i] == mCode[i]) {
                result.add(2);
            } else {
                remaining.add(code[i]);
                remainingTrue.add(mCode[i]);
            }
        }

        // In the second iteration, check for correct color in wrong slot
        while (remaining.size() > 0) {
            int codePoint = remaining.get(0);
            if (remainingTrue.contains(codePoint)) {
                result.add(1);
                remainingTrue.remove(Integer.valueOf(codePoint));
            }
            remaining.remove(0);
        }

        return result;
    }

    public int[] getTrueCode() { return mCode; }

    public int getNumColors() { return mNumColors; }

    public int getNumSlots() { return mNumSlots; }

    public void setStatus(Status status) { mStatus = status; }

    public Status getStatus() { return mStatus; }

}
