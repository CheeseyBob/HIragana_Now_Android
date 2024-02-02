package com.hiraganaNow;

import java.util.LinkedList;

public class ListUtils {

    public static <X> X removeRandom(LinkedList<X> list){
        return list.remove((int)(Math.random()*list.size()));
    }

    public static <X> void shuffle(LinkedList<X> listToShuffle){
        LinkedList<X> shuffledList = new LinkedList<>();
        while(!listToShuffle.isEmpty()){
            shuffledList.add(removeRandom(listToShuffle));
        }
        listToShuffle.addAll(shuffledList);
    }
}
