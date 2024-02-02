package com.hiraganaNow;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ListUtils {

    public static <X> void addRandom(LinkedList<X> list, X item){
        int index = randomIndex(list);
        list.add(index, item);
    }

    public static int randomIndex(List<?> list) {
        return (int)(Math.random()*list.size());
    }

    public static <X> LinkedList<X> removeConsecutiveDuplicates(LinkedList<X> list){
        LinkedList<X> duplicates = new LinkedList<>();
        if (list.isEmpty())
            return duplicates;

        ListIterator<X> iterator = list.listIterator();
        X a = iterator.next(), b = null;
        while(iterator.hasNext()) {
            b = iterator.next();

            if(a.equals(b)) {
                iterator.remove();
                duplicates.add(b);
            } else {
                a = b;
            }
        }

        return duplicates;
    }

    public static <X> X removeRandom(LinkedList<X> list){
        int index = randomIndex(list);
        return list.remove(index);
    }

    public static <X> void shuffle(LinkedList<X> listToShuffle){
        LinkedList<X> shuffledList = new LinkedList<>();
        while(!listToShuffle.isEmpty()){
            shuffledList.add(removeRandom(listToShuffle));
        }
        listToShuffle.addAll(shuffledList);
    }
}
