package ninja.javahacker.jaspasema.exceptions.http;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ninja.javahacker.jaspasema.exceptions.messages.TemplateField;

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
     * Constructs an instance specifiying which entity caused this exception.
     * @param entityType The type of the entity that was expected to be found.
     * @param key The name of the entity that was expected to be found.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} are {@code null}.
     */
    public NotFoundException(@NonNull Class<?> entityType, @NonNull Object key) {
        super(404);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Constructs an instance specifiying which entity caused this exception with a custom message.
     * @param entityType The type of the entity that was expected to be found.
     * @param key The name of the entity that already exists.
     * @param message The detail message.
     * @throws IllegalArgumentException If {@code entityType} or {@code key} is {@code null}.
     */
    public NotFoundException(@NonNull Class<?> entityType, @NonNull Object key, /*@NonNull*/ String message) {
        super(404, message);
        this.entityType = entityType;
        this.key = String.valueOf(key);
    }

    /**
     * Creates a function that receives a {@code int} key and produces an instance of {@code NotFoundException}.
     * @param entityType The type of the entity that was expected to be found.
     * @return The aforementioned function.
     */
    public static IntFunction<NotFoundException> intKey(@NonNull Class<?> entityType) {
        return k -> new NotFoundException(entityType, k);
    }

    /**
     * Creates a function that receives a {@code long} key and produces an instance of {@code NotFoundException}.
     * @param entityType The type of the entity that was expected to be found.
     * @return The aforementioned function.
     */
    public static LongFunction<NotFoundException> longKey(@NonNull Class<?> entityType) {
        return k -> new NotFoundException(entityType, k);
    }

    /**
     * Creates a function that receives an object key and produces an instance of {@code NotFoundException}.
     * @param entityType The type of the entity that was expected to be found.
     * @return The aforementioned function.
     */
    public static Function<String, NotFoundException> key(@NonNull Class<?> entityType) {
        return k -> new NotFoundException(entityType, k);
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
