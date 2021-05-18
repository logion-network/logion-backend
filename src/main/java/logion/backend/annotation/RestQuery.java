package logion.backend.annotation;

/**
 * <p>One generally expects a query on a REST resource to use the <code>GET</code> verb. However, using
 * <code>GET</code> comes with a couple of drawbacks:</p>
 *
 * <ul>
 *  <li>when the number of query parameters grows, grouping them in a single view cannot be achieved in a satisfactory
 *  way (you either have to
 *  <a href="https://nullbeans.com/how-to-configure-query-parameters-in-spring-controllers/#Using_Map_as_a_query_parameter">use a map</a>
 *  or <a href="https://stackoverflow.com/a/36650935/2703552">register a view and a converter</a>),
 *  </li>
 *  <li>it is not straightforward to represent complex queries requiring structure (like nested objects or arrays),</li>
 *  <li>by default, many caching mechanisms and tools consider it is safe to cache a <code>GET</code> request, which
 *  is not desirable in the context of most queries.</li>
 * </ul>
 *
 * <p>As a result, it was decided to use the PUT verb in order to represent queries on a resource.</p>
 *
 * <p>However, <code>GET</code> remains the first choice when it comes to access a uniquely identified object
 * or stable set of objects.</p>
 */
public @interface RestQuery {

}
