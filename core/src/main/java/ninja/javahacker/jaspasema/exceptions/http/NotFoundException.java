package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Represents a resource not found error (HTTP status code 404).
 * I.e., the client tried to access some resource or perform some operation, but the requested resource or operation do not exists.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class NotFoundException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * The type of the entity that was expected to be found.
     * -- GETTER --
     * Tells what is the type of the entity that was expected to be found.
     * @return The type of the entity that was expected to be found.
     */
    @Getter
    @NonNull
    private final Class<?> entityType;

    /**
     * The name of the entity that was expected to be found.
     */
    @NonNull
    private final String key;

    /**
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param entityType The type of the entity that was expected to be found.
     * @param key The name of the entity that was expected to be found.
     * @throws IllegalArgumentException If {@code method}, {@code entityType} or {@code key} are {@code null}.
     */
    public NotFoundException(/*@NonNull*/ Method method, @NonNull Class<?> entityType, @NonNull String key) {
        super(method, 404);
        this.entityType = entityType;
        this.key = key;
    }

    /**
     * Provides the name of the type of the entity that was expected to be found.
     * @return The name of the type of the entity that was expected to be found.
     */
    @NonNull
    @TemplateField("TYPE")
    public String getEntityTypeName() {
        return entityType.getName();
    }

    /**
     * Provides what is the name of the entity that was expected to be found.
     * @return The name of the entity that was expected to be found.
     */
    @NonNull
    @TemplateField("KEY")
    public String getKey() {
        return key;
    }
}
