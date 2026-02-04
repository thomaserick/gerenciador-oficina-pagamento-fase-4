package com.fiap.pj.core.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> void instanceNonNullCollection(Set<T> current, Set<T> submitted) {
        if (nonNull(current) && nonNull(submitted)) {
            current.removeIf(value -> (!submitted.contains(value)));
            current.addAll(submitted);
        }
    }

    public static <T> Stream<T> nullSafeStream(Collection<T> collection) {
        if (collection == null) return Stream.empty();
        return collection.stream();
    }
}
