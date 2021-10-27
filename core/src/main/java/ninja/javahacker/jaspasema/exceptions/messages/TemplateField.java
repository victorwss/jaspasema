package ninja.javahacker.jaspasema.exceptions.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate methods in the exception that gives the value for some variable present in its template message.
 * Most exceptions have a fixed template message. However, it might contains a few variable parts.
 * <p>For example, a message like {@code "The method foo received the bad parameter bar."} might be used based on a template
 * message {@code "The method $M$ received the bad parameter $P$."}, where {@code "$M$"} and {@code "$P$"} are variables
 * which would be filled by calling some getters on the exception. The getters would be those respectively annotated
 * with {@code TemplateField("M")} and {@code TemplateField("P")}.</p>
 * @author Victor Williams Stafusa da Silva
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateField {

    /**
     * The name of the variable in the template message to which the annotated methods binds to.
     * @return The name of the variable in the template message to which the annotated methods binds to.
     */
    public String value();

}
