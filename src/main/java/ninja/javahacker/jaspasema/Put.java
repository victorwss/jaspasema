package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ninja.javahacker.jaspasema.processor.HttpMethod;

/**
 * @author Victor Williams Stafusa da Silva
 */
@HttpMethod
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {
}
