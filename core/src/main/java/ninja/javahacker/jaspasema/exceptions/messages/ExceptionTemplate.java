package ninja.javahacker.jaspasema.exceptions.messages;

import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;

/**
 * Object used to configure and customize exception messages from Jaspasema.
 * @author Victor Williams Stafusa da Silva
 */
public interface ExceptionTemplate {

    /**
     * Gets the template message for some exception type.
     * @param type The exception type for searching the corresponding template.
     * @return The template message for some exception type, or a blank string if none was found.
     * @throws IllegalArgumentException If {@code type} is {@code null}.
     */
    @NonNull
    public String templateFor(@NonNull Class<? extends JaspasemaException> type);

    /**
     * Gets the name for an allowed annotation type.
     * @param type The allowed annotation type.
     * @return The template message for the allowed annotation type.
     * @throws IllegalArgumentException If {@code type} is {@code null}.
     */
    @NonNull
    public String nameFor(@NonNull TypeRestrictionViolationException.AllowedTypes type);

    /**
     * Gives the string {@code "returning methods"} by default or some other if a non-standard template was used.
     * @return The string {@code "returning methods"} by default or some other if a non-standard template was used.
     */
    @NonNull
    public String getReturningMethods();

    /**
     * Gives the string {@code "parameters"} by default or some other if a non-standard template was used.
     * @return The string {@code "parameters"} by default or some other if a non-standard template was used.
     */
    @NonNull
    public String getParameters();

    /**
     * Sets which is the active {@link ExceptionTemplate} instance.
     * @param template The {@link ExceptionTemplate} instance.
     * @throws IllegalArgumentException If {@code template} is {@code null}.
     */
    public static void setExceptionTemplate(@NonNull ExceptionTemplate template) {
        DefaultExceptionTemplate.ACTIVE.set(template);
    }

    /**
     * Provides the active {@link ExceptionTemplate} instance.
     * @return The active {@link ExceptionTemplate} instance.
     */
    public static ExceptionTemplate getExceptionTemplate() {
        return DefaultExceptionTemplate.ACTIVE.get();
    }

    /**
     * Provides the standard, fixed, default and immutable {@link ExceptionTemplate} instance featuring messages in English.
     * @return The standard {@link ExceptionTemplate} instance.
     */
    public static ExceptionTemplate getDefaultExceptionTemplate() {
        return EnglishExceptionTemplate.INSTANCE;
    }
}