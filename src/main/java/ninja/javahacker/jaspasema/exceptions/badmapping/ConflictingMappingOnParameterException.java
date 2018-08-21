package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ConflictingMappingOnParameterException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Conflicting mapping on parameter.";

    protected ConflictingMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter, MESSAGE_TEMPLATE);
    }

    public static ConflictingMappingOnParameterException create(@NonNull Parameter parameter) {
        return new ConflictingMappingOnParameterException(parameter);
    }
}
