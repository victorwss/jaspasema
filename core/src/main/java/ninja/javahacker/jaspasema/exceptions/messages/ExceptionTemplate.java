package ninja.javahacker.jaspasema.exceptions.messages;

import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ExceptionTemplate {

    @NonNull
    public String templateFor(@NonNull Class<? extends JaspasemaException> type);

    @NonNull
    public String nameFor(@NonNull TypeRestrictionViolationException.AllowedTypes type);

    @NonNull
    public String getReturningMethods();

    @NonNull
    public String getParameters();

    public static void setExceptionTemplate(@NonNull ExceptionTemplate template) {
        DefaultExceptionTemplate.ACTIVE.set(template);
    }

    public static ExceptionTemplate getExceptionTemplate() {
        return DefaultExceptionTemplate.ACTIVE.get();
    }
}