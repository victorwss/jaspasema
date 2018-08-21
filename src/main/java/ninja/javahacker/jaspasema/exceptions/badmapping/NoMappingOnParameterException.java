package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class NoMappingOnParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "No mapping on parameter.";

    protected NoMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter, MESSAGE_TEMPLATE);
    }

    public static NoMappingOnParameterException create(@NonNull Parameter parameter) {
        return new NoMappingOnParameterException(parameter);
    }
}
