package ninja.javahacker.jaspasema.exceptions.http;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ninja.javahacker.jaspasema.exceptions.messages.TemplateField;

/**
 * Represents an attempt to create a resource which already existed (HTTP status code 409).
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class AlreadyExistsException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * The type of the resource that was attempted to be created.
     * -- GETTER --
     * Provides the type of the resource that was attempted to be created.
     * @return The type of the resource that was attempted to be created.
     */
    @Getter
    @NonNull
    private final Class<?> entityType;

    /**
     * The name of the entity that already existed.
     */
    @NonNull
    private final String key;

    /**
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param entityType The type of the entity that was found.
     * @param key The name of the entity that already exists.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} is {@code null}.
     */
    public AlreadyExistsException(@NonNull Class<?> entityType, @NonNull String key) {
        super(409);
        this.entityType = entityType;
        this.key = key;
    }

    /**
     * Provides the name of the type of the resource that was attempted to be created.
     * @return The name of the type of the resource that was attempted to be created.
     */
    @NonNull
    @TemplateField("TYPE")
    public String getEntityTypeName() {
        return entityType.getName();
    }

    /**
     * Provides the name of the entity that already exists.
     * @return The name of the entity that already exists.
     */
    @NonNull
    @TemplateField("KEY")
    public String getKey() {
        return key;
    }
}
