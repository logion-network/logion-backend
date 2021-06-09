package logion.backend.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectionMapper {

    public static <T, R> Set<R> mapArrayToSet(Function<T, R> mappingFunction, T... from) {
        return mapCollection(mappingFunction, Set.of(from), Collectors.toSet());
    }

    public static <T, R> Set<R> mapSet(Function<T, R> mappingFunction, Collection<T> from) {
        return mapCollection(mappingFunction, from, Collectors.toSet());
    }

    public static <T, R> List<R> mapList(Function<T, R> mappingFunction, Collection<T> from) {
        return mapCollection(mappingFunction, from, Collectors.toList());
    }

    public static <T, R, C> C mapCollection(Function<T, R> mappingFunction, Collection<T> from, Collector<? super R, ?, C> collector) {
        return from.stream()
                .map(mappingFunction)
                .collect(collector);
    }

    private CollectionMapper() {
    }
}
