package com.hiraganaNow;

import java.util.LinkedList;

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
        if(input.equals(currentKana.romaji)) {
            currentKana.isNewToPlayer = false;
            nextCharacter();
            return isStartOfLevel() ? TestResult.LEVEL_UP : TestResult.SUCCESS;
        } else {
            lives--;
            failedKanaList.add(currentKana);

            // Failure resets the final level marathon. //
            if(isThisTheFinalLevel)
                resetFinalLevel();

            return TestResult.FAILURE;
        }
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

        currentKana.isNewToPlayer = false;
        failedKanaList.add(currentKana);

        // Using a pass resets the final level marathon. //
        if(isThisTheFinalLevel)
            resetFinalLevel();

        return currentKana.romaji;
    }

    private static void levelUp() {
        // HP //
        int lifeIncreaseAtLevelUp = lives < MAX_LIVES ? 1 : 0;

        // Passes //
        int passesAtLevelUp = passes < MAX_PASSES ? 1 : 0;

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
            }
            if(passesAtLevelUp > 0){
                passes += passesAtLevelUp;
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
            // Add two copies of each kana to the lineup. //
            kanaLineupThisLevel.addAll(currentKanaList);
            kanaLineupThisLevel.addAll(currentKanaList);

            // Add extra copies of kana that the player failed previously. //
            kanaLineupThisLevel.addAll(failedKanaList);
            failedKanaList.clear();

            // Shuffle the lineup. //
            ListUtils.shuffle(kanaLineupThisLevel);

            // Attempt to remove consecutive duplicates. //
            for(int i = 0; i < 5; i ++) {
                LinkedList<Kana> dupes = ListUtils.removeConsecutiveDuplicates(kanaLineupThisLevel);
                if(dupes.isEmpty())
                    break;
                while(!dupes.isEmpty())
                    ListUtils.addRandom(kanaLineupThisLevel, dupes.remove());
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
