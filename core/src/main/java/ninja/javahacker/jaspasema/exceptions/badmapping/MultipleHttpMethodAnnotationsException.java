package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MultipleHttpMethodAnnotationsException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Multiple @HttpMethod-annotated annotations on method.";

    protected MultipleHttpMethodAnnotationsException(/*@NonNull*/ Method method) {
        super(method, MESSAGE_TEMPLATE);
    }

    public static MultipleHttpMethodAnnotationsException create(@NonNull Method method) {
        return new MultipleHttpMethodAnnotationsException(method);
    }
}
