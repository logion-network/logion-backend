package logion.backend.model.adapters;

import javax.persistence.AttributeConverter;
import logion.backend.model.Ss58Address;

public class Ss58AddressConverter implements AttributeConverter<Ss58Address, String> {

    @Override
    public String convertToDatabaseColumn(Ss58Address attribute) {
        return attribute.getRawValue();
    }

    @Override
    public Ss58Address convertToEntityAttribute(String dbData) {
        return new Ss58Address(dbData);
    }
}
