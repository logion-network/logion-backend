package logion.backend.model.adapters;

import javax.persistence.AttributeConverter;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699") // Assertions in superclass
class Ss58AddressConverterTest extends AttributeConverterTest<Ss58Address, String> {

    @Test
    void convertsToDatabase() {
        givenEntityAttribute(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"));
        whenConvertingToDatabaseColumn();
        thenDatabaseColumn("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
    }

    @Test
    void convertsToAttribute() {
        givenDatabaseColumn("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        whenConvertingToEntityAttribute();
        thenEntityAttribute(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"));
    }

    @Test
    void convertsNullToDatabase() {
        givenEntityAttribute(null);
        whenConvertingToDatabaseColumn();
        thenDatabaseColumn(null);
    }

    @Test
    void convertsNullToAttribute() {
        givenDatabaseColumn(null);
        whenConvertingToEntityAttribute();
        thenEntityAttribute(null);
    }

    @Override
    protected AttributeConverter<Ss58Address, String> converter() {
        return new Ss58AddressConverter();
    }
}
