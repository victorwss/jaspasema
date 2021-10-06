package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an annotation, tells which is the class responsible for processing parameters
 * annotated with that annotation.
 * @author Victor Williams Stafusa da Silva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ParamSource {

    /**
     * Tells which is the class responsible for processing parameters annotated with this annotation.
     * @return Which is the class responsible for processing parameters annotated with this annotation.
     */
    public Class<? extends ParamProcessor<?>> processor();
}
