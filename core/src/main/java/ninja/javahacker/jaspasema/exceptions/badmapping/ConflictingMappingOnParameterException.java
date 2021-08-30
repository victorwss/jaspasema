package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Thrown when two or more {@link ParamSource}-annotated annotations are defined on the same parameter.
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingMappingOnParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending parameter.
     * @param parameter The offending parameter.
     */
    public ConflictingMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
