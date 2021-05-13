package logion.backend.annotation;

/**
 * <p>Commands are services managing transactions when updating aggregates.</p>
 *
 * <p>Commands are implemented with Spring beans, each method of the bean representing a command executed on a single
 * aggregate inside of a transaction. The bean class or its methods are generally annotated with
 * <code>@Transactional</code>.</p>
 *
 * <p>There are 3 types of commands:</p>
 *
 * <ul>
 * <li>Creation: consists in checking that a given aggregate does not already exists in the storage, then actually
 * saving it using the aggregate's repository.</li>
 * <li>Update: consists in retrieving an existing aggregate, calling on of its command then persisting it back to
 * storage.</li>
 * <li>Deletion: consists in removing an aggregate from storage, optionally after checking some consistency rule.</li>
 * </ul>
 *
 * @see Aggregate
 * @see AggregateRoot
 * @see Repository
 * @see Factory
 */
public @interface Commands {

}
