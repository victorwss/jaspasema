package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines which is the path that should be routed to some method.
 *
 * <p>For example:</p>
 * <pre>
 *     &#64;Get
 *     &#64;Path("/foo")
 *     public String foo(
 *         &#64;HeaderParam String xxx, // Uses the content of the "xxx" header.
 *     {
 *         // Do stuff.
 *     }
 *
 *     &#64;Get
 *     &#64;Path("/bar")
 *     public String bar(
 *         &#64;HeaderParam String xxx, // Uses the content of the "xxx" header.
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

    /**
     * The path to route.
     * @return The path to route.
     */
    public String value();
}
