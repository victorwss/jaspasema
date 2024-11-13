package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param entityType The type of the entity that was found.
     * @param key The name of the entity that already exists.
     * @throws IllegalArgumentException If {@code method}, {@code entityType} or {@code key} are {@code null}.
     */
    public AlreadyExistsException(/*@NonNull*/ Method method, @NonNull Class<?> entityType, @NonNull String key) {
        super(method, 409);
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
