package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate other annotations stating that they represent a particular HTTP verb and can themselves be used
 * to annotate methods in order to bind them to the HTTP verb.
 * @author Victor Williams Stafusa da Silva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface HttpMethod {

    /**
     * The name of the HTTP verb if it is not the same as the simple name of the annotated annotation.
     * @return The name of the HTTP verb if it is not the same as the simple name of the annotated annotation.
     */
    public String value() default "";
}
