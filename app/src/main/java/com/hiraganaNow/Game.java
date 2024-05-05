package com.hiraganaNow;

import java.util.LinkedList;

public class Game {
    private static final LinkedList<Kana> currentKanaList = new LinkedList<>();
    private static final LinkedList<Kana> remainingKanaList = new LinkedList<>();
    private static final LinkedList<Kana> kanaLineupThisLevel = new LinkedList<>();
    private static final LinkedList<Kana> failedKanaList = new LinkedList<>();

    private static final int MAX_LIVES = 5;
    private static final int MAX_PASSES = 3;
    private static final int MIN_POWER_LEVEL = 1;
    private static final int STARTING_POWER_LEVEL = 3;

    private static Mode mode = null;
    private static Kana currentKana = null;
    private static int lives = 0;
    private static int passes = 0;
    private static int progress = 0;
    private static int maxProgressThisLevel = 0;
    private static int level = 0;
    private static int maxLevel = 0;
    private static int powerLevel = 0;
    private static boolean isThisTheFinalLevel = false;
    private static boolean isGameWon = false;

    public static String getCurrentKana() {
        return currentKana.character;
    }

    public static int getLevel() {
        return level;
    }

    public static int getLevelMax() {
        return maxLevel;
    }

    public static int getLives() {
        return lives;
    }

    public static int getLivesMax() {
        return MAX_LIVES;
    }

    public static int getPasses() {
        return passes;
    }

    public static int getPassesMax() {
        return MAX_PASSES;
    }

    public static int getPowerLevel() {
        return powerLevel;
    }

    public static int getProgress() {
        return progress;
    }

    public static int getProgressMax() {
        return maxProgressThisLevel;
    }

    public static boolean isPassFree() {
        return currentKana.isNewToPlayer;
    }

    public static boolean isFinalLevel() {
        return isThisTheFinalLevel;
    }

    public static boolean isGameWon() {
        return isGameWon;
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
        powerLevel = STARTING_POWER_LEVEL;
        progress = 0;
        level = 1;
        maxLevel = Kana.fullList.size();
        isThisTheFinalLevel = false;
        isGameWon = false;
        nextLevel();
        nextCharacter();
    }

    public static TestResult test(String input) {
        if(!Kana.isValidRomaji(input))
            return TestResult.INVALID;

        // Check whether the input is correct. //
        if(input.equals(currentKana.romaji)) {
            if(isPassFree()) {
                powerLevel ++;
            }
            currentKana.isNewToPlayer = false;
            nextCharacter();
            return isGameWon() ? TestResult.WIN_GAME :
                    isStartOfLevel() ? TestResult.LEVEL_UP : TestResult.SUCCESS;
        } else {
            lives--;
            reducePowerLevel();
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
        if(passes > 0 && !isPassFree()) {
            passes --;
            reducePowerLevel();
        } else if(!isPassFree()) {
            return null;
        }

        currentKana.isNewToPlayer = false;
        failedKanaList.add(currentKana);

        // Using a pass resets the final level marathon. //
        if(isThisTheFinalLevel)
            resetFinalLevel();

        return currentKana.romaji;
    }

    public static void cheat(int skips) {
        for(int i = 0; i < skips; i ++)
            test(currentKana.romaji);
    }

    private static void levelUp() {
        progress = 0;

        // HP //
        int lifeIncreaseAtLevelUp = lives < MAX_LIVES ? 1 : 0;

        // Passes //
        int passesAtLevelUp = passes < MAX_PASSES ? 1 : 0;

        // Power Level //
        powerLevel += 1;
        powerLevel += lives == MAX_LIVES ? 1 : 0;
        powerLevel += passes == MAX_PASSES ? 1 : 0;

        if(isThisTheFinalLevel){
            isGameWon = true;
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
        if(remainingKanaList.size() <= powerLevel) {
            isThisTheFinalLevel = true;
            currentKanaList.addAll(remainingKanaList);
            remainingKanaList.clear();
            resetFinalLevel();
        } else {
            // Randomly choose new kana to add this level. //
            LinkedList<Kana> newKana = new LinkedList<>();
            for(int i = 0; i < powerLevel; i ++) {
                Kana kana = ListUtils.removeRandom(remainingKanaList);
                newKana.add(kana);
            }
            currentKanaList.addAll(newKana);

            // Add three copies of each new kana to the lineup. //
            kanaLineupThisLevel.addAll(newKana);
            kanaLineupThisLevel.addAll(newKana);

            // Add one copy of each kana from the previous level. //
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

        level = currentKanaList.size();
        maxProgressThisLevel = kanaLineupThisLevel.size();
    }

    private static void reducePowerLevel() {
        powerLevel = Math.max(MIN_POWER_LEVEL, powerLevel - 1);
    }

    private static void resetFinalLevel() {
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
        INVALID, FAILURE, SUCCESS, LEVEL_UP, WIN_GAME
    }
}
