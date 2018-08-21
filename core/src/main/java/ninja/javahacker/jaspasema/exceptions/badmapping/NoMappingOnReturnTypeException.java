package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class NoMappingOnReturnTypeException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "No mapping on return type.";

    protected NoMappingOnReturnTypeException(/*@NonNull*/ Method method) {
        super(method, MESSAGE_TEMPLATE);
    }

    public static NoMappingOnReturnTypeException create(@NonNull Method method) {
        return new NoMappingOnReturnTypeException(method);
    }
}
