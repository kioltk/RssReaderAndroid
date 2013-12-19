package com.agcy.reader.core.Feedly.Comparators;

import com.agcy.reader.Models.Feedly.Entry;

import java.util.Comparator;

/**
 * Created by kiolt_000 on 18.12.13.
 */
public class EntryComparator {

    public static class byDateFirst implements Comparator<Entry> {
        @Override
        public int compare(Entry o1, Entry o2) {
            return o1.date().compareTo(o2.date());
        }
    }

    public static class byDateLast implements Comparator<Entry> {
        @Override
        public int compare(Entry o1, Entry o2) {
            return o2.date().compareTo(o1.date());
        }
    }
}