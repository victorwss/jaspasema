package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Represents a gone error (HTTP status code 410).
 * I.e., the client tried to access some resource, but the resource does not exists anymore, although it existed in the past.
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

    /**
     * The name of the entity that was deleted.
     */
    @NonNull
    private final String key;

    /**
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param entityType The type of the entity that was deleted.
     * @param key The name of the entity that was deleted.
     * @throws IllegalArgumentException If any of {@code method}, {@code entityType} or {@code key} are {@code null}.
     */
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

    /**
     * Provides the name of the entity that was deleted.
     * @return The name of the entity that was deleted.
     */
    @NonNull
    @TemplateField("KEY")
    public String getKey() {
        return key;
    }
}
