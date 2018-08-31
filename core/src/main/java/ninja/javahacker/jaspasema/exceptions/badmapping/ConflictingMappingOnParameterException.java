package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingMappingOnParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public ConflictingMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
