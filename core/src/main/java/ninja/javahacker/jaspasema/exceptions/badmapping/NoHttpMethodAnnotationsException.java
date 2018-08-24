package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class NoHttpMethodAnnotationsException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "No @HttpMethod-annotated annotations on method.";

    protected NoHttpMethodAnnotationsException(/*@NonNull*/ Method method) {
        super(method, MESSAGE_TEMPLATE);
    }

    public static NoHttpMethodAnnotationsException create(@NonNull Method method) {
        return new NoHttpMethodAnnotationsException(method);
    }
}
