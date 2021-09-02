package ninja.javahacker.jaspasema.exceptions.badmapping;

import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.messages.ExceptionTemplate;

/**
 * Enumerates what are possibly allowed for specific annotations.
 * @author Victor Williams Stafusa da Silva
 */
public enum AllowedTypes {
    /** Means that only primitives, primitive wrappers, Strings and date/time are allowed. */
    SIMPLE,

    /** Means that only lists of primitive wrappers, Strings and date/time are allowed. */
    SIMPLE_LIST,

    /** Means that only primitives, primitive wrappers, Strings, date/time and lists of those are allowed. */
    SIMPLE_AND_LIST,

    /** Means that only date/time is allowed. */
    DATE_TIME,

    /** Means that only lists of date/time are allowed. */
    DATE_TIME_LIST,

    /** Means that only lists are allowed. */
    LIST,

    /** Means that only Request, Response and Session are allowed. */
    HTTP;

    /**
     * Gives a localized {@link String} representation of {@code this}.
     * @return A localized {@link String} representation of {@code this}.
     */
    @NonNull
    @Override
    public String toString() {
        return ExceptionTemplate.getExceptionTemplate().nameFor(this);
    }
}
