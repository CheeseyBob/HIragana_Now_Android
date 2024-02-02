package com.hiraganaNow;

import java.util.LinkedList;
import java.util.ListIterator;

public class Game {
    private final static LinkedList<Kana> currentKanaList = new LinkedList<>();
    private final static LinkedList<Kana> remainingKanaList = new LinkedList<>();
    private final static LinkedList<Kana> kanaLineupThisLevel = new LinkedList<>();
    private final static LinkedList<Kana> failedKanaList = new LinkedList<>();

    private static Mode mode = null;
    private static Kana currentKana = null;
    private static int lives = 0;
    private static int passes = 0;
    private static int progress = 0;
    private static int maxProgressThisLevel = 0;
    private static int level = 0;
    private static int maxLevel = 0;
    private static int lifeIncreaseAtLevelUp = 0;
    private static int passesAtLevelUp = 0;
    private static int freePassesUsed = 0;
    private static int nonFreePassesUsed = 0;
    private static int newKanaToAdd = 0;
    private static boolean isThisTheFinalLevel = false;

    public static final int MAX_LIVES = 5;
    public static final int MAX_PASSES = 3;
    private static final int STARTING_NEW_KANA = 2;

    public static String getCurrentKana() {
        return currentKana.character;
    }

    public static int getLevel() {
        return level;
    }

    public static int getLives() {
        return lives;
    }

    public static int getMaxLevel() {
        return maxLevel;
    }

    public static int getMaxProgress() {
        return maxProgressThisLevel;
    }

    public static int getPasses() {
        return passes;
    }

    public static int getProgress() {
        return progress;
    }

    public static boolean isPassFree() {
        return currentKana.isNewToPlayer;
    }

    public static boolean isStartOfLevel() {
        return progress == 1;
    }

    public static void reset() {
        reset(Game.mode);
    }

    public static void reset(Mode mode) {
        Game.mode = mode;
        Kana.load(mode);
        currentKanaList.clear();
        remainingKanaList.clear();
        remainingKanaList.addAll(Kana.fullList);
        kanaLineupThisLevel.clear();
        failedKanaList.clear();
        lives = MAX_LIVES;
        passes = MAX_PASSES;
        freePassesUsed = 0;
        nonFreePassesUsed = 0;
        newKanaToAdd = STARTING_NEW_KANA;
        progress = 0;
        level = 1;
        maxLevel = Kana.fullList.size();
        isThisTheFinalLevel = false;
        nextLevel();
        nextCharacter();
    }

    public static TestResult test(String input) {
        if(!Kana.isValidRomaji(input))
            return TestResult.INVALID;

        // Check whether the input is correct. //
        if(!input.equals(currentKana.romaji)) {
            lives--;
            failedKanaList.add(currentKana);

            // Failure resets the final level marathon. //
            if(isThisTheFinalLevel)
                resetFinalLevel();

            return TestResult.FAILURE;
        }

        currentKana.isNewToPlayer = false;
        nextCharacter();
        return isStartOfLevel() ? TestResult.LEVEL_UP : TestResult.SUCCESS;
    }

    /**
     * Consumes a pass (or free pass) and returns the romaji for the current kana. Returns null if
     * the player is unable to use a pass.
     */
    public static String usePass() {
        if(isPassFree()) {
            freePassesUsed ++;
        } else if(passes > 0) {
            passes --;
            nonFreePassesUsed ++;
        } else {
            return null;
        }

        currentKana.isNewToPlayer = false; // TODO - Replace this with a HashSet<Kana> of known kana.
        failedKanaList.add(currentKana);

        // Using a pass resets the final level marathon. //
        if(isThisTheFinalLevel)
            resetFinalLevel();

        return currentKana.romaji;
    }

    private static void levelUp() {
        // HP //
        lifeIncreaseAtLevelUp = lives < MAX_LIVES ? 1 : 0;

        // Passes //
        passesAtLevelUp = passes < MAX_PASSES ? 1 : 0;

        // New Kana //
        int unusedFreePasses = newKanaToAdd - freePassesUsed;
        newKanaToAdd = Math.max(2, newKanaToAdd + 1 - nonFreePassesUsed + unusedFreePasses);
        freePassesUsed = 0;
        nonFreePassesUsed = 0;
        progress = 0;

        if(isThisTheFinalLevel){
            // TODO ...
            //new GameWinEffect();
        } else {
			nextLevel();
            if(lifeIncreaseAtLevelUp > 0){
                lives += lifeIncreaseAtLevelUp;
                lifeIncreaseAtLevelUp = 0;
            }
            if(passesAtLevelUp > 0){
                passes += passesAtLevelUp;
                passesAtLevelUp = 0;
            }
            nextCharacter();
        }
    }

    private static void nextCharacter() {
        if(kanaLineupThisLevel.isEmpty()){
            levelUp();
        } else {
            currentKana = kanaLineupThisLevel.remove();
            progress ++;
        }
    }

    private static void nextLevel() {
        for(int i = 0; i < newKanaToAdd; i ++){
            if(!remainingKanaList.isEmpty()){
                currentKanaList.add(ListUtils.removeRandom(remainingKanaList));
            } else {
                isThisTheFinalLevel = true;
                break;
            }
        }
        level = currentKanaList.size();

        if(isThisTheFinalLevel){
            resetFinalLevel();
        } else {
            // Add a copies of each hiragana to the lineup in a random order, twice. //
            for(int i = 0; i < 2; i ++){
                ListUtils.shuffle(currentKanaList);
                kanaLineupThisLevel.addAll(currentKanaList);
            }

            // Add extra copies of hiragana that the player failed previously. //
            while(!failedKanaList.isEmpty()){
                Kana extraHira = ListUtils.removeRandom(failedKanaList);
                int index = (int)(Math.random()*kanaLineupThisLevel.size());
                kanaLineupThisLevel.add(index, extraHira);
            }

            // Swap out any doubles. //
            ListIterator<Kana> li = kanaLineupThisLevel.listIterator();
            while(li.hasNext()){
                Kana h1 = li.next();
                if(!li.hasNext()){
                    break;
                }
                Kana h2 = li.next();
                // If h1 and h2 are the same, we swap h2 with its successor, if possible. //
                if(h1.equals(h2) && li.hasNext()){
                    // Remove h2 from the list. //
                    li.remove();
                    // Move cursor to the right of h2's successor (now h1's successor). //
                    li.next();
                    // Add h2 back in to the list. //
                    li.add(h2);
                } else {
                    // Move back a step so we can continue by checking h2 and h2.next. //
                    li.previous();
                }
            }
        }

        maxProgressThisLevel = kanaLineupThisLevel.size();
    }

    private static void resetFinalLevel(){
        // One copy of each hiragana goes in the lineup, in a random order. //
        kanaLineupThisLevel.clear();
        ListUtils.shuffle(currentKanaList);
        kanaLineupThisLevel.addAll(currentKanaList);
        progress = 0;
    }

    public enum Mode {
        HIRAGANA, KATAKANA
    }

    public enum TestResult {
        INVALID, FAILURE, SUCCESS, LEVEL_UP
    }
}
