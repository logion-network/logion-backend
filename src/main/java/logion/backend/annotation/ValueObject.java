package logion.backend.annotation;

/**
 * <p>Value Objects (VOs) are immutable objects with no conceptual identity.</p>
 *
 * <p>Encapsulation should be used to make a VO immutable.
 * VOs represented using regular classes can be annotated with Lombok's @Value and @Builder.
 * @Builder may be omitted for simple VOs wrapping
 * a single raw value. In that case, the constructor provided by @Value is sufficient.</p>
 */
@DomainDrivenDesign
public @interface ValueObject {

}
