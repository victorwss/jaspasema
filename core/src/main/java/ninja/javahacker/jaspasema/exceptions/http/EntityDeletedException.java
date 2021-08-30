package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class EntityDeletedException extends HttpException {
    private static final long serialVersionUID = 1L;

    /**
     * The type of the resource that was deleted.
     * -- GETTER --
     * Provides the type of the resource that was deleted.
     * @return The type of the resource that was deleted.
     */
    @Getter
    @NonNull
    private final Class<?> entityType;

    @NonNull
    private final String key;

    public EntityDeletedException(/*@NonNull*/ Method method, @NonNull Class<?> entityType, @NonNull String key) {
        super(method, 410);
        this.entityType = entityType;
        this.key = key;
    }

    /**
     * Provides the name of the type of the resource that was deleted.
     * @return The name of the type of the resource that was deleted.
     */
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
