package logion.backend.util;

import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CollectionMapperTest {

    @Test
    void mapArrayToSet() {
        assertThat(CollectionMapper.mapArrayToSet(Function.identity(), 1, 2, 3), is(Set.of(1, 2, 3)));
        assertThat(CollectionMapper.mapArrayToSet(x -> x + 1, 1, 2, 3), is(Set.of(2, 3, 4)));
        assertThat(CollectionMapper.mapArrayToSet(String::valueOf, 1, 2, 3), is(Set.of("1", "2", "3")));
    }

    @Test
    void mapSet() {
        assertThat(CollectionMapper.mapSet(Function.identity(), Set.of(1, 2, 3)), is(Set.of(1, 2, 3)));
        assertThat(CollectionMapper.mapSet(x -> x + 1, Set.of(1, 2, 3)), is(Set.of(2, 3, 4)));
        assertThat(CollectionMapper.mapSet(String::valueOf, Set.of(1, 2, 3)), is(Set.of("1", "2", "3")));
    }
}
