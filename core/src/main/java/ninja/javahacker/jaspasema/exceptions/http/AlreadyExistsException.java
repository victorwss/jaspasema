package ninja.javahacker.jaspasema.exceptions.http;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
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
     * Constructs an instance specifiying which entity caused this exception.
     * @param entityType The type of the entity that was found.
     * @param key The name of the entity that already exists.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} is {@code null}.
     */
    public AlreadyExistsException(@NonNull Class<?> entityType, @NonNull Object key) {
        super(409);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Constructs an instance specifiying which entity caused this exception with a custom message.
     * @param entityType The type of the entity that was found.
     * @param key The name of the entity that already exists.
     * @param message The detail message.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} is {@code null}.
     */
    public AlreadyExistsException(@NonNull Class<?> entityType, @NonNull Object key, /*@NonNull*/ String message) {
        super(409, message);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Creates a function that receives a {@code int} key and produces an instance of {@code AlreadyExistsException}.
     * @param entityType The type of the entity that was found.
     * @return The aforementioned function.
     */
    public static IntFunction<AlreadyExistsException> intKey(@NonNull Class<?> entityType) {
        return k -> new AlreadyExistsException(entityType, k);
    }

    /**
     * Creates a function that receives a {@code long} key and produces an instance of {@code AlreadyExistsException}.
     * @param entityType The type of the entity that was found.
     * @return The aforementioned function.
     */
    public static LongFunction<AlreadyExistsException> longKey(@NonNull Class<?> entityType) {
        return k -> new AlreadyExistsException(entityType, k);
    }

    /**
     * Creates a function that receives an object key and produces an instance of {@code AlreadyExistsException}.
     * @param entityType The type of the entity that was found.
     * @return The aforementioned function.
     */
    public static Function<String, AlreadyExistsException> key(@NonNull Class<?> entityType) {
        return k -> new AlreadyExistsException(entityType, k);
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
