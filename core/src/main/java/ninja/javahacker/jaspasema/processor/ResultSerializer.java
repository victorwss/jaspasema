package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an annotation, tells which is the class responsible for processing return values of methods
 * annotated with that annotation.
 * @author Victor Williams Stafusa da Silva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ResultSerializer {

    /**
     * Tells which is the class responsible for processing return values of methods annotated with this annotation.
     * @return Which is the class responsible for processing return values of methods annotated with this annotation.
     */
    public Class<? extends ResultProcessor<?, ?>> processor();

    /**
     * Used to tell which method inside an annotation gives the exception type that triggers its behaviour.
     * @author Victor Williams Stafusa da Silva
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExitDiscriminator {
    }
}
