package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = CustomHandler.Container.class)
@ReturnSerializer(processor = CustomHandler.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomHandler {
    public String jQueryType() default "text";

    @ReturnSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public Class<? extends ExceptionRemapper> remapper();

    public static class Processor implements ReturnProcessor<CustomHandler> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull CustomHandler annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            ExceptionRemapper f;
            Class<? extends ExceptionRemapper> remapperClass = annotation.remapper();
            try {
                Constructor<? extends ExceptionRemapper> ctor = remapperClass.getConstructor();
                ctor.setAccessible(true);
                f = ctor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw UninstantiableRemapperException.create(method, remapperClass, e);
            } catch (InvocationTargetException e) {
                throw RemapperConstructorException.create(method, remapperClass, e.getCause());
            }
            f.validate(target, annotation, method);
            return new Stub<>(f::remap, annotation.jQueryType());
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public CustomHandler[] value();
    }

    public static interface ExceptionRemapper {
        public void remap(Request rq, Response rp, Object result);

        public default void validate(
                @NonNull ReifiedGeneric<?> target,
                @NonNull CustomHandler annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
        }
    }

    @Getter
    public static class UninstantiableRemapperException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE =
                "Couldn't instantiate the exception remapper $R$.";

        @NonNull
        private final Class<? extends CustomHandler.ExceptionRemapper> remapper;

        protected UninstantiableRemapperException(
                /*@NonNull*/ Method method,
                /*@NonNull*/ Class<? extends CustomHandler.ExceptionRemapper> remapper,
                /*@NonNull*/ Throwable cause)
        {
            super(method, MESSAGE_TEMPLATE.replace("$T$", remapper.getSimpleName()), cause);
            this.remapper = remapper;
        }

        public static UninstantiableRemapperException create(
                @NonNull Method method,
                @NonNull Class<? extends CustomHandler.ExceptionRemapper> remapper,
                @NonNull Throwable cause)
        {
            return new UninstantiableRemapperException(method, remapper, cause);
        }
    }

    @Getter
    public static class RemapperConstructorException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE =
                "Remapper constructor of class $R$ throwed an exception.";

        @NonNull
        private final Class<? extends CustomHandler.ExceptionRemapper> remapper;

        protected RemapperConstructorException(
                /*@NonNull*/ Method method,
                /*@NonNull*/ Class<? extends CustomHandler.ExceptionRemapper> remapper,
                /*@NonNull*/ Throwable cause)
        {
            super(method, MESSAGE_TEMPLATE.replace("$T$", remapper.getSimpleName()), cause);
            this.remapper = remapper;
        }

        public static RemapperConstructorException create(
                @NonNull Method method,
                @NonNull Class<? extends CustomHandler.ExceptionRemapper> remapper,
                @NonNull Throwable cause)
        {
            return new RemapperConstructorException(method, remapper, cause);
        }
    }
}
