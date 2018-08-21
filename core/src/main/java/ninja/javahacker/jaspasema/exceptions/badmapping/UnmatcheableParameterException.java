package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class UnmatcheableParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Parameter value do not matches anything in method's @Path value.";

    protected UnmatcheableParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter, MESSAGE_TEMPLATE);
    }

    public static UnmatcheableParameterException create(@NonNull Parameter parameter) {
        return new UnmatcheableParameterException(parameter);
    }
}
