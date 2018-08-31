package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class AlreadyExistsException extends HttpException {
    private static final long serialVersionUID = 1L;

    private final Class<?> entityType;

    private final String key;

    public AlreadyExistsException(/*@NonNull*/ Method method, @NonNull Class<?> entityType, @NonNull String key) {
        super(method, 409);
        this.entityType = entityType;
        this.key = key;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    @TemplateField("TYPE")
    public String getEntityTypeName() {
        return entityType.getName();
    }

    @TemplateField("KEY")
    public String getKey() {
        return key;
    }
}
