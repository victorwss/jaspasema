package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class UnmatcheableParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public UnmatcheableParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
