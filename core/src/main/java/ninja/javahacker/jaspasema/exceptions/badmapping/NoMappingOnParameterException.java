package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;

/**
 * Thrown when a a mapping annotaton should be present in a parameter method, but there is no such annotation.
 * @author Victor Williams Stafusa da Silva
 */
public class NoMappingOnParameterException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending parameter.
     * @param parameter The offending parameter.
     * @throws IllegalArgumentException If {@link parameter} is {@code null}.
     */
    public NoMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
