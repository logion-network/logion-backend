package logion.backend.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * <p>A Factory builds new aggregates.</p>
 *
 * <p>Factories are implemented as Spring beans. Each aggregate has its own factory.
 * The public methods of the bean return a reference to the root of each newly created aggregate.
 * A collection of references may be returned if several aggregates are created at once.</p>
 *
 * <p>This annotation also serves as a specialization of {@link Component @Component},
 * allowing for implementation classes to be autodetected through classpath scanning.</p>
 *
 * @see Component
 * @see Aggregate
 * @see AggregateRoot
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@DomainDrivenDesign
public @interface Factory {

    @AliasFor(annotation = Component.class)
    String value() default "";
}
