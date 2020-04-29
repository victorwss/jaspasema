package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class AlreadyExistsException extends HttpException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<?> entityType;

    @NonNull
    private final String key;

    public AlreadyExistsException(/*@NonNull*/ Method method, @NonNull Class<?> entityType, @NonNull String key) {
        super(method, 409);
        this.entityType = entityType;
        this.key = key;
    }

    @NonNull
    public Class<?> getEntityType() {
        return entityType;
    }

    @NonNull
    @TemplateField("TYPE")
    public String getEntityTypeName() {
        return entityType.getName();
    }

    @NonNull
    @TemplateField("KEY")
    public String getKey() {
        return key;
    }
}
