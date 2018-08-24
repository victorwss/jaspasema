package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MissingPathException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    protected MissingPathException(/*@NonNull*/ Method method) {
        super(method, "Missing mandatory @Path annotation.");
    }

    public static MissingPathException create(@NonNull Method method) {
        return new MissingPathException(method);
    }
}
