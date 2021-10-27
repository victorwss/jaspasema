package ninja.javahacker.jaspasema.exceptions.http;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ninja.javahacker.jaspasema.exceptions.messages.TemplateField;

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
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param entityType The type of the entity that was deleted.
     * @param key The name of the entity that was deleted.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} are {@code null}.
     */
    public EntityDeletedException(@NonNull Class<?> entityType, @NonNull String key) {
        super(410);
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
