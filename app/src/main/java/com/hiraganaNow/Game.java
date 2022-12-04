package com.hiraganaNow;

import java.util.LinkedList;
import java.util.ListIterator;

public class Game {
    private final static LinkedList<Kana> currentKanaList = new LinkedList<>();
    private final static LinkedList<Kana> remainingKanaList = new LinkedList<>();
    private final static LinkedList<Kana> kanaLineupThisLevel = new LinkedList<>();
    private final static LinkedList<Kana> failedKanaList = new LinkedList<>();

    private static Kana currentKana = null;
    private static int lives = 0;
    private static int passes = 0;
    private static int progress = 0;
    private static int level = 0;
    private static int maxLevel = 0;
    private static int hpAtLevelUp = 0;
    private static int passesAtLevelUp = 0;
    private static int freePassesUsed = 0;
    private static int nonFreePassesUsed = 0;
    private static int newKanaToAdd = 0;
    private static boolean isThisTheFinalLevel = false;

    private static final int STARTING_HP = 5;
    private static final int STARTING_PASSES = 3;
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

    public static int getPasses() {
        return passes;
    }

    public static int getProgress() {
        return progress;
    }

    public static void reset(Mode mode) {
        Kana.load(mode);
        currentKanaList.clear();
        remainingKanaList.clear();
        remainingKanaList.addAll(Kana.fullList);
        kanaLineupThisLevel.clear();
        failedKanaList.clear();
        lives = STARTING_HP;
        passes = STARTING_PASSES;
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
            return TestResult.SUCCESS;
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
        if(currentKana.isNewToPlayer) {
            currentKana.isNewToPlayer = false;
            freePassesUsed ++;
        } else if(passes > 0) {
            passes --;
            nonFreePassesUsed ++;
        } else {
            return null;
        }

        failedKanaList.add(currentKana);

        // Using a pass resets the final level marathon. //
        if(isThisTheFinalLevel)
            resetFinalLevel();

        return currentKana.romaji;
    }

    private static void levelUp() {
        // HP //
        hpAtLevelUp = lives < STARTING_HP ? 1 : 0;

        // Passes //
        passesAtLevelUp = passes < STARTING_PASSES ? 1 : 0;

        // New Kana //
        int unusedFreePasses = newKanaToAdd - freePassesUsed;
        newKanaToAdd = Math.max(2, newKanaToAdd + 1 - nonFreePassesUsed + unusedFreePasses);
        freePassesUsed = 0;
        nonFreePassesUsed = 0;
        progress = 0;

        /* TODO ...
        if(isThisTheFinalLevel){
            new GameWinEffect();
        } else {
            new LevelUpEffect();
        }
        */
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
                currentKanaList.add(removeRandom(remainingKanaList));
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
                shuffle(currentKanaList);
                kanaLineupThisLevel.addAll(currentKanaList);
            }

            // Add extra copies of hiragana that the player failed previously. //
            while(!failedKanaList.isEmpty()){
                Kana extraHira = removeRandom(failedKanaList);
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
    }

    private static void resetFinalLevel(){
        // One copy of each hiragana goes in the lineup, in a random order. //
        kanaLineupThisLevel.clear();
        shuffle(currentKanaList);
        kanaLineupThisLevel.addAll(currentKanaList);
        progress = 0;
    }

    private static <X> X removeRandom(LinkedList<X> list){
        return list.remove((int)(Math.random()*list.size()));
    }

    private static <X> void shuffle(LinkedList<X> listToShuffle){
        LinkedList<X> shuffledList = new LinkedList<>();
        while(!listToShuffle.isEmpty()){
            shuffledList.add(removeRandom(listToShuffle));
        }
        listToShuffle.addAll(shuffledList);
    }

    public enum Mode {
        HIRAGANA, KATAKANA
    }

    public enum TestResult {
        INVALID, FAILURE, SUCCESS
    }
}
