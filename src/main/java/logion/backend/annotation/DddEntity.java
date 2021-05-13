package logion.backend.annotation;

/**
 * <p>Entities are objects which represent a thread of continuity and identity, going through a life-cycle.</p>
 *
 * <p>A DDD Entity is generally implemented with a JPA entity (i.e., annotated with <code>@javax.Entity</code>).
 * Most of the time, only getters have public visibility, mutating the state of an entity being handled by
 * specific commands exposed in the form of public methods.</p>
 *
 * <p>Commands must check preconditions before actually mutating the state. The execution of a command always
 * leaves the entity in a valid state.</p>
 *
 * <p>The fields of an entity are package protected, leaving
 * the possibility to the factory (for an aggregate root) or the aggregate root (for an inner entity)
 * to alter an entity's state.</p>
 */
@DomainDrivenDesign
public @interface DddEntity {

}
