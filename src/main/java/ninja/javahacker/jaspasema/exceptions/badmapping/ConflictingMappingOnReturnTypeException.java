package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ConflictingMappingOnReturnTypeException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Conflicting mapping on return type.";

    protected ConflictingMappingOnReturnTypeException(/*@NonNull*/ Method method) {
        super(method, MESSAGE_TEMPLATE);
    }

    public static ConflictingMappingOnReturnTypeException create(@NonNull Method method) {
        return new ConflictingMappingOnReturnTypeException(method);
    }
}
