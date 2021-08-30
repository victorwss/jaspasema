package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ResultSerializer {
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
