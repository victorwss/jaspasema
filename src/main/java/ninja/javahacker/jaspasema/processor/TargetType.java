package ninja.javahacker.jaspasema.processor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class TargetType<X> {

    private final Type generic;
    private final JavaType javaType;

    protected TargetType() {
        try {
            this.generic = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException | IndexOutOfBoundsException | NullPointerException e) {
            throw new IllegalStateException("The generic type is ill-defined.");
        }
        if (!(this.generic instanceof ParameterizedType) && !(this.generic instanceof Class)) {
            throw new IllegalStateException("The generic type should be instantiable.");
        }
        this.javaType = TypeFactory.defaultInstance().constructType(generic);
    }

    public TargetType(Type type) {
        this.generic = type;
        if (!(this.generic instanceof ParameterizedType) && !(this.generic instanceof Class)) {
            throw new IllegalStateException("The generic type should be instantiable.");
        }
        this.javaType = TypeFactory.defaultInstance().constructType(generic);
    }

    public static <X> TargetType<X> forClass(Class<X> klass) {
        return new TargetType<>(klass);
    }

    public static TargetType<?> forType(Type t) {
        return new TargetType<>(t);
    }

    public Type getGeneric() {
        return generic;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public boolean isSameOf(@NonNull TargetType<?> other) {
        return generic.equals(other.generic);
    }

    private static final List<Class<?>> SIMPLE_TYPES = Arrays.asList(
            boolean.class, Boolean.class, char.class, Character.class, float.class, Float.class, double.class, Double.class,
            byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
            String.class
    );

    @SuppressWarnings("element-type-mismatch")
    public boolean isSimple() {
        return SIMPLE_TYPES.contains(generic);
    }

    public boolean isListType() {
        if (!(generic instanceof ParameterizedType)) return false;
        ParameterizedType pt = (ParameterizedType) generic;
        return pt.getRawType() == List.class;
    }

    @SuppressWarnings("unchecked")
    public static <E> TargetType<E> getListGenericType(TargetType<List<E>> target) {
        if (!target.isListType()) throw new IllegalStateException();
        return (TargetType<E>) TargetType.forType(((ParameterizedType) target.generic).getActualTypeArguments()[0]);
    }

    @Override
    public String toString() {
        return "TargetType[" + generic + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TargetType)) return false;
        return Objects.equals(generic, ((TargetType) other).generic);
    }

    @Override
    public int hashCode() {
        return generic.hashCode();
    }
}
