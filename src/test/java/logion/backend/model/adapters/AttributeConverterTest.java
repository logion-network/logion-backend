package logion.backend.model.adapters;

import javax.persistence.AttributeConverter;
import logion.backend.model.Ss58Address;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AttributeConverterTest<A, C> {

    protected void givenEntityAttribute(A value) {
        attribute = value;
    }

    private A attribute;

    protected void whenConvertingToDatabaseColumn() {
        column = converter.convertToDatabaseColumn(attribute);
    }

    private AttributeConverter<A, C> converter = converter();

    protected abstract AttributeConverter<A, C> converter();

    private C column;

    protected void thenDatabaseColumn(String value) {
        assertThat(column, equalTo(value));
    }

    protected void givenDatabaseColumn(C value) {
        column = value;
    }

    protected void whenConvertingToEntityAttribute() {
        attribute = converter.convertToEntityAttribute(column);
    }

    protected void thenEntityAttribute(Ss58Address value) {
        assertThat(attribute, equalTo(value));
    }
}
