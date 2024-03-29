package ninja.javahacker.jaspasema.exceptions.http;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
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
    public EntityDeletedException(@NonNull Class<?> entityType, @NonNull Object key) {
        super(410);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Constructs an instance specifiying which entity caused this exception with a custom message.
     * @param entityType The type of the entity that was deleted.
     * @param key The name of the entity that already exists.
     * @param message The detail message.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} is {@code null}.
     */
    public EntityDeletedException(@NonNull Class<?> entityType, @NonNull Object key, /*@NonNull*/ String message) {
        super(410, message);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Creates a function that receives a {@code int} key and produces an instance of {@code EntityDeletedException}.
     * @param entityType The type of the entity that was deleted.
     * @return The aforementioned function.
     */
    public static IntFunction<EntityDeletedException> intKey(@NonNull Class<?> entityType) {
        return k -> new EntityDeletedException(entityType, k);
    }

    /**
     * Creates a function that receives a {@code long} key and produces an instance of {@code EntityDeletedException}.
     * @param entityType The type of the entity that was deleted.
     * @return The aforementioned function.
     */
    public static LongFunction<EntityDeletedException> longKey(@NonNull Class<?> entityType) {
        return k -> new EntityDeletedException(entityType, k);
    }

    /**
     * Creates a function that receives an object key and produces an instance of {@code EntityDeletedException}.
     * @param entityType The type of the entity that was deleted.
     * @return The aforementioned function.
     */
    public static Function<String, EntityDeletedException> key(@NonNull Class<?> entityType) {
        return k -> new EntityDeletedException(entityType, k);
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
