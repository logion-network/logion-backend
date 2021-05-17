package logion.backend.annotation;

/**
 * <p>An Aggregate is a bounded cluster of Entities and Value Objects which defined its set of invariants.
 * The root of the aggregate is an Entity that may be referenced by external objects and
 * which enforces the invariants.</p>
 *
 * <p>An Aggregate is described by a package containing at least an aggregate root, a factory and a repository.
 * It is documented in the <code>package-info.java</code> file.</p>
 *
 * @see ValueObject
 * @see DddEntity
 * @see AggregateRoot
 * @see Factory
 * @see Repository
 */
@DomainDrivenDesign
public @interface Aggregate {

}
