package logion.backend.annotation;

/**
 * <p>View objects describe the JSON payload received in an HTTP request body or returned in an HTTP response body.</p>
 *
 * <p>They are generally implemented by classes annotated with Lombok's {@link lombok.Value} and {@link lombok.Builder}
 * annotations (for response views only). This enables writing cleaner code when it comes to building views. Also, the
 * generated getters and setters are properly handled by most JSON serialization/deserialization tools.</p>
 *
 * <p>Views are documented with Swagger annotations ({@link io.swagger.annotations.ApiModel}, etc.) in order to enrich
 * the generated REST API documentation.</p>
 */
public @interface View {

}
