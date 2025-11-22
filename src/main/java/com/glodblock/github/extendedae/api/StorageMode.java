package com.glodblock.github.extendedae.api;

public enum StorageMode {
    DEFAULT((value, threshold) -> true),
    GREATER_EQUAL((value, threshold) -> value >= threshold),
    GREATER((value, threshold) -> value > threshold),
    EQUAL((value, threshold) -> value == threshold),
    LESS((value, threshold) -> value < threshold),
    LESS_EQUAL((value, threshold) -> value <= threshold);

    private final AmountComparator comparator;

    StorageMode(AmountComparator comparator) {
        this.comparator = comparator;
    }

    public boolean test(long value, long threshold) {
        return this.comparator.compare(value, threshold);
    }

    interface AmountComparator {

        boolean compare(long value, long threshold);

    }

}
